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

    // orderId, orderSeq??? ?????? ?????? ????????? ??????????????? ??????
    @Transactional
	public void changeOrderStatus(String orderId, String orderSeq, String userId) {
        // orderId, orderSeq??? ???????????? TbOrderDetail ????????????
		log.debug("in changeOrderStatus ; orderId : " + orderId + ", orderSeq : " + orderSeq + ", userId : " + userId);
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
		String parentOrderSeq = tbOrderDetail.getParentOrderSeq();

		if (tbOrderDetail.getAssortGb().equals("002")) { // add_goods??? ?????? ?????? assortGb??? ???????????? ??????, ?????? ????????? ?????? ?????????.
			assortGb = this.getParentAssortGb2(orderId, parentOrderSeq);
        }

		tbOrderDetail.setUpdId(userId);

		System.out.println(assortGb);

        if(StringFactory.getGbOne().equals(assortGb)){ // assortGb == '01' : ??????
            this.changeOrderStatusWhenDirect(tbOrderDetail);
        }
        else if(StringFactory.getGbTwo().equals(assortGb)){ // assortGb == '02' : ??????
			this.changeOrderStatusWhenImport(tbOrderDetail, userId);
        }

		this.saveOrderLog(prevStatus, tbOrderDetail, userId);
    }

	// orderId, orderSeq??? ?????? ?????? ????????? ??????????????? ??????
	@Transactional
	public void noOptionChangeOrderStatus(String orderId, String orderSeq, String userId) {
		// orderId, orderSeq??? ???????????? TbOrderDetail ????????????
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

		if (assortGb == null) { // add_goods??? ?????? ?????? assortGb??? ???????????? ??????, ?????? ????????? ?????? ?????????.
			itasrt = this.getParentAssortGb(orderId, orderSeq, itasrt);
		}

		System.out.println(assortGb);

		if (StringFactory.getGbOne().equals(assortGb)) { // assortGb == '01' : ??????
			this.changeOrderStatusWhenDirect(tbOrderDetail);
		} else if (StringFactory.getGbTwo().equals(assortGb)) { // assortGb == '02' : ??????
			this.changeOrderStatusWhenImport(tbOrderDetail, userId);
		}

		this.saveOrderLog(prevStatus, tbOrderDetail, userId);
	}

    /**
     * add_goods??? itasrt??? ??????key??? ?????? add_goods??? assortGb??? ????????? ????????? ??????????????? ??????
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

	private String getParentAssortGb2(String orderId, String orderSeq) {

		TbOrderDetail td = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);

		Itasrt parentItasrt = jpaItasrtRepository.findByAssortId(td.getAssortId());

		return parentItasrt.getAssortGb();
	}

    /**
     * tbOrderDetail.statusCd??? ????????? ????????? ????????? ?????????.
     */
	private void saveOrderLog(String prevStatus, TbOrderDetail tbOrderDetail, String userId) {
        OrderLog orderLog = new OrderLog(tbOrderDetail);
        orderLog.setPrevStatus(prevStatus);
		orderLog.setRegId(userId);
		orderLog.setUpdId(userId);

        jpaOrderLogRepository.save(orderLog);
    }

    /**
     * ??????(???????????? -> ??????(??????)?????????)??? ??? ???????????? ?????? ??????
     * Ititmc : ????????????
     * Ititmt : ????????????????????????
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenDirect(TbOrderDetail tbOrderDetail) {
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
        String goodsStorageId = "000002";//tbOrderDetail.getStorageId(); // ????????? ???????????? ?????? id (????????????. ??????????????? ???????????? ????????? ??????)
		String userId = tbOrderDetail.getUpdId();

        // ???????????? ititmc ????????????
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, goodsStorageId, StringFactory.getStrEleven());
        // ???????????? ititmt ????????????
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, goodsStorageId, StringFactory.getStrEleven());

        long sumOfDomQty = this.getSumOfItitmcQty(ItitmcQty.QTY,domItitmc);
        long sumOfDomShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,domItitmc);
        long sumOfDomTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,domItitmt);
        long sumOfDomTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,domItitmt);

        System.out.println("??????????????? sumOfDomQty : " + sumOfDomQty);
        System.out.println("??????????????? sumOfDomShipIndQty : " + sumOfDomShipIndQty);
        System.out.println("??????????????? sumOfDomTempQty : " + sumOfDomTempQty);
        System.out.println("??????????????? sumOfDomTempIndQty : " + sumOfDomTempIndQty);
        // 1.????????????, 2.??????????????????
        // ?????????????????? ???????????? data ??????.

        boolean isStockExist;

        String statusCd = null;
        // 1. ???????????? ?????? ???????????? ??????
        if(sumOfDomQty - sumOfDomShipIndQty - tbOrderDetail.getQty() >= 0){
			// isStockExist = this.loopItitmc(domItitmc, tbOrderDetail); //20211217
			// ???????????? ???????????? ????????? ???????????? ?????? ,???????????? ???????????? ??????
			HashMap<String, Object> r = jpaStockService.checkStockWhenDirect(goodsStorageId, assortId, itemId,
					tbOrderDetail.getQty(),
					userId);

			Ititmc im_store = (Ititmc) r.get("store");
			Ititmc im_rack = (Ititmc) r.get("rack");

			// ???????????? ??????????????? ???????????? ????????????????????? ??????
			if (im_rack != null) {
				this.makeShipDataByDeposit(im_store, tbOrderDetail, StringFactory.getGbOne(), userId, im_rack); // 01
																												// (????????????)
																												// ????????????
			}

			// ??????????????? ????????? ???????????? ???????????? ????????????
			statusCd = im_rack != null ? StringFactory.getStrC04() : statusCd; // ??????????????????(???????????? ????????? ?????? ?????????) : C04
			// this.getLsdpsdListByGoodsInfo(tbOrderDetail).get(0); // ?????? ?????? ??????????????? ??? ????????? ?????????
			// ??????????????? orderId??? orderSeq ????????????
		}
//        // 2. ???????????????????????? ?????? ???????????? ??????
//        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
//            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.direct); // ???????????????????????? ?????? : B02 (????????????), ?????? : B01 (????????????)
//        }

		if (statusCd == null) {
            statusCd = StringFactory.getStrB01(); // ???????????? : B01
        }
		this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd, userId);
    }

    /**
     * orderStatus ????????? assortId, itemId, qty??? lsdpsd??? ???????????? ??????
     */
	private List<Lsdpsd> getLsdpsdListByGoodsInfo(TbOrderDetail tbOrderDetail, String userId) {
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
		this.updateLsdpsds(lsdpsd, userId);

        Lspchd lspchd = lsdpsd.getLspchd();
        lspchd.setOrderId(tbOrderDetail.getOrderId());
        lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
		this.updateLspchds(lspchd, userId);

		lspchd.setUpdId(userId);

        jpaLspchdRepository.save(lspchd);

        return lsdpsdList;
    }

    /**
     * lspchd??? lspchb ????????????
     */
	private void updateLspchds(Lspchd lspchd, String userId) {
        Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(lspchd.getPurchaseNo(), lspchd.getPurchaseSeq(), Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        lspchb.setEffEndDt(LocalDateTime.now());
        Lspchb newLspchb = new Lspchb(lspchd, "regId"); // regId ?????? ????????????

		newLspchb.setRegId(userId);
        newLspchb.setPurchaseStatus(lspchb.getPurchaseStatus());

		lspchd.setUpdId(userId);
		lspchb.setUpdId(userId);

		newLspchb.setUpdId(userId);

        jpaLspchdRepository.save(lspchd);
        jpaLspchbRepository.save(lspchb);
        jpaLspchbRepository.save(newLspchb);
    }

    /**
     * lsdpsd??? lsdpds ????????????
     */
	private void updateLsdpsds(Lsdpsd lsdpsd, String userId) {
        Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(), lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        lsdpds.setEffEndDt(new Date());
        Lsdpds newLsdpds = new Lsdpds(lsdpsd);

		newLsdpds.setRegId(userId);

		lsdpds.setUpdId(userId);
		newLsdpds.setUpdId(userId);
		lsdpsd.setUpdId(userId);

        jpaLsdpdsRepository.save(lsdpds);
        jpaLsdpdsRepository.save(newLsdpds);
        jpaLsdpsdRepository.save(lsdpsd);
    }

    /**
     * ??????(???????????? -> ???????????? -> ???????????????)??? ??? ???????????? ?????? ??????
     * Ititmc : ????????????
     * Ititmt : ????????????????????????
     * @param tbOrderDetail
     */
	private void changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail, String userId) {

		System.out.println("changeOrderStatusWhenImport");

        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
        if(itasrt == null){
            log.debug("?????? ?????? ????????? ???????????? ????????????.");
            return;
        }

        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
		String domesticStorageId = tbOrderDetail.getStorageId(); // ????????? ??????(???????) ?????? id (????????????)
        String overseaStorageId = itasrt.getStorageId(); // ????????? ??????(?) ?????? id (????????????)

        // ???????????? ititmc ????????????
        List<Ititmc> domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // ???????????? ititmt ????????????
        List<Ititmt> domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, domesticStorageId, StringFactory.getStrEleven());
        // ???????????? ititmc ????????????
        List<Ititmc> ovrsItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());
        // ???????????? ititmt ????????????
        List<Ititmt> ovrsItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGrade(assortId, itemId, overseaStorageId, StringFactory.getStrEleven());

        long sumOfDomQty = this.getSumOfItitmcQty(ItitmcQty.QTY,domItitmc);
        long sumOfDomShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,domItitmc);
        long sumOfDomTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,domItitmt);
        long sumOfDomTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,domItitmt);

        long sumOfOvrsQty = this.getSumOfItitmcQty(ItitmcQty.QTY,ovrsItitmc);
        long sumOfOvrsShipIndQty = this.getSumOfItitmcQty(ItitmcQty.SHIPINDQTY,ovrsItitmc);
        long sumOfOvrsTempQty = this.getSumOfItitmtQty(ItitmtQty.TEMPQTY,ovrsItitmt);
        long sumOfOvrsTempIndQty = this.getSumOfItitmtQty(ItitmtQty.TEMPINDQTY,ovrsItitmt);

        System.out.println("??????????????? sumOfDomQty : " + sumOfDomQty);
        System.out.println("??????????????? sumOfDomShipIndQty : " + sumOfDomShipIndQty);
        System.out.println("??????????????? sumOfDomTempQty : " + sumOfDomTempQty);
        System.out.println("??????????????? sumOfDomTempIndQty : " + sumOfDomTempIndQty);
        System.out.println("??????????????? sumOfOvrsQty : " + sumOfOvrsQty);
        System.out.println("??????????????? sumOfOvrsShipIndQty : " + sumOfOvrsShipIndQty);
        System.out.println("??????????????? sumOfOvrsTempQty : " + sumOfOvrsTempQty);
        System.out.println("??????????????? sumOfOvrsTempIndQty : " + sumOfOvrsTempIndQty);
        // 1.????????????, 2.??????????????????, 3.????????????, 4.?????????????????? ?????? ??? ???????????? ??????.
        // ?????????????????? ???????????? data ??????.

        String statusCd = null;
        // 1. ??????????????? ?????? ???????????? ??????
        if(sumOfDomQty - sumOfDomShipIndQty - tbOrderDetail.getQty() >= 0){

		HashMap<String,Object> r= jpaStockService.checkStockWhenImport(domesticStorageId, assortId, itemId,
					tbOrderDetail.getQty(), userId);

		Ititmc im_store = (Ititmc) r.get("store");
		Ititmc im_rack = (Ititmc) r.get("rack");
		
			// ???????????? ??????????????? ???????????? ????????????????????? ??????
			if (im_rack != null) {
				this.makeDomesticShipDataByDeposit(im_store, tbOrderDetail, StringFactory.getGbOne(), userId, im_rack); // 01
																														// (????????????)
																											// ????????????
			}

			statusCd = im_rack != null ? StringFactory.getStrC04() : statusCd;

			// boolean isDomStockExist = this.loopItitmcByDomestic(domItitmc,
			// tbOrderDetail);
			// statusCd = isDomStockExist? StringFactory.getStrC04() : statusCd; //
			// ??????(??????)???????????? : C04
        }
