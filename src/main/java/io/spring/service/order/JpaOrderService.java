package io.spring.service.order;

import io.spring.enums.DirectOrImport;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.deposit.JpaLsdpdsRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.goods.*;
import io.spring.jparepos.order.*;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.order.entity.*;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.service.deposit.JpaDepositService;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOrderService {
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaTbOrderHistoryRepository jpaTbOrderHistoryRepository;
    private final JpaPurchaseService jpaPurchaseService;
	private final JpaOrderStockRepository jpaOrderStockRepository;
    private final JpaOrderLogRepository jpaOrderLogRepository;
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaLsdpdsRepository jpaLsdpdsRepository;

    private final EntityManager em;

    // orderId, orderSeq를 받아 주문 상태를 변경해주는 함수
    @Transactional
    public void changeOrderStatus(String orderId, String orderSeq) {
        // orderId, orderSeq로 해당하는 TbOrderDetail 찾아오기
        log.debug("in changeOrderStatus ; orderId : " + orderId + ", orderSeq : " + orderSeq);
        TypedQuery<TbOrderDetail> query =
                em.createQuery("select td from TbOrderDetail td " +
                                "join fetch td.tbOrderMaster tm " +
                                "left join fetch td.ititmm it " +
                                "left join fetch td.itasrt itasrt " +
                                "where td.orderId = ?1" +
                                "and td.orderSeq = ?2 "
                        , TbOrderDetail.class);
        query.setParameter(1, orderId).setParameter(2, orderSeq);
        TbOrderDetail tbOrderDetail = query.getSingleResult();
        if(tbOrderDetail == null){
            log.debug("There is no TbOrderDetail of " + orderId + " and " + orderSeq);
            return;
        }
        else if(tbOrderDetail.getItitmm() == null){
            log.debug("There is no ititmm of orderId : " + orderId + " and orderSeq : " + orderSeq);
            return;
        }
        else if(tbOrderDetail.getItasrt() == null){
            log.debug("There is no itasrt of orderId : " + orderId + " and orderSeq : " + orderSeq);
            return;
        }
        Itasrt itasrt = tbOrderDetail.getItasrt();
        String prevStatus = tbOrderDetail.getStatusCd();
//        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        String assortGb = itasrt.getAssortGb();

        if(assortGb == null){ // add_goods인 경우 자체 assortGb가 존재하지 않아, 부모 상품의 것을 따른다.
            itasrt = this.getParentAssortGb(orderId, orderSeq, itasrt);
        }

        if(StringFactory.getGbOne().equals(assortGb)){ // assortGb == '01' : 직구
            this.changeOrderStatusWhenDirect(tbOrderDetail);
        }
        else if(StringFactory.getGbTwo().equals(assortGb)){ // assortGb == '02' : 수입
            this.changeOrderStatusWhenImport(tbOrderDetail);
        }

        this.saveOrderLog(prevStatus, tbOrderDetail);
    }

    /**
     * add_goods의 itasrt와 주문key를 받아 add_goods의 assortGb를 부모의 것으로 설정해주는 함수
     */
    private Itasrt getParentAssortGb(String orderId, String orderSeq, Itasrt itasrt) {
        TypedQuery<Itasrt> q = em.createQuery("select i from Itasrt i, TbOrderDetail td, IfOrderDetail id, IfGoodsMaster im " +
                "where td.channelOrderNo=id.channelOrderNo and td.channelOrderSeq=id.channelOrderSeq " +
                "and id.channelParentGoodsNo=im.goodsNo " +
                "and im.assortId=i.assortId " +
                "and td.orderId=?1 and td.orderSeq=?2",Itasrt.class).setParameter(1,orderId).setParameter(2,orderSeq);
        Itasrt parentItasrt = q.getSingleResult();
        itasrt.setAssortGb(parentItasrt.getAssortGb());
        return itasrt;
    }

    /**
     * tbOrderDetail.statusCd가 변동될 때마다 로그를 기록함.
     */
    private void saveOrderLog(String prevStatus, TbOrderDetail tbOrderDetail) {
        OrderLog orderLog = new OrderLog(tbOrderDetail);
        orderLog.setPrevStatus(prevStatus);
        jpaOrderLogRepository.save(orderLog);
    }
    
    /**
     * 직구(해외창고 -> 국내(현지)주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenDirect(TbOrderDetail tbOrderDetail) {
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
        String goodsStorageId = "000002";//tbOrderDetail.getStorageId(); // 물건이 도착하는 창고 id (해외창고. 직구이므로 국내창고 거치지 않음)

        // 국내창고 ititmc 불러오기
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, goodsStorageId, StringFactory.getStrEleven());
        // 국내창고 ititmt 불러오기
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, goodsStorageId, StringFactory.getStrEleven());

        long sumOfDomQty = this.getSumOfItitmcQty(ItitmcQty.QTY,domItitmc);
        long sumOfDomShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,domItitmc);
        long sumOfDomTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,domItitmt);
        long sumOfDomTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,domItitmt);

        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomQty : " + sumOfDomQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomShipIndQty : " + sumOfDomShipIndQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempQty : " + sumOfDomTempQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempIndQty : " + sumOfDomTempIndQty);
        // 1.해외재고, 2.해외입고예정
        // 입고예정이면 입고예정 data 생성.

        boolean isStockExist;

        String statusCd = null;
        // 1. 해외재고 있을 가능성이 있음
        if(sumOfDomQty - sumOfDomShipIndQty - tbOrderDetail.getQty() >= 0){
            isStockExist = this.loopItitmc(domItitmc, tbOrderDetail);
            statusCd = isStockExist? StringFactory.getStrC04() : statusCd; // 국내입고완료(해외지만 거기서 바로 쏘므로) : C04
            this.getLsdpsdListByGoodsInfo(tbOrderDetail).get(0); // 숫자 맞는 상품입고와 그 입고에 연결된 상품발주에 orderId와 orderSeq 적어넣기
        }
        // 2. 해외입고예정재고 있을 가능성이 있음
        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.direct); // 해외입고예정재고 있음 : B02 (발주완료), 없음 : B01 (발주대기)
        }
        // 3. 해외재고도 없고 해외입고예정재고도 없음
        else if(statusCd == null){
            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
        }
        this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
    }

    /**
     * orderStatus 판단시 assortId, itemId, qty로 lsdpsd를 가져오는 함수
     */
    private List<Lsdpsd> getLsdpsdListByGoodsInfo(TbOrderDetail tbOrderDetail){
        TypedQuery<Lsdpsd> q = em.createQuery("select lsdpsd from Lsdpsd lsdpsd " +
                        "join fetch lsdpsd.lspchd ld " +
                        "where lsdpsd.assortId=?1 and lsdpsd.itemId=?2 and lsdpsd.depositQty=?3",Lsdpsd.class)
                .setParameter(1, tbOrderDetail.getAssortId())
                .setParameter(2,tbOrderDetail.getItemId())
                .setParameter(3,tbOrderDetail.getQty());
        List<Lsdpsd> lsdpsdList = q.getResultList();
        Lsdpsd lsdpsd = lsdpsdList.get(0);

        Lspchd lspchd = lsdpsd.getLspchd();
        lsdpsd.setOrderId(tbOrderDetail.getOrderId());
        lsdpsd.setOrderSeq(tbOrderDetail.getOrderSeq());
        jpaLsdpsdRepository.save(lsdpsd);

        Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(), lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        lsdpds.setEffEndDt(new Date());
        Lsdpds newLsdpds = new Lsdpds(lsdpds);
        jpaLsdpdsRepository.save(lsdpds);
        jpaLsdpdsRepository.save(newLsdpds);

        lspchd.setOrderId(tbOrderDetail.getOrderId());
        lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
        jpaLspchdRepository.save(lspchd);

        return lsdpsdList;
    }
    
    /**
     * 수입(해외창고 -> 국내창고 -> 국내주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void  changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail) {
        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
        if(itasrt == null){
            log.debug("해당 상품 정보가 존재하지 않습니다.");
            return;
        }
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
		String domesticStorageId = tbOrderDetail.getStorageId(); // 주문자 현지(국내?) 창고 id (국내창고)
        String overseaStorageId = itasrt.getStorageId(); // 물건의 산지(?) 창고 id (해외창고)

        // 국내창고 ititmc 불러오기
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // 국내창고 ititmt 불러오기
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // 해외창고 ititmc 불러오기
        List<Ititmc> ovrsItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());
        // 해외창고 ititmt 불러오기
        List<Ititmt> ovrsItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());

        long sumOfDomQty = this.getSumOfItitmcQty(ItitmcQty.QTY,domItitmc);
        long sumOfDomShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,domItitmc);
        long sumOfDomTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,domItitmt);
        long sumOfDomTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,domItitmt);

        long sumOfOvrsQty = this.getSumOfItitmcQty(ItitmcQty.QTY,ovrsItitmc);
        long sumOfOvrsShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,ovrsItitmc);
        long sumOfOvrsTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,ovrsItitmt);
        long sumOfOvrsTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,ovrsItitmt);

        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomQty : " + sumOfDomQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomShipIndQty : " + sumOfDomShipIndQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempQty : " + sumOfDomTempQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempIndQty : " + sumOfDomTempIndQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfOvrsQty : " + sumOfOvrsQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfOvrsShipIndQty : " + sumOfOvrsShipIndQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfOvrsTempQty : " + sumOfOvrsTempQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfOvrsTempIndQty : " + sumOfOvrsTempIndQty);
        // 1.국내재고, 2.국내입고예정, 3.해외재고, 4.해외입고예정 확인 후 주문상태 변경.
        // 입고예정이면 입고예정 data 생성.

        String statusCd = null;
        // 1. 국내재고가 있을 가능성이 있음
        if(sumOfDomQty - sumOfDomShipIndQty - tbOrderDetail.getQty() >= 0){
            boolean isDomStockExist = this.loopItitmc(domItitmc, tbOrderDetail);
            statusCd = isDomStockExist? StringFactory.getStrC04() : statusCd; // 국내(현지)입고완료 : C04
        }
        // 2. 국내입고예정 재고가 있을 가능성이 있음
        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.imports);
        }
        // 3. 해외재고가 있을 가능성이 있음
        if(statusCd == null && sumOfOvrsQty - sumOfOvrsShipIndQty - tbOrderDetail.getQty() >= 0){
            this.loopItitmc(ovrsItitmc, tbOrderDetail);
            statusCd = StringFactory.getStrC01(); // 해외입고완료 : C01
        }
        // 4. 해외입고예정 재고가 있을 가능성이 있음
        if(statusCd == null && sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() >= 0){
            statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail, DirectOrImport.imports);
        }
        // 5. 아무것도 없음
        if(statusCd == null){
            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
        }
        this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
    }

    /**
     * Ititmt list를 loop 돌면서 qty 관련 계산
     */
    private String loopItitmt(List<Ititmt> ititmtList, TbOrderDetail tbOrderDetail, DirectOrImport di) {
        boolean isStockCandidateExist = false;
        long orderQty = tbOrderDetail.getQty();
        String goodsStorageId = null;
        for(Ititmt ititmt : ititmtList){
            if(ititmt.getTempQty() >= orderQty + ititmt.getTempIndicateQty()){
                ititmt.setTempIndicateQty(orderQty + ititmt.getTempIndicateQty());
                isStockCandidateExist = true;
                goodsStorageId = ititmt.getStorageId();
                break;
            }
        }
        if(isStockCandidateExist && di.equals(DirectOrImport.direct)){ // 직구
            jpaPurchaseService.makePurchaseDataByOrder(tbOrderDetail, di);
            return StringFactory.getStrB02(); // 발주완료 : B02
        }
        else if(isStockCandidateExist && di.equals(DirectOrImport.imports)){ // 수입
            String statusCd;
            if(!tbOrderDetail.getStorageId().equals(goodsStorageId)){ // 물건이 해외입고예정이라면
                statusCd = StringFactory.getStrB02(); // 발주완료 : B02
            }
            else { // 물건이 국내(주문자위치)입고예정이라면
                statusCd = StringFactory.getStrC03(); // 이동지시완료 : C03
            }
            jpaPurchaseService.makePurchaseDataByOrder(tbOrderDetail, di);
            return statusCd;
        }
        else{
            return StringFactory.getStrB01(); // 발주대기 : B01
        }
    }

    /**
     * Ititmc list를 loop 돌면서 qty 관련 계산
     */
    private boolean loopItitmc(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail){
        long orderQty = tbOrderDetail.getQty();
        for(Ititmc ititmc : ititmcList){
            if(ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()){
                ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
                return true;
            }
        }
        return false;
    }

    /**
     * JB
     * orderDetail의 orderStatusCd를 update할 때 orderDetail과 orderHistory를 한꺼번에 update 시켜주는 함수
     * @param orderId
     * @param orderSeq
     * @param statusCd
     */
	public void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        Date date = Utilities.getStringToDate(StringFactory.getDoomDay());
		List<TbOrderHistory> tohs = jpaTbOrderHistoryRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq, date);

		tod.setStatusCd(statusCd);

		Date newEffEndDate = new Date();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
		 }

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.getStringToDate(StringFactory.getDoomDay()));
        // 임시 코드
        toh.setRegId("1");
        toh.setUpdId("1");

		tohs.add(toh);

