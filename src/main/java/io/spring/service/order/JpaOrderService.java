package io.spring.service.order;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaOrderStockRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.order.entity.OrderStock;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOrderService {
    private final JpaItitmmRepository jpaItitmmRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaTbOrderMasterRepository jpaTbOrderMasterRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaTbOrderHistoryRepository jpaTbOrderHistoryRepository;
    private final JpaPurchaseService jpaPurchaseService;
	private final JpaOrderStockRepository jpaOrderStockRepository;

    private final EntityManager em;

    // orderId, orderSeq를 받아 주문 상태를 변경해주는 함수
    @Transactional
    public void changeOrderStatus(String orderId, String orderSeq) {
        // orderId, orderSeq로 해당하는 TbOrderDetail 찾아오기
        TypedQuery<TbOrderDetail> query =
                em.createQuery("select td from TbOrderDetail td " +
                                "join fetch td.tbOrderMaster tm " +
                                "left join fetch td.ititmm it " +
                                "where td.orderId = ?1" +
                                "and td.orderSeq = ?2 "
                        , TbOrderDetail.class);
        query.setParameter(1, orderId).setParameter(2, orderSeq);
        TbOrderDetail tbOrderDetail = query.getSingleResult();
//        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        if(tbOrderDetail == null){
            log.debug("There is no TbOrderDetail of " + orderId + " and " + orderSeq);
            return;
        }
        String assortGb = tbOrderDetail.getAssortGb();
        if(assortGb.equals(StringFactory.getGbOne())){ // assortGb == '01' : 직구
            this.changeOrderStatusWhenDirect(tbOrderDetail);
        }
        else if(assortGb.equals(StringFactory.getGbTwo())){ // assortGb == '02' : 수입
            this.changeOrderStatusWhenImport(tbOrderDetail);
        }
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
        String domesticStorageId = tbOrderDetail.getStorageId(); // 주문자 현지(국내?) 창고 id (국내창고)

        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4(국내입고완료)인 애들의 list
        List<TbOrderDetail> domTbOrderDetailsC04 = tbOrderDetailsC04.stream().filter(x -> x.getStorageId().equals(domesticStorageId)).collect(Collectors.toList());
        long sumOfDomTbOrderDetailsC04 = domTbOrderDetailsC04.stream().map(x -> {if(x.getQty() == null){return 0l;}else {return x.getQty();}}).reduce((a,b) -> a+b).orElseGet(()->0l); // C04고 국내창고인 애들의 sum(domQty)
        // 국내창고 ititmc 불러오기
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // 국내창고 ititmt 불러오기
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());

        long sumOfDomQty = this.getSumOfItitmcQty(ItitmcQty.QTY,domItitmc);
        long sumOfDomShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,domItitmc);
        long sumOfDomTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,domItitmt);
        long sumOfDomTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,domItitmt);

        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomQty : " + sumOfDomQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomShipIndQty : " + sumOfDomShipIndQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempQty : " + sumOfDomTempQty);
        System.out.println("ㅡㅡㅡㅡㅡ sumOfDomTempIndQty : " + sumOfDomTempIndQty);
        // 1.국내재고, 2.국내입고예정
        // 입고예정이면 입고예정 data 생성.

        String statusCd;
        // 1. 국내재고
        if(sumOfDomQty - sumOfDomShipIndQty - sumOfDomTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){
            this.loopItitmc(domItitmc, tbOrderDetail);
            statusCd = StringFactory.getStrC04(); // 국내(현지)입고완료 : C04
        }
        // 2. 국내입고예정
        else if(sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
            statusCd = this.loopItitmt(domItitmt, tbOrderDetail);
        }
        else{
            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
        }
        this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