//        // 2. ?????????????????? ????????? ?????? ???????????? ??????
//        if(statusCd == null && sumOfDomTempQty - sumOfDomTempIndQty - tbOrderDetail.getQty() >= 0){
//            statusCd = this.loopItitmt(domItitmt, tbOrderDetail, DirectOrImport.imports);
//        }
        // 3. ??????????????? ?????? ???????????? ??????

        if(statusCd == null && sumOfOvrsQty - sumOfOvrsShipIndQty - tbOrderDetail.getQty() >= 0){

			HashMap<String, Object> r = jpaStockService.checkStockWhenImport(overseaStorageId, assortId, itemId,
					tbOrderDetail.getQty(), userId);

			Ititmc im_store = (Ititmc) r.get("store");
			Ititmc im_rack = (Ititmc) r.get("rack");

			// ???????????? ??????????????? ???????????? ????????????????????? ??????
			if (im_rack != null) {
				this.makeMoveDataByDeposit(im_store, tbOrderDetail, StringFactory.getGbOne(), userId, im_rack); // 01
																												// (????????????)
																												// ????????????
			}

			statusCd = im_rack != null ? StringFactory.getStrC01() : statusCd;

			// this.loopItitmcByMove(ovrsItitmc, tbOrderDetail);
			// statusCd = StringFactory.getStrC01(); // ?????????????????? : C01
        }
        // 4. ?????????????????? ????????? ?????? ???????????? ??????