//        System.out.println(tod);
        TbOrderDetail t = jpaTbOrderDetailRepository.save(tod);
//        System.out.println(t);
        jpaTbOrderHistoryRepository.saveAll(tohs);
	}

	public List<OrderStock> getOrderStock() {
		return jpaOrderStockRepository.findAll();
		

	}

    public TbOrderDetail getOrderDetail(String orderId,String orderSeq){
        return jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,orderSeq);
    }

	@Transactional
	public void saveOrderStock(OrderStockMngInsertRequestData req) {

		System.out.println(req.getUserNm());

		if (req.getId() == "") {
			OrderStock os = new OrderStock(req);
			jpaOrderStockRepository.save(os);
		} else {
			OrderStock o = jpaOrderStockRepository.findById(Long.parseLong(req.getId())).orElse(null);

			if (o != null) {
				OrderStock os = new OrderStock(req);

				os.setId(o.getId());
				jpaOrderStockRepository.save(os);

			}

			/*
			 * o.setPurchaseVendor(req.getPurchaseVendor()); o.setBrand(req.getBrand());
			 * o.setModelNo(req.getModelNo()); o.setAssortNm(req.getAssortNm());
			 * o.setOptionInfo(req.getOptionInfo());
			 * o.setTextOptionInfo(req.getTextOptionInfo());
			 * 
			 * 
			 * if (req.getQty().length() > 0) {
			 * 
			 * o.setQty(Long.parseLong(req.getQty()));
			 * 
			 * 
			 * } if (req.getUnitAmt().length() > 0) { o.setUnitAmt(req.getUnitAmt() == "" ?
			 * null : Float.parseFloat(req.getUnitAmt())); } if (req.getOptionAmt().length()
			 * > 0) { o.setOptionAmt(Float.parseFloat(req.getOptionAmt()));
			 * 
			 * } if (req.getAmt().length() > 0) { o.setAmt(Float.parseFloat(req.getAmt()));
			 * } if (req.getDiscountRate().length() > 0) {
			 * o.setDiscountRate(Float.parseFloat(req.getDiscountRate())); } if
			 * (req.getRealAmt().length() > 0) {
			 * o.setRealAmt(Float.parseFloat(req.getRealAmt())); }
			 * 
			 * o.setDeliMethod(req.getDeliMethod());
			 * o.setRealDeliMethod(req.getRealDeliMethod()); o.setOrderNm(req.getOrderNm());
			 * o.setOrderId(req.getOrderId()); o.setOrderDt(req.getOrderDt());
			 * o.setOrderMemo(req.getOrderMemo()); o.setStockNo(req.getStockNo());
			 * o.setPurchaseNo(req.getPurchaseNo()); o.setPi(req.getPi());
			 * o.setEstimatedProductionDate(req.getEstimatedProductionDate());
			 * o.setEstimatedShipmentDate(req.getEstimatedShipmentDate());
			 * o.setEstimatedArrivalDate(req.getEstimatedArrivalDate());
			 * o.setExpectedDeliveryDate(req.getExpectedDeliveryDate());
			 * o.setBlNo(req.getBlNo()); o.setDeliveryPeriod(req.getDeliveryPeriod());
			 * o.setStatusCd(req.getStatusCd()); o.setCarrier(req.getCarrier());
			 * o.setPurchaseDt(req.getPurchaseDt()); o.setMemo(req.getMemo());
			 * o.setOrigin(req.getOrigin()); o.setGoogleDrive(req.getGoogleDrive());
			 */
		}
	}

    /**
     * ititmc 리스트의 qty 또는 shipIndQty의 총합을 반환하는 함수
     */
    private long getSumOfItitmcQty(ItitmcQty ititmcQty, List<Ititmc> ititmcList){
        long sum = 0l;
        if(ititmcQty.equals(ItitmcQty.QTY)){
            sum = ititmcList.stream().map(x->{if(x.getQty() == null){return 0l;}else{return x.getQty();}}).reduce((a,b)->a+b).orElseGet(() -> 0l);
        }
        else if(ititmcQty.equals(ItitmcQty.SHIPINDQTY)){
            sum = ititmcList.stream().map(x->{if(x.getShipIndicateQty() == null){return 0l;}else{return x.getShipIndicateQty();}}).reduce((a,b)->a+b).orElseGet(() -> 0l);
        }
        return sum;
    }

    /**
     * ititmt 리스트의 tempQty 또는 tempIndicateQty의 총합을 반환하는 함수
     */
    private long getSumOfItitmtQty(ItitmtQty ititmtQty, List<Ititmt> ititmtList){
        long sum = 0l;
        if(ititmtQty.equals(ItitmtQty.TEMPQTY)){
            sum = ititmtList.stream().map(x->{if(x.getTempQty() == null){return 0l;}else{return x.getTempQty();}}).reduce((a,b)->a+b).orElseGet(() -> 0l);
        }
        else if(ititmtQty.equals(ItitmtQty.TEMPINDQTY)){
            sum = ititmtList.stream().map(x->{if(x.getTempIndicateQty() == null){return 0l;}else{return x.getTempIndicateQty();}}).reduce((a,b)->a+b).orElseGet(() -> 0l);
        }
        return sum;
    }

    private enum ItitmcQty{
        QTY, SHIPINDQTY
    }

    private enum ItitmtQty{
        TEMPQTY, TEMPINDQTY
    }

}


