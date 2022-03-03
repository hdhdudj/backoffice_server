package io.spring.service.order;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spring.enums.DirectOrImport;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpdsRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.goods.JpaIfGoodsOptionRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.goods.JpaTmitemRepository;
import io.spring.jparepos.order.JpaIfOrderCancelRepository;
import io.spring.jparepos.order.JpaIfOrderDetailRepository;
import io.spring.jparepos.order.JpaIfOrderMasterRepository;
import io.spring.jparepos.order.JpaOrderLogRepository;
import io.spring.jparepos.order.JpaOrderStockRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.jparepos.purchase.JpaLspchbRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.IfGoodsOption;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.entity.Tmitem;
import io.spring.model.order.entity.IfOrderCancel;
import io.spring.model.order.entity.IfOrderDetail;
import io.spring.model.order.entity.IfOrderMaster;
import io.spring.model.order.entity.OrderLog;
import io.spring.model.order.entity.OrderStock;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.model.order.response.OrderStatusWatingItemListResponseData;
import io.spring.model.purchase.entity.Lspchb;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.service.nhncloud.KakaoBizMessageService;
import io.spring.service.nhncloud.SmsService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final JpaLspchbRepository jpaLspchbRepository;

    private final JpaLsshpsRepository jpaLsshpsRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;

	private final JpaIfGoodsOptionRepository jpaIfGoodsOptionRepository;

	private final JpaTmitemRepository jpaTmitemRepository;

	private final JpaItitmmRepository jpaItitmmRepository;

	private final JpaIfOrderDetailRepository jpaIfOrderDetailRepository;

	private final JpaIfOrderMasterRepository jpaIfOrderMasterRepository;

	private final JpaTbOrderMasterRepository jpaTbOrderMasterRepository;

	private final JpaIfOrderCancelRepository jpaIfOrderCancelRepository;

    private final EntityManager em;

    private final KakaoBizMessageService kakaoBizMessageService;
    private final SmsService smsService;

	private final JpaStockService jpaStockService;

    // orderId, orderSeq를 받아 주문 상태를 변경해주는 함수
    @Transactional
    public void changeOrderStatus(String orderId, String orderSeq) {
        // orderId, orderSeq로 해당하는 TbOrderDetail 찾아오기
        log.debug("in changeOrderStatus ; orderId : " + orderId + ", orderSeq : " + orderSeq);
        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq2(orderId, orderSeq);//query.getSingleResult();
        if(tbOrderDetail == null){
            log.debug("There is no TbOrderDetail of " + orderId + " and " + orderSeq);
            return;
        }
        else if(tbOrderDetail.getItitmm() == null){
            log.debug("There is no ititmm of orderId : " + orderId + " and orderSeq : " + orderSeq);
            return;
        }
        else if(tbOrderDetail.getItitmm().getItasrt() == null){
            log.debug("There is no itasrt of orderId : " + orderId + " and orderSeq : " + orderSeq);
            return;
        }
        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        String prevStatus = tbOrderDetail.getStatusCd();
//        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        String assortGb = itasrt.getAssortGb();

        if(assortGb == null){ // add_goods인 경우 자체 assortGb가 존재하지 않아, 부모 상품의 것을 따른다.
            itasrt = this.getParentAssortGb(orderId, orderSeq, itasrt);
        }

		System.out.println(assortGb);

        if(StringFactory.getGbOne().equals(assortGb)){ // assortGb == '01' : 직구
            this.changeOrderStatusWhenDirect(tbOrderDetail);
        }
        else if(StringFactory.getGbTwo().equals(assortGb)){ // assortGb == '02' : 수입
            this.changeOrderStatusWhenImport(tbOrderDetail);
        }

        this.saveOrderLog(prevStatus, tbOrderDetail);
    }

	// orderId, orderSeq를 받아 주문 상태를 변경해주는 함수
	@Transactional
	public void noOptionChangeOrderStatus(String orderId, String orderSeq) {
		// orderId, orderSeq로 해당하는 TbOrderDetail 찾아오기
		log.debug("in changeOrderStatus ; orderId : " + orderId + ", orderSeq : " + orderSeq);
		TypedQuery<TbOrderDetail> query = em.createQuery(
				"select td from TbOrderDetail td " + "join fetch td.tbOrderMaster tm " + "left join fetch td.ititmm it "
						+ "left join fetch it.itasrt itasrt " + "where td.orderId = ?1" + "and td.orderSeq = ?2 ",
				TbOrderDetail.class);
		query.setParameter(1, orderId).setParameter(2, orderSeq);
		TbOrderDetail tbOrderDetail = query.getSingleResult();
		if (tbOrderDetail == null) {
			log.debug("There is no TbOrderDetail of " + orderId + " and " + orderSeq);
			return;
		}
		Ititmm ititmm = jpaItitmmRepository.findByAssortIdAndItemId(tbOrderDetail.getAssortId(),
				tbOrderDetail.getItemId());
		if (ititmm == null) {
			log.debug("There is no ititmm of orderId : " + orderId + " and orderSeq : " + orderSeq);
			return;
		}

		Itasrt itasrt = jpaItasrtRepository.findByAssortId(tbOrderDetail.getAssortId());

		if (itasrt == null) {
			log.debug("There is no itasrt of orderId : " + orderId + " and orderSeq : " + orderSeq);
			return;
		}
		// Itasrt itasrt = tbOrderDetail.getItasrt();
		String prevStatus = tbOrderDetail.getStatusCd();
//        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
		String assortGb = itasrt.getAssortGb();

		if (assortGb == null) { // add_goods인 경우 자체 assortGb가 존재하지 않아, 부모 상품의 것을 따른다.
			itasrt = this.getParentAssortGb(orderId, orderSeq, itasrt);
		}

		System.out.println(assortGb);

		if (StringFactory.getGbOne().equals(assortGb)) { // assortGb == '01' : 직구
			this.changeOrderStatusWhenDirect(tbOrderDetail);
		} else if (StringFactory.getGbTwo().equals(assortGb)) { // assortGb == '02' : 수입
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
			// isStockExist = this.loopItitmc(domItitmc, tbOrderDetail); //20211217
			// 국내재고 있는경우 랙에서 지시수량 차감 ,창고에서 지시수량 차감
			Ititmc im = jpaStockService.checkStockWhenDirect(goodsStorageId, assortId, itemId, tbOrderDetail.getQty());

			// 지시수량 차감처리가 되었다면 출고지시데이타 생성
			if (im != null) {
				this.makeShipDataByDeposit(im, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
			}

			
			// 재고처리가 제대로 되었다면 주문상태 업데이트
			statusCd = im != null ? StringFactory.getStrC04() : statusCd; // 국내입고완료(해외지만 거기서 바로 쏘므로) : C04
			// this.getLsdpsdListByGoodsInfo(tbOrderDetail).get(0); // 숫자 맞는 상품입고와 그 입고에 연결된
			// 상품발주에 orderId와 orderSeq 적어넣기
		}
//        // 2. 해외입고예정재고 있을 가능성이 있음
//        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
//            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.direct); // 해외입고예정재고 있음 : B02 (발주완료), 없음 : B01 (발주대기)
//        }

		if (statusCd == null) {
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
        lsdpsd.setOrderId(tbOrderDetail.getOrderId());
        lsdpsd.setOrderSeq(tbOrderDetail.getOrderSeq());
        this.updateLsdpsds(lsdpsd);

        Lspchd lspchd = lsdpsd.getLspchd();
        lspchd.setOrderId(tbOrderDetail.getOrderId());
        lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
        this.updateLspchds(lspchd);
        jpaLspchdRepository.save(lspchd);

        return lsdpsdList;
    }

    /**
     * lspchd와 lspchb 업데이트
     */
    private void updateLspchds(Lspchd lspchd) {
        Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(lspchd.getPurchaseNo(), lspchd.getPurchaseSeq(), Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        lspchb.setEffEndDt(LocalDateTime.now());
        Lspchb newLspchb = new Lspchb(lspchd, "regId"); // regId 임시 하드코딩
        newLspchb.setPurchaseStatus(lspchb.getPurchaseStatus());
        jpaLspchdRepository.save(lspchd);
        jpaLspchbRepository.save(lspchb);
        jpaLspchbRepository.save(newLspchb);
    }

    /**
     * lsdpsd와 lsdpds 업데이트
     */
    private void updateLsdpsds(Lsdpsd lsdpsd){
        Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(), lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        lsdpds.setEffEndDt(new Date());
        Lsdpds newLsdpds = new Lsdpds(lsdpsd);
        jpaLsdpdsRepository.save(lsdpds);
        jpaLsdpdsRepository.save(newLsdpds);
        jpaLsdpsdRepository.save(lsdpsd);
    }

    /**
     * 수입(해외창고 -> 국내창고 -> 국내주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void  changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail) {

		System.out.println("changeOrderStatusWhenImport");

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

			Ititmc im = jpaStockService.checkStockWhenImport(domesticStorageId, assortId, itemId,
					tbOrderDetail.getQty());

			// 지시수량 차감처리가 되었다면 출고지시데이타 생성
			if (im != null) {
				this.makeDomesticShipDataByDeposit(im, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
			}

			statusCd = im != null ? StringFactory.getStrC04() : statusCd;

			// boolean isDomStockExist = this.loopItitmcByDomestic(domItitmc,
			// tbOrderDetail);
			// statusCd = isDomStockExist? StringFactory.getStrC04() : statusCd; //
			// 국내(현지)입고완료 : C04
        }
//        // 2. 국내입고예정 재고가 있을 가능성이 있음
//        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
//            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.imports);
//        }
        // 3. 해외재고가 있을 가능성이 있음

        if(statusCd == null && sumOfOvrsQty - sumOfOvrsShipIndQty - tbOrderDetail.getQty() >= 0){

			Ititmc im = jpaStockService.checkStockWhenImport(overseaStorageId, assortId, itemId,
					tbOrderDetail.getQty());

			// 지시수량 차감처리가 되었다면 출고지시데이타 생성
			if (im != null) {
				this.makeMoveDataByDeposit(im, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
			}

			statusCd = im != null ? StringFactory.getStrC01() : statusCd;

			// this.loopItitmcByMove(ovrsItitmc, tbOrderDetail);
			// statusCd = StringFactory.getStrC01(); // 해외입고완료 : C01
        }
        // 4. 해외입고예정 재고가 있을 가능성이 있음
//        if(statusCd == null && sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() >= 0){
		// System.out.println("44444444444444444444444444444444444444");

		// statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail,
		// DirectOrImport.imports);
///        }

//        if(statusCd == null && sumOfOvrsQty - sumOfOvrsShipIndQty - tbOrderDetail.getQty() >= 0){
//			this.loopItitmcByMove(ovrsItitmc, tbOrderDetail);
		// statusCd = StringFactory.getStrC01(); // 해외입고완료 : C01
		// }
//        // 4. 해외입고예정 재고가 있을 가능성이 있음
//        if(statusCd == null && sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() >= 0){
//			System.out.println("44444444444444444444444444444444444444");
//
//            statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail, DirectOrImport.imports);
//        }

        // 5. 아무것도 없음
        if(statusCd == null){
            statusCd = StringFactory.getStrB01(); // 발주대기 : B01
        }
        this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd);
    }

    /**
     * Ititmt list를 loop 돌면서 qty 관련 계산
     * 10-21 수정 : 해당 주문 이상의 숫자를 가진 ititmt가 존재해야 함.
     */
    private String loopItitmt(List<Ititmt> ititmtList, TbOrderDetail tbOrderDetail, DirectOrImport di) {

		System.out.println("loopItitmt");

		System.out.println(di);

        boolean isStockCandidateExist = false;
        long orderQty = tbOrderDetail.getQty();
        String goodsStorageId = null;

        for(Ititmt ititmt : ititmtList){
            if(ititmt.getTempQty() >= orderQty + ititmt.getTempIndicateQty()){
				ititmt.setTempQty(ititmt.getTempQty() - orderQty);
				// ititmt.setTempIndicateQty(orderQty + ititmt.getTempIndicateQty());
				// ititmt.setTempIndicateQty(orderQty + ititmt.getTempIndicateQty());
                isStockCandidateExist = true;
                goodsStorageId = ititmt.getStorageId();
                break;
            }
        }

        if(isStockCandidateExist && di.equals(DirectOrImport.direct)){ // 직구

			System.out.println("111111111111111111111111111");
            di = DirectOrImport.purchase;
            jpaPurchaseService.makePurchaseDataByOrder(tbOrderDetail, di);
            return StringFactory.getStrB02(); // 발주완료 : B02
        }
        else if(isStockCandidateExist && di.equals(DirectOrImport.imports)){ // 수입

			System.out.println("222222222222222222222222222");

            String statusCd;
            if(!tbOrderDetail.getStorageId().equals(goodsStorageId)){ // 물건이 해외입고예정이라면
                statusCd = StringFactory.getStrB02(); // 발주완료 : B02
				// di = DirectOrImport.move;
				di = DirectOrImport.purchase;
            }
            else { // 물건이 국내(주문자위치)입고예정이라면
                statusCd = StringFactory.getStrC03(); // 이동지시완료 : C03
				// di = DirectOrImport.purchase;
				di = DirectOrImport.move;
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


		throw new IllegalArgumentException("loopItitmc use ititmc");
//
//    	
//        long orderQty = tbOrderDetail.getQty();
//        for(Ititmc ititmc : ititmcList){
//            if(ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()){
//
//                ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//
//                this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
//                return true;
//            }
//        }
//        return false;

		// 이부분은 일부러 오류낼려고 처리한 내역임.랙처리되면서 사라지는 내용 2022-01-14
//        long orderQty = tbOrderDetail.getQty();
//        boolean isBigOneExist = false;
//        for(Ititmc ititmc : ititmcList){
//            if(ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()){
//                ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//                this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
//                return true;
//            }
//        }
//        return false;

    }

	/**
	 * Ititmc list를 loop 돌면서 qty 관련 계산
	 */
	private boolean loopItitmcByDomestic(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail) {
		throw new IllegalArgumentException("loopItitmcByDomestic use ititmc");

//		
//		long orderQty = tbOrderDetail.getQty();
//		for (Ititmc ititmc : ititmcList) {
//			if (ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()) {
//				ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//				this.makeDomesticShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * Ititmc list를 loop 돌면서 qty 관련 계산
	 */
	private boolean loopItitmcByMove(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail) {

		throw new IllegalArgumentException("loopItitmcByMove use ititmc");
//		// 해외입고건은 이동지시 생성
//
//		long orderQty = tbOrderDetail.getQty();
//		for (Ititmc ititmc : ititmcList) {
//			if (ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()) {
//				ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//				this.makeMoveDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (출고지시) 하드코딩
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s) ShipIndicateSaveData 객체로
	 * lsshpm,s,d 생성
	 */
	private String makeMoveDataByDeposit(Ititmc ititmc, TbOrderDetail tbOrderDetail, String shipStatus) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("03", shipId, itasrt, tbOrderDetail);

		lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipGb("02");
		lsshpm.setShipOrderGb("01");
		lsshpm.setMasterShipGb("03");

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		lsshpm.setStorageId(itasrt.getStorageId());

		// lsshps 저장
		Lsshps lsshps = new Lsshps(lsshpm);
		jpaLsshpsRepository.save(lsshps);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd 저장
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 하드코딩
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
		lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
		lsshpd.setShipGb("03"); // 주문이동지시
		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

    /**
     * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s) ShipIndicateSaveData 객체로
     * lsshpm,s,d 생성
     */
    private String makeShipDataByDeposit(Ititmc ititmc, TbOrderDetail tbOrderDetail, String shipStatus) {
        String shipId = this.getShipId();

        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        // lsshpm 저장
        Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

        lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고
        lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

        lsshpm.setShipOrderGb("01");
        lsshpm.setMasterShipGb("01");

        // lsshpm.setOStorageId(tbOrderDetail.getStorageId());

        lsshpm.setStorageId(itasrt.getStorageId());

        // lsshps 저장
        Lsshps lsshps = new Lsshps(lsshpm);
        jpaLsshpsRepository.save(lsshps);
        jpaLsshpmRepository.save(lsshpm);
        // lsshpd 저장
        String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 하드코딩
        Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
        lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
        lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
        lsshpd.setShipGb("01"); // 주문출고지시
        jpaLsshpdRepository.save(lsshpd);
        return shipId;
    }

    /**
	 * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s) ShipIndicateSaveData 객체로
	 * lsshpm,s,d 생성
	 */
	private String makeDomesticShipDataByDeposit(Ititmc ititmc, TbOrderDetail tbOrderDetail, String shipStatus) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipOrderGb("01");
		lsshpm.setMasterShipGb("01");

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		lsshpm.setStorageId(tbOrderDetail.getStorageId());
		lsshpm.setOStorageId("");

		// lsshps 저장
		Lsshps lsshps = new Lsshps(lsshpm);
		jpaLsshpsRepository.save(lsshps);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd 저장
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 하드코딩
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
		lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
		lsshpd.setShipGb("01"); // 주문출고지시
		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

	/**
	 * shipId 채번 함수
	 */
    private String getShipId(){
        return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
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
        LocalDateTime date = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
		List<TbOrderHistory> tohs = jpaTbOrderHistoryRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq, date);
        if(statusCd.equals(tod.getStatusCd())){
            log.debug("변경하려는 주문상태가 현재 주문상태와 동일합니다.");
            return;
        }
		tod.setStatusCd(statusCd);

        LocalDateTime newEffEndDate = LocalDateTime.now();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
		 }

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        // 임시 코드
        toh.setRegId("1");
        toh.setUpdId("1");

		tohs.add(toh);

//        System.out.println(tod);
//        TbOrderDetail t = jpaTbOrderDetailRepository.save(tod);
//        System.out.println(t);
        jpaTbOrderHistoryRepository.saveAll(tohs);

        // 카카오 알림톡 발송
//        kakaoBizMessageService.sendKakaoBizMessage(statusCd, tod);
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

    public TbOrderDetail getNullTest(String orderId, String orderSeq) {
        TbOrderDetail tbOrderDetail = em.createQuery("select t from TbOrderDetail t " +
                "left join fetch t.ititmm im " +
                "left join fetch im.itasrt " +
                "where t.orderId=?1 and t.orderSeq=?2", TbOrderDetail.class)
                .setParameter(1, orderId)//, "O00020410"O00025071
                .setParameter(2,orderSeq).getSingleResult();

        if(tbOrderDetail.getItitmm().getItasrt() == null){
            System.out.println("널입니다.");
        }
        return tbOrderDetail;
    }

    private enum ItitmcQty{
        QTY, SHIPINDQTY
    }

    private enum ItitmtQty{
        TEMPQTY, TEMPINDQTY
    }


    public void testSms(String body, String tbOrderNo){
        TbOrderDetail td = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(tbOrderNo, "0001");
        smsService.sendSmsMessage(body, td);
    }


	@Transactional
	public boolean saveGoodsIfoption(String orderId, String orderSeq, String assortId, String channelGoodsNo,
			String channelOptionSno) {

		IfGoodsOption igo = null;
		Tmitem ti = null;

		System.out.println(orderId);
		System.out.println(orderSeq);
		System.out.println(assortId);
		System.out.println(channelGoodsNo);
		System.out.println(channelOptionSno);
		
		List<Ititmm> r2 = jpaItitmmRepository.findByAssortId(assortId);
		
		if (r2.size() > 1) {
			System.out.println(assortId + " 해당상품은 옵션이 많은 상품입니다.확인후 처리하세요");
			throw new RuntimeException("해당상품은 옵션이 많은 상품입니다.확인후 처리하세요.");
		}

		System.out.println(r2.size());

		List<IfGoodsOption> r = jpaIfGoodsOptionRepository.findBySnoAndGoodsNo(channelOptionSno, channelGoodsNo);
		if (r == null || r.size() == 0) {

			igo = new IfGoodsOption();
			
			igo.setChannelGb("01");
			igo.setGoodsNo(channelGoodsNo);
			igo.setSno(channelOptionSno);
			igo.setOptionNo("1");
			igo.setOptionViewFl("y");
			igo.setUploadStatus("02");
			igo.setAssortId(assortId);
			igo.setItemId("0001");
			igo.setRegDt(LocalDateTime.now());
			igo.setModDt(LocalDateTime.now());

		} else {
			System.out.println("ifGoodsOption 이 이미 있습니다");
			// throw new RuntimeException("ifGoodsOption 이 이미 있습니다");
		}

		System.out.println(r.size());

		Tmitem r1 = jpaTmitemRepository.findByChannelGbAndAssortIdAndItemId("01", assortId, "0001").orElse(null);

		if (r1 == null) {
			ti = new Tmitem("01", assortId, "0001", channelGoodsNo, channelOptionSno);
			ti.setRegDt(LocalDateTime.now());
			ti.setRegId("1");
			ti.setUpdDt(LocalDateTime.now());
			ti.setUpdId("1");
		} else {
			System.out.println("tmitem 이 이미 있습니다");
			// throw new RuntimeException("tmitem 이 이미 있습니다");
		}

		System.out.println(r1);

		TbOrderDetail tod = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);

		if (tod != null) {
			tod.setItemId("0001");
		} else {
			System.out.println("TbOrderDetail 이 없습니다.");
			throw new RuntimeException("TbOrderDetail 이 없습니다.");
		}

		System.out.println(tod);

		Boolean ret = false;

		if (r2.size() == 1) {

			if (r.size() == 0 && r1 == null) {
				jpaIfGoodsOptionRepository.save(igo);
				jpaTmitemRepository.save(ti);
				jpaTbOrderDetailRepository.save(tod);
				ret = true;
			} else if (r.size() == 1 && r1 != null) {
				jpaTbOrderDetailRepository.save(tod);
				ret = true;
			}

		}

		return ret;


	}

	@Transactional
	public boolean cancelGodoOrder(HashMap<String, Object> p) {

//		m.put("orderId", o.getOrderId());
//		m.put("orderSeq", o.getOrderSeq());
//		m.put("cancelGb", o.getCancelGb());
//		m.put("cancelMsg", o.getCancelMsg());
//		m.put("cancelQty", o.getCancelQty());
//		m.put("ifCancelGb", o.getIfCancelGb());
//		m.put("userId", userId);

		String seq = p.get("seq").toString();

		IfOrderCancel ioc = jpaIfOrderCancelRepository.findById(seq).orElse(null);

		System.out.println(ioc.getIfNo());

		if (ioc == null || !ioc.getIfStatus().equals("01")) {
			System.out.println("취소요청데이타 이상!!!");
			throw new RuntimeException("취소요청데이타 이상!!!.");
		}

		// jpaIfOrderCancelRepository.

		Date today = new Date();

		LocalDateTime todayLDT = Instant.ofEpochMilli(today.getTime()).atZone(ZoneId.of("Asia/Seoul"))
				.toLocalDateTime();




			TbOrderDetail od = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(p.get("orderId").toString(),
					p.get("orderSeq").toString());

			TbOrderMaster om = jpaTbOrderMasterRepository.findById(p.get("orderId").toString()).orElse(null);

			IfOrderDetail iod = jpaIfOrderDetailRepository
					.findByChannelOrderNoAndChannelOrderSeq(od.getChannelOrderNo(), od.getChannelOrderSeq());
			
			IfOrderMaster iom = jpaIfOrderMasterRepository.findByChannelOrderNo(iod.getChannelOrderNo());

			String ifCancelGb = ioc.getIfCancelGb().toString();

			if (od.getStatusCd().equals("B01") || od.getStatusCd().equals("B02") || od.getStatusCd().equals("A01")) {
				if (od.getStatusCd().equals("B02")) {
					boolean r = jpaPurchaseService.cancelOrderPurchase(p);

					System.out.println(r);

				}

				if (ifCancelGb.equals("01")) {
					// 주문취소
					updateOrderStatusCd(p.get("orderId").toString(), p.get("orderSeq").toString(), "X01");
				} else if (ifCancelGb.equals("02")) {
					// 상품수량변경
				//	Long qty = 
				od.setQty(iod.getGoodsCnt());

				od.setGoodsPrice(iod.getFixedPrice());
				od.setSalePrice(iod.getGoodsPrice());
				od.setGoodsDcPrice(iod.getGoodsDcPrice());
				od.setMemberDcPrice(iod.getMemberDcPrice());
				od.setCouponDcPrice(iod.getCouponDcPrice());
				// od.setDcSumPrice(iod.getDc);
				od.setDeliPrice(iod.getDeliPrice());
				od.setOptionPrice(iod.getOptionPrice());

				float goodsDcPrice = iod.getGoodsDcPrice() == null ? 0 : iod.getGoodsDcPrice();
				float memberDcPrice = iod.getMemberDcPrice() == null ? 0 : iod.getMemberDcPrice();
				float couponDcPrice = iod.getCouponDcPrice() == null ? 0 : iod.getCouponDcPrice();
				float adminDcPrice = iod.getAdminDcPrice() == null ? 0 : iod.getAdminDcPrice();

				od.setDcSumPrice(goodsDcPrice + memberDcPrice + couponDcPrice + adminDcPrice);

				om.setOrderAmt(iom.getPayAmt());
				om.setReceiptAmt(iom.getPayAmt());
				om.setTotalGoodsPrice(iom.getTotalGoodsPrice());
				om.setTotalDeliveryCharge(iom.getTotalDeliveryCharge());
				om.setTotalGoodsDcPrice(iom.getTotalGoodsDcPrice());
				om.setTotalMemberDcPrice(iom.getTotalMemberDcPrice());
				om.setTotalMemberOverlapDcPrice(iom.getTotalMemberOverlapDcPrice());
				om.setTotalCouponGoodsDcPrice(iom.getTotalCouponGoodsDcPrice());
				om.setTotalCouponOrderDcPrice(iom.getTotalCouponOrderDcPrice());
				om.setTotalCouponDeliveryDcPrice(iom.getTotalCouponDeliveryDcPrice());
				om.setTotalMileage(iom.getTotalMileage());
				om.setTotalGoodsMileage(iom.getTotalGoodsMileage());
				om.setTotalMemberMileage(iom.getTotalMemberMileage());
				om.setTotalCouponGoodsMileage(iom.getTotalCouponGoodsMileage());
				om.setTotalCouponOrderMileage(iom.getTotalCouponOrderMileage());

				jpaTbOrderDetailRepository.save(od);
				jpaTbOrderMasterRepository.save(om);

			} else if (ifCancelGb.equals("03")) {
				// 상품수량변경,주문취소
				// Long qty =
				od.setQty(iod.getGoodsCnt());

				od.setGoodsPrice(iod.getFixedPrice());
				od.setSalePrice(iod.getGoodsPrice());
				od.setGoodsDcPrice(iod.getGoodsDcPrice());
				od.setMemberDcPrice(iod.getMemberDcPrice());
				od.setCouponDcPrice(iod.getCouponDcPrice());
				// od.setDcSumPrice(iod.getDc);
				od.setDeliPrice(iod.getDeliPrice());
				od.setOptionPrice(iod.getOptionPrice());

				float goodsDcPrice = iod.getGoodsDcPrice() == null ? 0 : iod.getGoodsDcPrice();
				float memberDcPrice = iod.getMemberDcPrice() == null ? 0 : iod.getMemberDcPrice();
				float couponDcPrice = iod.getCouponDcPrice() == null ? 0 : iod.getCouponDcPrice();
				float adminDcPrice = iod.getAdminDcPrice() == null ? 0 : iod.getAdminDcPrice();

				od.setDcSumPrice(goodsDcPrice + memberDcPrice + couponDcPrice + adminDcPrice);

				om.setOrderAmt(iom.getPayAmt());
				om.setReceiptAmt(iom.getPayAmt());
				om.setTotalGoodsPrice(iom.getTotalGoodsPrice());
				om.setTotalDeliveryCharge(iom.getTotalDeliveryCharge());
				om.setTotalGoodsDcPrice(iom.getTotalGoodsDcPrice());
				om.setTotalMemberDcPrice(iom.getTotalMemberDcPrice());
				om.setTotalMemberOverlapDcPrice(iom.getTotalMemberOverlapDcPrice());
				om.setTotalCouponGoodsDcPrice(iom.getTotalCouponGoodsDcPrice());
				om.setTotalCouponOrderDcPrice(iom.getTotalCouponOrderDcPrice());
				om.setTotalCouponDeliveryDcPrice(iom.getTotalCouponDeliveryDcPrice());
				om.setTotalMileage(iom.getTotalMileage());
				om.setTotalGoodsMileage(iom.getTotalGoodsMileage());
				om.setTotalMemberMileage(iom.getTotalMemberMileage());
				om.setTotalCouponGoodsMileage(iom.getTotalCouponGoodsMileage());
				om.setTotalCouponOrderMileage(iom.getTotalCouponOrderMileage());

				jpaTbOrderDetailRepository.save(od);
				jpaTbOrderMasterRepository.save(om);

				updateOrderStatusCd(p.get("orderId").toString(), p.get("orderSeq").toString(), "X01");

				}

				System.out.println("z05");
				ioc.setIfStatus("02");
				jpaIfOrderCancelRepository.save(ioc);
			} else {

				System.out.println("z06");
				ioc.setIfStatus("99");
				jpaIfOrderCancelRepository.save(ioc);
			}
			




		return true;
	}

	// 미발주조회

	public OrderStatusWatingItemListResponseData getOrderStatusWatingItems(String statusCd, int waitCnt,
			String assortGb) {

		List<TbOrderDetail> l = jpaTbOrderDetailRepository.findOrderStatusWatingDay(statusCd, waitCnt, assortGb);

		OrderStatusWatingItemListResponseData r = new OrderStatusWatingItemListResponseData(statusCd, waitCnt,
				assortGb);

		List<OrderStatusWatingItemListResponseData.Item> items = new ArrayList<>();
		
		for (TbOrderDetail o : l) {

			OrderStatusWatingItemListResponseData.Item item = new OrderStatusWatingItemListResponseData.Item(o);

			items.add(item);
		}

		r.setItems(items);

		return r;

	}

}