//        if(statusCd == null && sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() >= 0){
		// System.out.println("44444444444444444444444444444444444444");

		// statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail,
		// DirectOrImport.imports);
///        }

//        if(statusCd == null && sumOfOvrsQty - sumOfOvrsShipIndQty - tbOrderDetail.getQty() >= 0){
//			this.loopItitmcByMove(ovrsItitmc, tbOrderDetail);
		// statusCd = StringFactory.getStrC01(); // ?????????????????? : C01
		// }
//        // 4. ?????????????????? ????????? ?????? ???????????? ??????
//        if(statusCd == null && sumOfOvrsTempQty - sumOfOvrsTempIndQty - tbOrderDetail.getQty() >= 0){
//			System.out.println("44444444444444444444444444444444444444");
//
//            statusCd = this.loopItitmt(ovrsItitmt, tbOrderDetail, DirectOrImport.imports);
//        }

        // 5. ???????????? ??????
        if(statusCd == null){
            statusCd = StringFactory.getStrB01(); // ???????????? : B01
        }
		this.updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), statusCd, userId);
    }

    /**
     * Ititmt list??? loop ????????? qty ?????? ??????
     * 10-21 ?????? : ?????? ?????? ????????? ????????? ?????? ititmt??? ???????????? ???.
     */
	private String loopItitmt(List<Ititmt> ititmtList, TbOrderDetail tbOrderDetail, DirectOrImport di, String userId) {

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

        if(isStockCandidateExist && di.equals(DirectOrImport.direct)){ // ??????

			System.out.println("111111111111111111111111111");
            di = DirectOrImport.purchase;
			jpaPurchaseService.makePurchaseDataByOrder(tbOrderDetail, di, userId);
            return StringFactory.getStrB02(); // ???????????? : B02
        }
        else if(isStockCandidateExist && di.equals(DirectOrImport.imports)){ // ??????

			System.out.println("222222222222222222222222222");

            String statusCd;
            if(!tbOrderDetail.getStorageId().equals(goodsStorageId)){ // ????????? ???????????????????????????
                statusCd = StringFactory.getStrB02(); // ???????????? : B02
				// di = DirectOrImport.move;
				di = DirectOrImport.purchase;
            }
            else { // ????????? ??????(???????????????)?????????????????????
                statusCd = StringFactory.getStrC03(); // ?????????????????? : C03
				// di = DirectOrImport.purchase;
				di = DirectOrImport.move;
            }
			jpaPurchaseService.makePurchaseDataByOrder(tbOrderDetail, di, userId);
            return statusCd;
        }
        else{
            return StringFactory.getStrB01(); // ???????????? : B01
        }
    }

    /**
     * Ititmc list??? loop ????????? qty ?????? ??????
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
//                this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (????????????) ????????????
//                return true;
//            }
//        }
//        return false;

		// ???????????? ????????? ??????????????? ????????? ?????????.?????????????????? ???????????? ?????? 2022-01-14
//        long orderQty = tbOrderDetail.getQty();
//        boolean isBigOneExist = false;
//        for(Ititmc ititmc : ititmcList){
//            if(ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()){
//                ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//                this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (????????????) ????????????
//                return true;
//            }
//        }
//        return false;

    }

	/**
	 * Ititmc list??? loop ????????? qty ?????? ??????
	 */
	private boolean loopItitmcByDomestic(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail) {
		throw new IllegalArgumentException("loopItitmcByDomestic use ititmc");

//		
//		long orderQty = tbOrderDetail.getQty();
//		for (Ititmc ititmc : ititmcList) {
//			if (ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()) {
//				ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//				this.makeDomesticShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (????????????) ????????????
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * Ititmc list??? loop ????????? qty ?????? ??????
	 */
	private boolean loopItitmcByMove(List<Ititmc> ititmcList, TbOrderDetail tbOrderDetail) {

		throw new IllegalArgumentException("loopItitmcByMove use ititmc");
//		// ?????????????????? ???????????? ??????
//
//		long orderQty = tbOrderDetail.getQty();
//		for (Ititmc ititmc : ititmcList) {
//			if (ititmc.getQty() >= orderQty + ititmc.getShipIndicateQty()) {
//				ititmc.setShipIndicateQty(orderQty + ititmc.getShipIndicateQty());
//				this.makeMoveDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne()); // 01 (????????????) ????????????
//				return true;
//			}
//		}
//		return false;
	}

	/**
	 * ?????? ?????? ??? update, ?????? ?????? data ?????? ?????? (lsshpm,d,s) ShipIndicateSaveData ?????????
	 * lsshpm,s,d ??????
	 */
	private String makeMoveDataByDeposit(Ititmc ititmc_store, TbOrderDetail tbOrderDetail, String shipStatus,
			String userId, Ititmc ititmc_rack) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm ??????
		Lsshpm lsshpm = new Lsshpm("03", shipId, itasrt, tbOrderDetail);

		lsshpm.setRegId(userId);

		lsshpm.setShipStatus(shipStatus); // 01 : ????????????or????????????, 02 : ????????????or???????????? ??????, 04 : ??????
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipGb("02");
		lsshpm.setShipOrderGb("01");
		lsshpm.setMasterShipGb("03");

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		lsshpm.setStorageId(itasrt.getStorageId());

		// lsshps ??????
		Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);


		jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd ??????
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 ????????????
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc_store, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : ??????, 02 : ??????, 03 : ????????????
		lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
		lsshpd.setShipGb("03"); // ??????????????????

		lsshpd.setRackNo(ititmc_rack.getStorageId());

		lsshpd.setUpdId(userId);

		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

    /**
     * ?????? ?????? ??? update, ?????? ?????? data ?????? ?????? (lsshpm,d,s) ShipIndicateSaveData ?????????
     * lsshpm,s,d ??????
     */
	private String makeShipDataByDeposit(Ititmc ititmc_store, TbOrderDetail tbOrderDetail, String shipStatus,
			String userId, Ititmc ititmc_rack) {
        String shipId = this.getShipId();

        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        // lsshpm ??????
        Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setRegId(userId);

        lsshpm.setShipStatus(shipStatus); // 01 : ????????????or????????????, 02 : ????????????or???????????? ??????, 04 : ??????
        lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

        lsshpm.setShipOrderGb("01");
        lsshpm.setMasterShipGb("01");

        // lsshpm.setOStorageId(tbOrderDetail.getStorageId());

        lsshpm.setStorageId(itasrt.getStorageId());

        // lsshps ??????
        Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

        jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);

        jpaLsshpmRepository.save(lsshpm);
        // lsshpd ??????
        String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 ????????????
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc_store, itasrt);

		lsshpd.setRegId(userId);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
        lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : ??????, 02 : ??????, 03 : ????????????
        lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
        lsshpd.setShipGb("01"); // ??????????????????

		lsshpd.setRackNo(ititmc_rack.getStorageId());

		lsshpd.setUpdId(userId);

        jpaLsshpdRepository.save(lsshpd);
        return shipId;
    }

    /**
	 * ?????? ?????? ??? update, ?????? ?????? data ?????? ?????? (lsshpm,d,s) ShipIndicateSaveData ?????????
	 * lsshpm,s,d ??????
	 */
	private String makeDomesticShipDataByDeposit(Ititmc ititmc_store, TbOrderDetail tbOrderDetail, String shipStatus,
			String userId, Ititmc ititmc_rack) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm ??????
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setRegId(userId);

		lsshpm.setShipStatus(shipStatus); // 01 : ????????????or????????????, 02 : ????????????or???????????? ??????, 04 : ??????
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipOrderGb("01");
		lsshpm.setMasterShipGb("01");

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		lsshpm.setStorageId(tbOrderDetail.getStorageId());
		lsshpm.setOStorageId("");

		// lsshps ??????
		Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

		jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd ??????
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0'); // 0001 ????????????
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc_store, itasrt);

		lsshpd.setRegId(userId);

		lsshpd.setRackNo(ititmc_rack.getStorageId());
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : ??????, 02 : ??????, 03 : ????????????
		lsshpd.setShipIndicateQty(tbOrderDetail.getQty());
		lsshpd.setShipGb("01"); // ??????????????????

		lsshpd.setUpdId(userId);

		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

	/**
	 * shipId ?????? ??????
	 */
    private String getShipId(){
        return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
    }

    /**
     * JB
     * orderDetail??? orderStatusCd??? update??? ??? orderDetail??? orderHistory??? ???????????? update ???????????? ??????
     * @param orderId
     * @param orderSeq
     * @param statusCd
     */
	public void updateOrderStatusCd(String orderId, String orderSeq, String statusCd, String userId) {

		TbOrderDetail tod = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        LocalDateTime date = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
		List<TbOrderHistory> tohs = jpaTbOrderHistoryRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq, date);
        if(statusCd.equals(tod.getStatusCd())){
            log.debug("??????????????? ??????????????? ?????? ??????????????? ???????????????.");
            return;
        }
		tod.setStatusCd(statusCd);
		tod.setUpdId(userId);

        LocalDateTime newEffEndDate = LocalDateTime.now();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
		 }

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        // ?????? ??????
		toh.setRegId(userId);
		toh.setUpdId(userId);

		tohs.add(toh);