//        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
//                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4인 애들의 list
//        long sumOfTbOrderDetailsC04 = tbOrderDetailsC04.stream().map(x -> x.getQty()).reduce((a,b) -> a+b).get(); // C04인 애들의 sum(qty)
//        // assortId로 itasrt 찾아오기
//        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
//        String assortId = tbOrderDetail.getAssortId();
//        String itemId = tbOrderDetail.getItemId();
//        String storageId = itasrt.getStorageId();
//        // ititmm 불러오기
//        List<Ititmc> ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
//        // ititmt 불러오기
//        List<Ititmt> ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
//        long qty = ititmc == null? 0l:ititmc.getQty();
//        long shipIndQty = ititmc == null? 0l:ititmc.getShipIndicateQty();
//        long tempQty = ititmt == null? 0l:ititmt.getTempQty();
//        long tempIndQty = ititmt == null? 0l:ititmt.getTempIndicateQty();
//        String statusCd;
//        if(qty - shipIndQty - sumOfTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){ // (총 들어온 애들) - (이미 팔기로 예약된 애들) - (국내입고 완료 상태인 애들) > 0
//            statusCd = StringFactory.getStrC04(); // 입고완료 : C04
//        }
//        else if(tempQty - tempIndQty - tbOrderDetail.getQty() >= 0){
//            ititmt.setTempIndicateQty(tempIndQty + tbOrderDetail.getQty());
//            em.persist(ititmt);
//            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
//            boolean flag = jpaPurchaseService.makePurchaseData(tbOrderDetail, itasrt, ititmc, ititmt, StringFactory.getGbTwo());
//            if(!flag){
//                statusCd = StringFactory.getStrB01(); // 발주대기 : B01
//            }
//            else{
//                statusCd = StringFactory.getStrB02(); // 발주완료 : B02
//            }
//        }
//        else {
//            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
//        }
//        updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
    }
    
    /**
     * 수입(해외창고 -> 국내창고 -> 국내주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail) {
        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
        if(itasrt == null){
            log.debug("해당 상품 정보가 존재하지 않습니다.");
            return;
        }
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
		String domesticStorageId = tbOrderDetail.getStorageId(); // 주문자 현지(국내?) 창고 id (국내창고)
        String overseaStorageId = itasrt.getStorageId(); // 물건의 산지(?) 창고 id (해외창고)

        List<TbOrderDetail> tbOrderDetailsC01 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList()); // 주문코드가 CO1(해외입고완료)인 애들의 list
        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4(국내입고완료)인 애들의 list
//        List<TbOrderDetail> ovrsTbOrderDetailsC01 = tbOrderDetailsC01.stream().filter(x -> x.getStorageId().equals(overseaStorageId)).collect(Collectors.toList());
        List<TbOrderDetail> domTbOrderDetailsC04 = tbOrderDetailsC04.stream().filter(x -> x.getStorageId().equals(domesticStorageId)).collect(Collectors.toList());
//        long sumOfTbOrderDetailsC01 = tbOrderDetailsC01.stream().map(x -> {if(x.getQty() == null){return 0l;}else {return x.getQty();}}).reduce((a,b) -> a+b).orElseGet(()->0l); // C01인 애들의 sum(domQty) (창고 id는 불요)
        long sumOfDomTbOrderDetailsC04 = domTbOrderDetailsC04.stream().map(x -> {if(x.getQty() == null){return 0l;}else {return x.getQty();}}).reduce((a,b) -> a+b).orElseGet(()->0l); // C04고 국내창고인 애들의 sum(domQty)
        // 국내창고 ititmc 불러오기
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // 국내창고 ititmt 불러오기
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // 해외창고 ititmc 불러오기
        List<Ititmc> ovrsItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());
        // 해외창고 ititmt 불러오기
        List<Ititmt> ovrsItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());

//        long domQty = domItitmc == null? 0l:domItitmc.getQty();
//        long domShipIndQty = domItitmc == null? 0l:domItitmc.getShipIndicateQty();
//        long domTempQty = domItitmt == null? 0l:domItitmt.getTempQty();
//        long domTempIndQty = domItitmt == null? 0l:domItitmt.getTempIndicateQty();
//        long ovrsQty = ovrsItitmc == null? 0l:ovrsItitmc.getQty();
//        long ovrsShipIndQty = ovrsItitmc == null? 0l:ovrsItitmc.getShipIndicateQty();
//        long ovrsTempQty = ovrsItitmt == null? 0l:ovrsItitmt.getTempQty();
//        long ovrsTempIndQty = ovrsItitmt == null? 0l:ovrsItitmt.getTempIndicateQty();

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

        String statusCd;
        // 1. 국내재고
        if(sumOfDomQty - sumOfDomShipIndQty - sumOfDomTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){
            this.loopItitmc(domItitmc, tbOrderDetail);
            statusCd = StringFactory.getStrC04(); // 국내(현지)입고완료 : C04
        }
        // 2. 국내입고예정
        else if(sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
            statusCd = this.loopItitmt(domItitmt, tbOrderDetail);
        }
        // 3. 해외재고
        else if(sumOfOvrsQty - sumOfOvrsShipIndQty - sumOfDomTbOrderDetailsC04 - tbOrderDetail.getQty() > 0){
            this.loopItitmc(ovrsItitmc, tbOrderDetail);
            statusCd = StringFactory.getStrC01(); // 해외입고완료 : C01
        }
        // 4. 해외입고예정
        else if(sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() > 0){
            statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail);
        }
        else{
            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
        }
        // 1. 국내재고 확인 (tbOrderDetail의 storageId 확인)
//        if(domQty - domShipIndQty - sumOfDomTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){
//            statusCd = StringFactory.getStrC04(); // 국내(현지)입고완료 : C04
//        }
//        // 2. 국내입고예정 재고 확인
//        else if(domTempQty - domTempIndQty - tbOrderDetail.getQty() >= 0){
//            // ititmt 주문발주에 물량만큼 더하기
//            domItitmt.setTempIndicateQty(domTempIndQty + tbOrderDetail.getQty());
//            em.persist(domItitmt);
//            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
//            boolean flag = jpaPurchaseService.makePurchaseData(tbOrderDetail, itasrt, domItitmc, domItitmt, StringFactory.getGbTwo());
//            if(!flag){
//                statusCd = StringFactory.getStrB01(); // 발주대기 : B01
//            }
//            else{
//                statusCd = StringFactory.getStrC03(); // 이동지시완료 : C03
//            }
//        }
//        // 3. 해외재고 확인 (itasrt의 storageId 확인)
//        else if(ovrsQty - ovrsShipIndQty - sumOfTbOrderDetailsC01 - tbOrderDetail.getQty() >= 0){
//            statusCd = StringFactory.getStrC01(); // 해외입고완료 : C01
//        }
//        // 4. 해외입고예정 재고 확인
//        else if(ovrsTempQty - ovrsTempIndQty - tbOrderDetail.getQty() >= 0){
//            // ititmt 주문발주에 물량만큼 더하기
//            ovrsItitmt.setTempIndicateQty(ovrsTempIndQty + tbOrderDetail.getQty());
//            em.persist(ovrsItitmt);
//            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
//            boolean flag = jpaPurchaseService.makePurchaseData(tbOrderDetail, itasrt, ovrsItitmc, ovrsItitmt, StringFactory.getGbOne());
//            if(!flag){
//                statusCd = StringFactory.getStrB01(); // 발주대기 : B01
//            }
//            else{
//                statusCd = StringFactory.getStrB02(); // 발주완료 : B02
//            }
//        }
//        else {
//            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
//        }
        this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
    }

    /**
     * Ititmt list를 loop 돌면서 qty 관련 계산
     */
    private String loopItitmt(List<Ititmt> ititmtList, TbOrderDetail tbOrderDetail) {
        long orderQty = tbOrderDetail.getQty();
        for(Ititmt ititmt : ititmtList){
            if(ititmt.getTempQty() >= orderQty + ititmt.getTempIndicateQty()){
                ititmt.setTempIndicateQty(orderQty + ititmt.getTempIndicateQty());
                break;
            }
            else{
                orderQty = orderQty - (ititmt.getTempQty() - ititmt.getTempIndicateQty());
                ititmt.setTempIndicateQty(ititmt.getTempQty());
            }
        }
        boolean flag = jpaPurchaseService.makePurchaseData(tbOrderDetail);
        if(!flag){
            return StringFactory.getStrB01(); // 발주대기 : B01
        }
        else{
            return StringFactory.getStrC03(); // 이동지시완료 : C03
        }
    }

    /**
     * Ititmc list를 loop 돌면서 qty 관련 계산
     */
    private void loopItitmc(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail){
        long orderQty = tbOrderDetail.getQty();
        for(Ititmc ititmc : ititmcList){
            if(ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()){
                ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
                break;
            }
            else{
                orderQty = orderQty - (ititmc.getQty() - ititmc.getShipIndicateQty());
                ititmc.setShipIndicateQty(ititmc.getQty());
            }
        }
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