//        System.out.println(tod);
//        TbOrderDetail t = jpaTbOrderDetailRepository.save(tod);
//        System.out.println(t);
        jpaTbOrderHistoryRepository.saveAll(tohs);

        // ????????? ????????? ??????
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
     * ititmc ???????????? qty ?????? shipIndQty??? ????????? ???????????? ??????
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
     * ititmt ???????????? tempQty ?????? tempIndicateQty??? ????????? ???????????? ??????
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
            System.out.println("????????????.");
        }
        return tbOrderDetail;
    }

    private enum ItitmcQty{
        QTY, SHIPINDQTY
    }

    private enum ItitmtQty{
        TEMPQTY, TEMPINDQTY
    }

	public void testSms(String body, String tbOrderNo, String userId) {
        TbOrderDetail td = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(tbOrderNo, "0001");
		smsService.sendSmsMessage(body, td, userId);
    }


	@Transactional
	public boolean saveGoodsIfoption(String orderId, String orderSeq, String assortId, String channelGoodsNo,
			String channelOptionSno, String userId) {

		IfGoodsOption igo = null;
		Tmitem ti = null;

		// system.out.println(orderId);
		// System.out.println(orderSeq);
		// System.out.println(assortId);
		// System.out.println(channelGoodsNo);
		// System.out.println(channelOptionSno);
		
		List<Ititmm> r2 = jpaItitmmRepository.findByAssortId(assortId);
		
		if (r2.size() > 1) {
			System.out.println(assortId + " ??????????????? ????????? ?????? ???????????????.????????? ???????????????");
			throw new RuntimeException("??????????????? ????????? ?????? ???????????????.????????? ???????????????.");
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
			igo.setRegId(userId);
			igo.setUpdId(userId);

		} else {
			System.out.println("ifGoodsOption ??? ?????? ????????????");
			// throw new RuntimeException("ifGoodsOption ??? ?????? ????????????");
		}

		System.out.println(r.size());

		Tmitem r1 = jpaTmitemRepository.findByChannelGbAndAssortIdAndItemId("01", assortId, "0001").orElse(null);

		if (r1 == null) {
			ti = new Tmitem("01", assortId, "0001", channelGoodsNo, channelOptionSno);
			ti.setRegDt(LocalDateTime.now());
			ti.setRegId(userId);
			ti.setUpdDt(LocalDateTime.now());
			ti.setUpdId(userId);
		} else {
			System.out.println("tmitem ??? ?????? ????????????");
			// throw new RuntimeException("tmitem ??? ?????? ????????????");
		}

		System.out.println(r1);

		TbOrderDetail tod = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);

		if (tod != null) {
			tod.setItemId("0001");
		} else {
			System.out.println("TbOrderDetail ??? ????????????.");
			throw new RuntimeException("TbOrderDetail ??? ????????????.");
		}

		System.out.println(tod);

		Boolean ret = false;

		if (r2.size() == 1) {

			if (r.size() == 0 && r1 == null) {

				igo.setUpdId(userId);

				jpaIfGoodsOptionRepository.save(igo);

				ti.setUpdId(userId);

				jpaTmitemRepository.save(ti);

				tod.setUpdId(userId);

				jpaTbOrderDetailRepository.save(tod);
				ret = true;
			} else if (r.size() == 1 && r1 != null) {

				tod.setUpdId(userId);

				jpaTbOrderDetailRepository.save(tod);
				ret = true;
			}

		}

		return ret;


	}

	@Transactional
	public boolean cancelGodoOrder(HashMap<String, Object> p, String userId) {

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
			System.out.println("????????????????????? ??????!!!");
			throw new RuntimeException("????????????????????? ??????!!!.");
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
					boolean r = jpaPurchaseService.innerCancelOrderPurchase(p, userId);

					System.out.println(r);

				}

				if (ifCancelGb.equals("01")) {
					// ????????????
					updateOrderStatusCd(p.get("orderId").toString(), p.get("orderSeq").toString(), "X01", userId);
				} else if (ifCancelGb.equals("02")) {
					// ??????????????????
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

				od.setUpdId(userId);

				jpaTbOrderDetailRepository.save(od);

				om.setUpdId(userId);
				jpaTbOrderMasterRepository.save(om);

			} else if (ifCancelGb.equals("03")) {
				// ??????????????????,????????????
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

				od.setUpdId(userId);

				jpaTbOrderDetailRepository.save(od);

				om.setUpdId(userId);

				jpaTbOrderMasterRepository.save(om);

				updateOrderStatusCd(p.get("orderId").toString(), p.get("orderSeq").toString(), "X01", userId);

				}

				System.out.println("z05");
				ioc.setIfStatus("02");

				ioc.setUpdId(userId);

				jpaIfOrderCancelRepository.save(ioc);
			} else {

				System.out.println("z06");
				ioc.setIfStatus("99");

				ioc.setUpdId(userId);
				jpaIfOrderCancelRepository.save(ioc);
			}
			




		return true;
	}

	// ???????????????

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


