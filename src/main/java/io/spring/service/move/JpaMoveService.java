package io.spring.service.move;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.mapstruct.MoveCompletedListResponseDataMapper;
import io.spring.infrastructure.mapstruct.MoveIndicateListResponseDataMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaIfBrandRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.move.request.GoodsMoveSaveData;
import io.spring.model.move.request.MoveListExcelRequestData;
import io.spring.model.move.request.MoveListSaveData;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.GoodsModalListResponseData;
import io.spring.model.move.response.MoveCompletedLIstReponseData;
import io.spring.model.move.response.MoveIndicateDetailResponseData;
import io.spring.model.move.response.MoveIndicateListResponseData;
import io.spring.model.move.response.MoveListResponseData;
import io.spring.model.move.response.MovedDetailResponseData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaMoveService {
//    private final JpaCommonService jpaCommonService;
    private final JpaIfBrandRepository jpaIfBrandRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItitmtRepository jpaItitmtRepository;

	private final JpaItitmmRepository jpaItitmmRepository;

	private final JpaItasrtRepository jpaItasrtRepository;

    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;

	private final JpaLsdpsmRepository jpaLsdpsmRepository;

	private final JpaStockService jpaStockService;

    private final JpaPurchaseService jpaPurchaseService;
	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final EntityManager em;

    private final MoveCompletedListResponseDataMapper moveCompletedListResponseDataMapper;
    private final MoveIndicateListResponseDataMapper moveIndicateListResponseDataMapper;

    /**
	 * ?????? ???????????? ?????? ????????? ???????????? ?????? 2021-10-18 ?????? ?????? jb
	 */
//    public List<OrderMoveListResponseData> getOrderMoveList(Map<String, Object> map) {
//        LocalDate startDt = (LocalDate)map.get(StringFactory.getStrStartDt());
//        LocalDate endDt = (LocalDate)map.get(StringFactory.getStrEndDt());
//        String storageId = (String)map.get(StringFactory.getStrStorageId());
//        String assortId = (String)map.get(StringFactory.getStrAssortId());
//        String assortNm = (String)map.get(StringFactory.getStrAssortNm());
//        String itemId = (String)map.get(StringFactory.getStrItemId());
//        String deliMethod = (String)map.get(StringFactory.getStrDeliMethod());
//        List<TbOrderDetail> tbOrderDetailList = this.getTbOrderDetail(startDt, endDt, storageId, assortId, assortNm, itemId, deliMethod);
////        List<Lsdpsd> lsdpsdList = this.getLsdpsd(startDt, endDt, storageId, assortId, assortNm, itemId, deliMethod);
//        List<OrderMoveListResponseData> orderMoveListDataListResponse = new ArrayList<>();
//        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
//            Lspchd lspchd = this.getLsdpsdTbOrderDetailLspchd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq());
//            OrderMoveListResponseData orderMoveListResponseData = new OrderMoveListResponseData(lspchd);
//            Utilities.setOptionNames(orderMoveListResponseData, tbOrderDetail.getItasrt().getItvariList());
//            orderMoveListDataListResponse.add(orderMoveListResponseData);
//        }
//        return orderMoveListDataListResponse;
//    }

//    // lspchd??? tbOrderDetail??? itasrt??? ?????? ???????????? ??????
//    private Lspchd getLsdpsdTbOrderDetailLspchd(String orderId, String orderSeq) {
//        TypedQuery<Lspchd> query = em.createQuery("select lspchd from Lspchd lspchd " +
//                "join fetch lspchd.tbOrderDetail td " +
//                "join fetch td.itasrt it " +
//                "where lspchd.orderId=?1 and lspchd.orderSeq=?2", Lspchd.class);
//        query.setParameter(1,orderId).setParameter(2,orderSeq);
//        Lspchd lspchd = query.getSingleResult();
//        return lspchd;
//    }

    /**
     * ?????? ???????????? ???????????? ????????? ?????? TbOrderDetail?????? ???????????? ??????
     */
//    private List<TbOrderDetail> getTbOrderDetail(LocalDate startDt, LocalDate endDt, String storageId, String assortId, String assortNm, String itemId, String deliMethod) {
//        // lsdpsd, lsdpsm, tbOrderDetail, itasrt, itvari
//        LocalDateTime start = startDt.atStartOfDay();
//        LocalDateTime end = endDt.atTime(23,59,59);
//        TypedQuery<TbOrderDetail> query = em.createQuery("select to from TbOrderDetail to " +
//                "join fetch pd.lspchm pm " +
//                "join fetch pd.lsdpsd sd " +
//                "join fetch sd.lsdpsm sm " +
//                "join fetch to.itasrt i " +
//                "where " +
//                "to.regDt between ?1 and ?2 " +
//                "and (?3 is null or trim(?3)='' or pm.storeCd=?3) " +
//                "and (?4 is null or trim(?4)='' or to.assortId=?4) " +
//                "and (?5 is null or trim(?5)='' or to.itemId=?5) " +
//                "and (?6 is null or trim(?6)='' or to.deliMethod=?6) " +
//                "and (?7 is null or trim(?7)='' or i.assortNm like concat('%',?7,'%')) " +
//                "and to.statusCd = ?8"
//        , TbOrderDetail.class);
//        query.setParameter(1, start).setParameter(2, end).setParameter(3,storageId)
//                .setParameter(4,assortId).setParameter(5,itemId).setParameter(6,deliMethod)
//                .setParameter(7,assortNm).setParameter(8,StringFactory.getStrC01());
//        List<TbOrderDetail> tbOrderDetailList = query.getResultList();
//        return tbOrderDetailList;
//    }

    /**
     * ?????? ???????????? ???????????? ????????? ?????? Lsdpsd?????? ???????????? ??????
     */
    private List<Lsdpsd> getLsdpsd(LocalDate startDt, LocalDate endDt, String storageId, String assortId, String assortNm, String itemId, String deliMethod) {
        // lsdpsd, lsdpsm, tbOrderDetail, itasrt, itvari
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        Query query = em.createQuery("select d from Lsdpsd d " +
                "join fetch d.lsdpsm m " +
                "join fetch d.lspchd pd " +
                "join fetch pd.tbOrderDetail t " +
                "join fetch t.ititmm itm " +
                "join fetch itm.itasrt i " +
                "where " +
                "m.depositDt between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or m.storeCd=?3) " +
                "and (?4 is null or trim(?4)='' or d.assortId=?4) " +
                "and (?5 is null or trim(?5)='' or d.itemId=?5) " +
                "and (?6 is null or trim(?6)='' or t.deliMethod=?6) " +
                "and (?7 is null or trim(?7)='' or i.assortNm like concat('%',?7,'%'))"
        );
        query.setParameter(1, start).setParameter(2, end).setParameter(3,storageId)
        .setParameter(4,assortId).setParameter(5,itemId).setParameter(6,deliMethod)
                .setParameter(7,assortNm);
        List<Lsdpsd> lsdpsdList = query.getResultList();
        return lsdpsdList;
    }



    /**
     * ?????? ???????????? ?????? ??????
     */
    @Transactional
	public List<String> saveOrderMove(OrderMoveSaveData orderMoveSaveData, String userId) {

        List<OrderMoveSaveData.Move> moveList = orderMoveSaveData.getMoves();
        if(moveList.size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<String> newShipIdList = new ArrayList<>();
//        List<Lsdpsd> lsdpsdList = new ArrayList<>();
		List<HashMap<String, Object>> orderList = new ArrayList<>();

        // 1. ?????? data ??????
        for(OrderMoveSaveData.Move move : moveList){

			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("order_id", move.getOrderId());
			m.put("order_seq", move.getOrderSeq());

			orderList.add(m);

			List<String> shipIdList = this.saveOrderMoveSaveData(move, userId);
            if(shipIdList.size() > 0){
                shipIdList.stream().forEach(x->newShipIdList.add(x));
            }
        }
        // 2. ?????? data ??????
		// jpaPurchaseService.makePurchaseDataFromOrderMoveSave(lsdpsdList, moveList);
		// ??????????????? ?????? ?????????..2021-10-18

		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C02.toString(), userId);

		// moveList

        return newShipIdList;
    }

	/**
     * ?????? ???????????? ?????? ??????
     */
    @Transactional
	public List<String> saveOrderMoveByDeposit(Lsdpsd lsdpsd, String userId) {

        //List<OrderMoveSaveData.Move> moveList = orderMoveSaveData.getMoves();
		if (lsdpsd == null) {
            log.debug("input data is empty.");
            return null;
        }

        List<String> newShipIdList = new ArrayList<>();
       // List<Lsdpsd> lsdpsdList = new ArrayList<>();
		List<HashMap<String, Object>> orderList = new ArrayList<>();


		String aaa = lsdpsd.getAssortId();
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("order_id", lsdpsd.getOrderId());
		m.put("order_seq", lsdpsd.getOrderSeq());
		orderList.add(m);

		// ??????????????? ??????????????? ???????????? ?????????.
		// ???????????? ?????? ??????????????? ?????? ?????????????????? ????????? ???????????? ???????????? ??????

		if (lsdpsd.getOrderId() != null) {

			List<String> shipIdList = this.saveOrderMoveSaveDataByDeposit(lsdpsd, userId);
			if (shipIdList.size() > 0) {
				shipIdList.stream().forEach(x -> newShipIdList.add(x));
			}
		}
		


        // 2. ?????? data ??????
		// ?????????????????? ?????????????????? ?????? ????????????
		// jpaPurchaseService.makePurchaseDataFromOrderMoveSaveByDeposit(lsdpsdList,
		// moveList);

		// this.changeStatusCdOfTbOrderDetail(orderList, "C02");

		// moveList

        return newShipIdList;
    }

    /**
     * OrderMoveSaveData????????? lsshpm,s,d ??????
     * lsdpsm,d,s,b, lsdpsp, ititmt(???????????????) ??????
     * tbOrderDetail??? ??????
     */
	private List<String> saveOrderMoveSaveData(OrderMoveSaveData.Move move, String userId) {

		List<String> ret = new ArrayList<String>();

		Lsshpm lsshpm = jpaLsshpmRepository.findById(move.getShipId()).orElse(null);
//		Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(move.getShipId(), move.getShipSeq());

		lsshpm.setInstructDt(LocalDateTime.now());
		lsshpm.setShipStatus("02");

		Lsshps lsshps = new Lsshps(lsshpm);
		this.updateLsshps(lsshpm, userId);

		ret.add(move.getShipId());

//		Lsdpsd lsdpsd = this.getLsdpsdByDepositNoAndDepositSeq(move);
//        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(move.getOrderId(),move.getOrderSeq());
//        List<String> shipIdList = this.makeOrderShipData(lsdpsd, tbOrderDetail, move.getQty(), StringFactory.getGbOne());
//        if(shipIdList.size() > 0){
//            lsdpsdList.add(lsdpsd);
//        }
//        this.updateQty(orderMoveSaveData);
		return ret;
    }

	/**
	 * OrderMoveSaveData????????? lsshpm,s,d ?????? lsdpsm,d,s,b, lsdpsp, ititmt(???????????????) ??????
	 * tbOrderDetail??? ??????
	 */
	private List<String> saveOrderMoveSaveDataByDeposit(Lsdpsd lsdpsd, String userId) {
		// Lsdpsd lsdpsd = this.getLsdpsdByDepositNoAndDepositSeq(move);
		TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
				lsdpsd.getOrderSeq());
		List<String> shipIdList = this.makeOrderShipData(lsdpsd, tbOrderDetail, lsdpsd.getDepositQty(),
				StringFactory.getGbOne(), userId);
//		if (shipIdList.size() > 0) {
		// lsdpsdList.add(lsdpsd);
		// }
//        this.updateQty(orderMoveSaveData);
		return shipIdList;
	}

    /**
	 * depositNo??? depositSeq??? Lsdpsd??? ???????????? ?????? 2021-10-18 jb ????????????
	 */
//    private Lsdpsd getLsdpsdByDepositNoAndDepositSeq(OrderMoveSaveData.Move move) {
//        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d " +
////                "join fetch d.lsdpsp lp " +
////                "join fetch d.lsdpsm lm " +
////                "join fetch d.ititmm tm " +
////                "join fetch d.itasrt it " +
////                "join fetch tm.ititmc ic " +
////                "join fetch lp.tbOrderDetail t " +
//                        "where " +
//                        "d.depositNo=?1 and d.depositSeq=?2"
//                , Lsdpsd.class);
//        query.setParameter(1, move.getDepositNo())
//                .setParameter(2, move.getDepositSeq());
//        Lsdpsd lsdpsd = query.getSingleResult();
//        return lsdpsd;
//    }

    /**
     * ???????????? ??????, ?????? ?????? data ?????? ?????? (lsshpm,d,s)
     */
	private List<String> makeOrderShipData(Lsdpsd lsdpsd, TbOrderDetail tbOrderDetail, long qty, String shipStatus,
			String userId) {


        List<String> shipIdList = new ArrayList<>();


		Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsd.getAssortId());

		Ititmm ititmm = jpaItitmmRepository.findByAssortIdAndItemId(lsdpsd.getAssortId(), lsdpsd.getItemId());

		// List<Ititmc> ititmcList = ititmm.getItitmc();

//		List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemId(ititmm.getAssortId(), ititmm.getItemId());//ititmm.getItitmc();


		Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);
		
		HashMap<String, Object> p = new HashMap<String, Object>();

		p.put("assortId", lsdpsd.getAssortId());
		p.put("itemId", lsdpsd.getItemId());
		p.put("effStaDt", lsdpsm.getDepositDt());
		p.put("itemGrade", "11");
		p.put("storageId", lsdpsm.getStoreCd());
		p.put("rackNo", lsdpsd.getRackNo());
		p.put("qty", lsdpsd.getDepositQty());

		int r = jpaStockService.minusIndicateStockByOrder(p, userId);

		LocalDateTime depositDt = lsdpsm.getDepositDt();
		String storageId = lsdpsm.getStoreCd();
		String itemGrade = lsdpsd.getItemGrade();

		Ititmc imc_storage = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("storageId").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);

		// ititmcList = ititmcList.stream().filter(x ->
		// x.getEffEndDt().equals(depositDt)
		// && x.getStorageId().equals(storageId)
		// && x.getItemGrade().equals(itemGrade)).collect(Collectors.toList());
		// Ititmc ititmc = ititmcList.get(0);
		// // ititmc?????? shipIndicateQty ???????????????
		// if(qty > ititmc.getQty() - ititmc.getShipIndicateQty()){
		// log.debug("???????????? ???????????? ???????????????.");
		// return null;
		// }
		// ititmc.setShipIndicateQty(ititmc.getShipIndicateQty() + qty);
		// jpaItitmcRepository.save(ititmc);
//        TbOrderMaster tbOrderMaster = lsdpsd.getLspchd().getLsdpsp().get(0).getTbOrderDetail().getTbOrderMaster();

		// ????????????????????? ???????????????????????? row????????? ??????
		// for (int i = 0; i < qty; i++) { //??????1?????????????????? for??? ????????????
            String shipId = getShipId();
//            Lsdpsp lsdpsp = lsdpsd.getLspchd().getLsdpsp().get(i);
            // lsshpm ??????
			Lsshpm lsshpm = new Lsshpm("03", shipId, itasrt, tbOrderDetail);

			lsshpm.setRegId(userId);

			// ostorageId ??? ?????? to
			// storageId ??? ???????????? from

			lsshpm.setStorageId(storageId);
			lsshpm.setOStorageId(tbOrderDetail.getStorageId());

            lsshpm.setShipStatus(shipStatus); // 01 : ????????????, 04 : ??????
            // lsshps ??????
            Lsshps lsshps = new Lsshps(lsshpm);

			lsshps.setRegId(userId);
			lsshps.setUpdId(userId);

            jpaLsshpsRepository.save(lsshps);

			lsshpm.setUpdId(userId);

            jpaLsshpmRepository.save(lsshpm);
            // lsshpd ??????
            String shipSeq = StringFactory.getFourStartCd(); // 0001 ???????????? //StringUtils.leftPad(Integer.toString(i + 1), 4,'0');
			Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, imc_storage, itasrt);



			lsshpd.setRackNo(lsdpsd.getRackNo());
			lsshpd.setShipIndicateQty(qty);
			lsshpd.setRegId(userId);
			lsshpd.setUpdId(userId);

            jpaLsshpdRepository.save(lsshpd);
            shipIdList.add(shipId);
			// } //??????1?????????????????? for??? ????????????
        return shipIdList;
    }

    /**
     * ?????? ????????? : ?????????????????? ???????????? ???????????? ????????? ???????????? ????????? ????????? ??? ???????????? ??????
     */
    public GoodsModalListResponseData getGoodsList(String storageId, String purchaseVendorId, String assortId, String assortNm) {

		System.out.println("getGoodsList");
		List<Ititmc> ititmcList = this.getItitmc2(storageId, purchaseVendorId, assortId, assortNm);
        List<GoodsModalListResponseData.Goods> goodsList = new ArrayList<>();
        GoodsModalListResponseData goodsModalListResponseData = new GoodsModalListResponseData(storageId, purchaseVendorId, assortId, assortNm);
        for(Ititmc ititmc : ititmcList){

			GoodsModalListResponseData.Goods goods = new GoodsModalListResponseData.Goods(ititmc);
//	          Itasrt itasrt = ititmc.getItasrt();
//            IfBrand ifBrand = itasrt.getIfBrand();//jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(), itasrt.getBrandId()); // ????????? 01 ????????????

			// ???????????? ??????????????? ?????????????????? indicateqty ??? ?????? ????????? ?????????????????? ?????? ?????? ??????

//            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(),ititmc.getItemId())
			// .stream().filter(x->x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
			// long qtyOfC01 = tbOrderDetailList.size();

			goods.setOrderQty(0L);
			goods.setAvailableQty(goods.getAvailableQty());
//            goods.setStoreCd(goodsModalListResponseData.getStoreCd());
			// Utilities.setOptionNames(goods, itasrt.getItvariList()); //2022-02-09 ????????????
//            List<Itvari> itvariList = itasrt.getItvariList();
//            if(itvariList.size() > 0){
//                Itvari itvari1 = itvariList.get(0);
//                goods.setOptionNm1(itvari1.getOptionNm());
//            }
//            if(itvariList.size() > 1){
//                Itvari itvari2 = itvariList.get(1);
//                goods.setOptionNm2(itvari2.getOptionNm());
//            }
//            if(ifBrand != null){
			// goods.setBrandNm(ifBrand.getBrandNm());
			// }
            goodsList.add(goods);
        }
//        goodsList = this.removeDuplicate(goodsList); // goodsKey??? group by
        goodsModalListResponseData.setGoods(goodsList);
        return goodsModalListResponseData;
    }

//    /**
//     * goodsKey??? goodsList??? ??????????????? ?????? (????????????????????? ?????????????????? ??????????????? goodsKey?????? ??????)
//     * @param goodsList ?????????????????? ????????? ?????? goodsKey??? ?????? row??? ?????????
//     * @return goodsKey??? ????????? ????????? goodsList
//     */
//    private List<GoodsModalListResponseData.Goods> removeDuplicate(List<GoodsModalListResponseData.Goods> goodsList) {
//        List<GoodsModalListResponseData.Goods> newGoodsList = new ArrayList<>();
//        Map<String, GoodsModalListResponseData.Goods> goodsMap = new HashMap<>();
//        for(GoodsModalListResponseData.Goods goods : goodsList){
//            GoodsModalListResponseData.Goods goods1 = goodsMap.get(goods.getGoodsKey());
//            if(goods1 == null){
//                newGoodsList.add(goods);
//            }
//            else{
//                goods1.setAvailableQty(goods1.getAvailableQty() + goods.getAvailableQty());
//                goods1.setOrderQty(goods1.getOrderQty() + goods.getOrderQty());
//            }
//        }
//        return newGoodsList;
//    }

//    /**
//     * ?????? ???????????? : ??????????????????, ????????????, ????????????, ??????????????? ????????? ??? ???????????? ????????? ????????? ??? ?????? ????????? ??????
//     */
//    public List<Ititmc> getItitmcList(String storeCd, String purchaseVendorId, String assortId, String assortNm) {
//        List<Ititmc> ititmcList = this.getItitmc(storeCd, purchaseVendorId, assortId, assortNm);
//        List<GoodsMoveListResponseData> goodsMoveListDataListResponse = new ArrayList<>();
//        for(Ititmc ititmc : ititmcList){
//            GoodsMoveListResponseData goodsMoveListResponseData = new GoodsMoveListResponseData(ititmc);
//            goodsMoveListDataListResponse.add(goodsMoveListResponseData);
//        }
//        return goodsMoveListDataListResponse;
//    }

//    /**
//     * ?????? ???????????? : ??????????????????, ????????????, ????????????, ??????????????? ????????? ??? ???????????? ????????? ????????? ??? ?????? ????????? ??????
//     */
//    public List<GoodsMoveListResponseData> getGoodsMoveList(LocalDate shipIndDt, String storeCd, String oStoreCd, String deliMethod) {
//        List<Ititmc> ititmcList = this.getItitmc(shipIndDt, oStoreCd, deliMethod);
//        List<GoodsMoveListResponseData> goodsMoveListDataListResponse = new ArrayList<>();
//        for(Ititmc ititmc : ititmcList){
//            GoodsMoveListResponseData goodsMoveListResponseData = new GoodsMoveListResponseData(ititmc);
//            goodsMoveListDataListResponse.add(goodsMoveListResponseData);
//        }
//        return goodsMoveListDataListResponse;
//    }

    /**
     * ?????????????????? ???????????? ????????? ?????? Ititmc?????? ???????????? ??????
     */
    private List<Ititmc> getItitmc(String storageId, String purchaseVendorId, String assortId, String assortNm) {
        Query query = em.createQuery("select distinct(ic) from Ititmc ic " +
                "join fetch ic.itasrt it " +
				"left join fetch it.itbrnd ib "
				+
                "join fetch it.itvariList iv " +
                "where " +
                "(?1 is null or trim(?1)='' or ic.storageId=?1) " +
                "and (?2 is null or trim(?2)='' or it.vendorId=?2) " +
                "and (?3 is null or trim(?3)='' or ic.assortId=?3) " +
                "and (?4 is null or trim(?4)='' or it.assortNm like concat('%',?4,'%'))"
        );
        query.setParameter(1,storageId).setParameter(2,purchaseVendorId).setParameter(3,assortId)
        .setParameter(4,assortNm);
        List<Ititmc> ititmcList = query.getResultList();
        return ititmcList;
    }

	/**
	 * ?????????????????? ???????????? ????????? ?????? Ititmc?????? ???????????? ??????
	 */
	public List<Ititmc> getItitmc2(String storageId, String purchaseVendorId, String assortId, String assortNm) {

		// ???????????? ?????????.

		Query query = em
				.createQuery("select ic from Ititmc ic " + "join fetch ic.itasrt it " + "left join fetch it.itbrnd ib "
						+ "join fetch ic.cmstgm cm " + "join fetch ic.ititmm itm "
						+ "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
						+ "left join fetch itm.itvari3 itv3 " +
						"where "
						+ "(?1 is null or trim(?1)='' or cm.upStorageId=?1) "
						+ "and (?2 is null or trim(?2)='' or it.vendorId=?2) "
						+ "and (?3 is null or trim(?3)='' or ic.assortId=?3) "
						+ "and (?4 is null or trim(?4)='' or it.assortNm like concat('%',?4,'%')) and ic.qty > 0 "
						+ "order by ic.assortId,ic.effStaDt ");
		query.setParameter(1, storageId).setParameter(2, purchaseVendorId).setParameter(3, assortId).setParameter(4,
				assortNm);
		List<Ititmc> ititmcList = query.getResultList();
		return ititmcList;
	}

    /**
     * ?????????????????? ?????? ??????
     */
    @Transactional
	public List<String> saveGoodsMove(GoodsMoveSaveData goodsMoveSaveData, String userId) {

		// ?????????????????? ???????????????,

        List<String> shipIdList = new ArrayList<>();

        String regId = null;
        LocalDateTime purchaseDt = null;
        List<Lsshpd> lsshpdList = new ArrayList<>();

        for (GoodsMoveSaveData.Goods goods : goodsMoveSaveData.getGoods()) {
            regId = goodsMoveSaveData.getUserId();

            long moveQty = goods.getMoveQty();
            // 1. ?????? data ??????


//???????????? ????????????????????? ??????????????? ??????????????????

//            Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(goods.getAssortId(), goods.getItemId(),
//                    goods.getStorageId(), StringFactory.getStrEleven(), goods.getDepositDt());
//
//			// ???????????????????????? ???????????? ????????? ??????????????? ?????? ???????????? ??????????????? ????????? ??????????????? ????????? ???????????? ??????
//			// List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository
//			// .findByAssortIdAndItemId(ititmc.getAssortId(), ititmc.getItemId()).stream()
//			// .filter(x ->
//			// x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
//
//			// System.out.println(goods);
//
//			// System.out.println(tbOrderDetailList);
//
//			// long qtyOfC01 = tbOrderDetailList.size();
//			long qtyOfC01 = 0;
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty();
//            long shipIndicateQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty();
//            if(goods.getMoveQty() > qty - shipIndicateQty - qtyOfC01){
//                log.debug("???????????? ????????????????????? ?????????.");
//                continue;
//            }

            if(goods.getMoveQty() == 0){
                log.debug("???????????? 0????????? ???????????? ????????????.");
                continue;
            }
            

			String StorageId = jpaStockService.getUpStorageId(goods.getStorageId());
			String rackNo = goods.getStorageId();
   
            
			Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
					goods.getAssortId(), goods.getItemId(), rackNo, StringFactory.getStrEleven(),
			// Utilities.dateToLocalDateTime(goods.getDepositDt()));
					goods.getDepositDt());
//
//			// ???????????????????????? ???????????? ????????? ??????????????? ?????? ???????????? ??????????????? ????????? ??????????????? ????????? ???????????? ??????
//			// List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository
//			// .findByAssortIdAndItemId(ititmc.getAssortId(), ititmc.getItemId()).stream()
//			// .filter(x ->
//			// x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
//
//			// System.out.println(goods);
//
//			// System.out.println(tbOrderDetailList);
//
//			// long qtyOfC01 = tbOrderDetailList.size();
//			long qtyOfC01 = 0;
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty();
//            long shipIndicateQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty();
//            if(goods.getMoveQty() > qty - shipIndicateQty - qtyOfC01){
//                log.debug("???????????? ????????????????????? ?????????.");
//                continue;
//            }

            // 1-0. Lsshpm ??????
            String shipId = this.getShipId();
			Lsshpm lsshpm = new Lsshpm("04", shipId, goodsMoveSaveData);
			lsshpm.setRegId(userId);
            purchaseDt = lsshpm.getReceiptDt();

			lsshpm.setInstructDt(LocalDateTime.now()); // ?????????????????? ??????
			lsshpm.setShipStatus("02"); // ????????????????????? ?????? 01 ??? ??????????????????

//            // 1-1. ititmc ??? ??????
//            if(lsshpm != null){
//                ititmc.setShipIndicateQty(shipIndicateQty + moveQty);
//                ititmc.setUpdId(goodsMoveSaveData.getUserId());
//                jpaItitmcRepository.save(ititmc);
//            }
			
			
            String shipSeq = StringFactory.getFourStartCd(); // 0001 ???????????? //StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd ??????
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, ititmc, goods, regId);

			lsshpd.setRegId(userId);

			lsshpd.setRackNo(rackNo);

            lsshpd.setOStorageId(goodsMoveSaveData.getOStorageId());
            lsshpd.setShipIndicateQty(moveQty);
            lsshpm.setChannelId(goods.getChannelId()); // vendorId??? ???????????? set
            lsshpdList.add(lsshpd);
//            jpaLsshpdRepository.save(lsshpd);
            // 1-3. Lsshps ??????
            Lsshps lsshps = new Lsshps(lsshpm, regId);
			lsshps.setRegId(userId);


			lsshpm.setUpdId(userId);

            jpaLsshpmRepository.save(lsshpm);


			lsshps.setUpdId(userId);

            jpaLsshpsRepository.save(lsshps);

            shipIdList.add(shipId);


			// ??????????????????
			HashMap<String, Object> p = new HashMap<String, Object>();

			p.put("assortId", goods.getAssortId());
			p.put("itemId", goods.getItemId());
			p.put("effStaDt", goods.getDepositDt()); // Utilities.dateToLocalDateTime(goods.getDepositDt()));
			p.put("itemGrade", "11");
			p.put("storageId", StorageId);
			p.put("rackNo", rackNo);
			p.put("qty", moveQty);

			jpaStockService.minusIndicateStockByOrder(p, userId);


			// 2. ?????? data ?????? ?????? //?????????????????? ?????????????????? ???????????? ????????????.
//            jpaPurchaseService.makePurchaseDataFromGoodsMoveSave(regId, purchaseDt, lsshpm, lsshpd);
//            List<Lsdpsp> lsdpspList = new ArrayList<>();
//            lsdpspList.add(lsdpsp);
//
//            // 3. ititmt ?????? ??????
//            Ititmc ititmc1 = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(lsshpd.getAssortId(),lsshpd.getItemId(),lsshpm.getStorageId(),
//                    StringFactory.getStrEleven(), Utilities.dateToLocalDateTime(goods.getDepositDt())); // ititmc??? ?????? ????????? ???????????? ??? ititmt??? ?????? ????????? ??????.
//            Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndUpdDt(lsshpd.getAssortId(), lsshpd.getItemId(),
//                    lsshpd.getOStorageId(),StringFactory.getStrEleven(),ititmc1.getRegDt());
//            ititmt.setTempIndicateQty(moveQty);
//            jpaItitmtRepository.save(ititmt);
        }

		// ????????????????????? ?????? ??????????????? ???????????????????????? ????????? ????????? ititmc ?????? ???????????? ???????????? ????????? ??????????????? ?????? ????????? ?????? ?????????
		// ??????????????? ??????????????? ??????????????? ?????? ?????????,
		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(lsshpdList, userId);


        return shipIdList;
    }

    /**
     * ?????? data ?????? ??????
     */
	private long saveGoodsMoveSaveData(String shipId, GoodsMoveSaveData goodsMoveSaveData,
			GoodsMoveSaveData.Goods goods, List<Integer> indexStore, List<GoodsMoveSaveData.Goods> newGoodsList,
			String userId) {
        GoodsMoveSaveData.Goods rowGoods = this.getItitmcByCondition(goods, newGoodsList);
		long lsshpdNum = this.makeGoodsShipData(shipId, rowGoods, goodsMoveSaveData, indexStore, userId);
//        ititmcList.add(ititmc);
//        this.updateQty(goods);
        return lsshpdNum;
    }

    /**
     * ????????? ?????? ititmc??? ????????? ??????.
     * ?????? : assortId, itemId
     */
    private GoodsMoveSaveData.Goods getItitmcByCondition(GoodsMoveSaveData.Goods goods, List<GoodsMoveSaveData.Goods> newGoodsList) {
        TypedQuery<Ititmc> query = em.createQuery("select ic from Ititmc ic " +
//                "join fetch d.lsdpsp lp " +
//                "join fetch d.lsdpsm lm " +
//                "join fetch d.ititmm tm " +
//                "join fetch d.itasrt it " +
//                "join fetch tm.ititmc ic " +
//                "join fetch lp.tbOrderDetail t " +
                        "where " +
                        "ic.assortId=?1 and ic.itemId=?2 order by ic.effEndDt asc"
                , Ititmc.class);
        query.setParameter(1, goods.getAssortId())
                .setParameter(2, goods.getItemId());
        List<Ititmc> ititmcList = query.getResultList();

        // 2. ititmc qty??? ??????
        ititmcList = this.calcItitmcQty(ititmcList, goods.getMoveQty());
        if(ititmcList.size() > 0){
            newGoodsList.add(goods);
        }
        else{
            log.debug("????????? ??? ?????? ????????? ???????????? ????????????.");
        }

        GoodsMoveSaveData.Goods rowGoods = this.makeItitmcsToOneRow(ititmcList, goods);
        return rowGoods;
    }

    /**
     * ?????????????????? ????????? ititmc??? qty ?????? ?????? (??????????????? ??????)
     */
    public List<Ititmc> calcItitmcQty(List<Ititmc> ititmcList, long shipQty) {
		throw new IllegalArgumentException("calcItitmcQty use ititmc");

//        List<Ititmc> newItitmcList = new ArrayList<>();
//        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
//        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
//        if(ititmcQty - ititmcShipIndQty < shipQty){
//            return newItitmcList;
//        }
//        for(Ititmc ititmc : ititmcList){
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc ?????????
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc ???????????????
//            long canShipQty = qty - shipIndQty; // ???????????????
//            if(canShipQty <= 0){ // ?????? ??????
//                log.debug("?????? ?????? ????????? ???????????????.");
//                continue;
//            }
//            if(shipQty <= canShipQty){ // ??? ???????????? ?????? ?????? ??????
//                ititmc.setShipIndicateQty(shipIndQty + shipQty);
//                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
////            else{ // ??? ???????????? ?????? ?????? ????????? ????????? ???????????? ??? ?????? ??????
////                shipQty -= canShipQty;
////                ititmc.setShipIndicateQty(qty);
////                jpaItitmcRepository.save(ititmc);
////            }
//        }
//        return newItitmcList;
    }

    /**
     * ititmc ???????????? ?????? ?????????????????? ????????? ??? ?????? ???????????? ????????? ????????? ??????
     */
    private GoodsMoveSaveData.Goods makeItitmcsToOneRow(List<Ititmc> ititmcList, GoodsMoveSaveData.Goods goods) {
        if(ititmcList.size() == 0){
            log.debug("?????? ????????????????????? ??????????????? ?????????.");
            return null;
        }
        Ititmc ititmc = ititmcList.get(0);
        GoodsMoveSaveData.Goods goodsRow = new GoodsMoveSaveData.Goods(ititmc);
        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
        long orderQty = 0l;
        for(Ititmc item : ititmcList){
            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(), ititmc.getItemId());
            orderQty += tbOrderDetailList.stream().map(x-> {
                if (x.getQty() == null) {
                    return 0l;
                } else {
                    return x.getQty();
                }
            }).reduce((a,b)->a+b).get();

            Itasrt itasrt = item.getItasrt();
            goodsRow.setAssortNm(itasrt.getAssortNm());
            goodsRow.setOptionNm(itasrt.getItvariList().get(0).getOptionNm());
        }
        goodsRow.setAvailableQty(ititmcQty - ititmcShipIndQty);
        goodsRow.setOrderQty(orderQty);
        goodsRow.setMoveQty(goods.getMoveQty());

        return goodsRow;
    }

    /**
     * goods ????????? ?????? ?????? data (lsshpd) ???????????? ??????
     */
	private long makeGoodsShipData(String shipId, GoodsMoveSaveData.Goods goods, GoodsMoveSaveData goodsMoveSaveData,
			List<Integer> indexStore, String userId) {
        if(goods ==  null){
            log.debug("this row don't save.");
            return 0l;
        }
        int index = indexStore.get(0);
        long moveQty = goods.getMoveQty();
        for (long i = 0; i < moveQty ; i++) {
            String shipSeq = StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd ??????
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, null, goods, goodsMoveSaveData.getUserId());

			lsshpd.setRegId(userId);
			lsshpd.setUpdId(userId);

            jpaLsshpdRepository.save(lsshpd);
            index++;
            indexStore.remove(0);
            indexStore.add(index);
        }
        return moveQty;
    }

	@Transactional
	public List<String> changeShipStatus2(MoveListSaveData moveListSaveData, String userId) {

		List<String> newShipIdList = new ArrayList<>();
		List<String> shipIdList = new ArrayList<>();


		List<MoveListSaveData.Move> moveList = moveListSaveData.getMoves();
		moveList.stream().forEach(x -> shipIdList.add(x.getShipId()));
		Set<String> shipNoSet = new HashSet(shipIdList);

		List<HashMap<String, Object>> orderList = new ArrayList<>();
		List<Lsshpd> l2 = new ArrayList<Lsshpd>();

		for (String shipId : shipNoSet) {

			Lsshpm lsshpm = jpaLsshpmRepository.findById(shipId).orElse(null);

			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 ??????, 02 ??????
				log.debug("????????????????????? ?????? ???????????????????????????.");
//				lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 ????????????or???????????? 02 ????????????or???????????? ?????? 04 ??????
//				jpaLsshpmRepository.save(lsshpm);
//				continue; // ???????????????????????? ??????????????? ?????????.
			}
			List<Lsshpd> lsshpdList2 = jpaLsshpdRepository.findByShipId(shipId);


			for (Lsshpd lsshpd : lsshpdList2) {

				long shipIndQty = lsshpd.getShipIndicateQty();
				HashMap<String, Object> p = new HashMap<String, Object>();

				p.put("assortId", lsshpd.getAssortId());
				p.put("itemId", lsshpd.getItemId());
				p.put("effStaDt", lsshpd.getExcAppDt());
				p.put("itemGrade", "11");
				p.put("storageId", lsshpm.getStorageId());
				p.put("rackNo", lsshpd.getRackNo());
				p.put("shipQty", shipIndQty);

				int r = jpaStockService.minusShipStockByOrder(p, userId);

				lsshpd.setShipQty(lsshpd.getShipIndicateQty());

				lsshpd.setUpdId(userId);

				jpaLsshpdRepository.save(lsshpd);

				if (lsshpm.getShipOrderGb().equals("01")) {
					// ??????????????????????????? ??????????????? ??????
					l2.add(lsshpd);

					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("order_id", lsshpd.getOrderId());
					m.put("order_seq", lsshpd.getOrderSeq());
					orderList.add(m);

				}



			}

			lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 ????????????
			lsshpm.setApplyDay(LocalDateTime.now()); // ???????????? now date
			newShipIdList.add(lsshpm.getShipId());

			lsshpm.setUpdId(userId);

			jpaLsshpmRepository.save(lsshpm);
			this.updateLsshps(lsshpm, userId);


		}
		// 2022-02-10 ????????????
//=======
//        // lss- ??????
//        for(String shipId : shipNoSet) {
//            // (?????? ????????????) todo ???????????? ????????? ???????????? ???????????? 1???????????? ??????????????? ????????? ???????????? ????????? ??????????????? ???????????????.
//            List<Lsshpd> lsshpdList2 = jpaLsshpdRepository.findByShipIdWithItitmc(shipId);//.get(0);
//            for (Lsshpd lsd : lsshpdList2) {
//                lsshpdList.add(lsd);
//                assortIdList.add(lsd.getAssortId());
//            }
//            Lsshpm lsshpm = lsshpdList2.size() > 0? lsshpdList2.get(0).getLsshpm() : null;//jpaLsshpmRepository.findByShipId(lsshpd.getShipId());
//            if(lsshpm == null){
//                log.debug("there's no data(lsshpm) of shipId : " + shipId);
//                continue;
//            }
//            if(lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 ??????, 02 ??????
//                log.debug("????????????????????? ?????? ???????????????????????????.");
//            }
////            lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 ????????????or???????????? 02 ????????????or???????????? ?????? 04 ??????
////            jpaLsshpmRepository.save(lsshpm);
//            // continue; // ???????????????????????? ??????????????? ?????????.
//        }
//        ititmcList = jpaItitmcRepository.findByAssortIdList(assortIdList);
//        int index = 0;
//        for(Lsshpd lsshpd : lsshpdList){
//            if(!this.ititmcProcess(lsshpd, ititmcList)){
//                continue;
//            }
//            lsshpd.getLsshpm().setShipStatus(StringFactory.getGbFour());
//            jpaLsshpmRepository.save(lsshpd.getLsshpm());
//            this.lsshpdProcess(index, lsshpd, newShipIdList, l2, orderList);
//            index++;
//        }
//>>>>>>> dev
		// 2022-02-10 ?????????

		// ??????????????????
		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C03.toString(), userId);

		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(l2, userId);

		return newShipIdList;
	}

	// 2022-02-10 ????????????
//=======
//
//        return newShipIdList;
//    }
//
//    /**
//     * ???????????? ???????????? ititmc ???????????? ??????
//     */
//    private boolean ititmcProcess(Lsshpd lsshpd, List<Ititmc> ititmcList){
//        // ititmc.shipIndicateQty, ititmc.shipQty ??????
//        long shipIndQty = lsshpd.getShipIndicateQty();
//        List<Ititmc> ititmcList2 = ititmcList.stream().filter(x->x.getEffEndDt().equals(lsshpd.getExcAppDt()) && x.getStorageId().equals(lsshpd.getLsshpm().getStorageId())).collect(Collectors.toList());
//
//        // List<Ititmc> ititmcList =
//        // jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//        // lsshpd.getItemId(), lsshpd.getExcAppDt());
//        // List<Ititmc> ititmcList =
//        // jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//        // lsshpd.getItemId(), lsshpd.getExcAppDt());
////        String assortId = lsshpd.getAssortId();
////        String itemId = lsshpd.getItemId();
//        LocalDateTime excAppDt = lsshpd.getExcAppDt();
//        String storageId = lsshpd.getLsshpm().getStorageId();
//
////        List<Ititmc> ititmcList = lsshpd.getItitmcList().stream().filter(x->x.getEffEndDt().equals(excAppDt) && x.getStorageId().equals(storageId)).collect(Collectors.toList());
////        jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtAndStorageIdOrderByEffEndDtAsc(assortId, itemId, excAppDt, storageId);
//        List<Ititmc> returnList = this.subItitmcQties(excAppDt, storageId, ititmcList2, shipIndQty);
//        return returnList != null && returnList.size() > 0;
//    }
//>>>>>>> dev
	// 2022-02-10 ?????????

    /**
     * ????????????(lsshpm.shipStatus??? 01?????? 04??? ??????)
     * changeShipStatus ??? ?????? ?????????. ?????? changeShipStatus2 ??????.
     */
	// ??????????????? ????????? ?????? 2022-02-07
//    @Transactional
//    public List<String> changeShipStatus(MoveListSaveData moveListSaveData) {
//
//    	
//    	// ???????????? ???????????? ?????? ????????????.
//    	
//    	
//		// todo : ??????????????? ?????? ??????.????????????????????? ??????????????????, ????????????????????? ????????????.??????????????? ???????????? ??????????????????
//
//        List<String> newShipIdList = new ArrayList<>();
//        List<String> shipIdList = new ArrayList<>();
//
//		List<HashMap<String, Object>> orderList = new ArrayList<>();
//
//        List<MoveListSaveData.Move> moveList = moveListSaveData.getMoves();
//        moveList.stream().forEach(x->shipIdList.add(x.getShipId()));
//        Set<String> shipNoSet = new HashSet(shipIdList);
//
//		List<Lsshpd> l2 = new ArrayList<>();
//        List<Ititmc> ititmcList = new ArrayList<>();
//        List<String> assortIdList = new ArrayList<>();
//        List<Lsshpd> lsshpdList = new ArrayList<>();
//
//        // lss- ??????
//        for(String shipId : shipNoSet) {
//            // (?????? ????????????) todo ???????????? ????????? ???????????? ???????????? 1???????????? ??????????????? ????????? ???????????? ????????? ??????????????? ???????????????.
//            List<Lsshpd> lsshpdList2 = jpaLsshpdRepository.findByShipIdWithItitmc(shipId);//.get(0);
//            for (Lsshpd lsd : lsshpdList2) {
//                lsshpdList.add(lsd);
//                assortIdList.add(lsd.getAssortId());
//            }
//            Lsshpm lsshpm = lsshpdList2.size() > 0? lsshpdList2.get(0).getLsshpm() : null;//jpaLsshpmRepository.findByShipId(lsshpd.getShipId());
//            if(lsshpm == null){
//                log.debug("there's no data(lsshpm) of shipId : " + shipId);
//                continue;
//            }
//            if(lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())){ // 01 ??????, 02 ??????
//                log.debug("????????????????????? ?????? ???????????????????????????.");
//                lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 ????????????or???????????? 02 ????????????or???????????? ?????? 04 ??????
//                jpaLsshpmRepository.save(lsshpm);
//                // continue; // ???????????????????????? ??????????????? ?????????.
//            }
//<<<<<<< HEAD
//            // ititmc.shipIndicateQty, ititmc.shipQty ??????
//            long shipIndQty = lsshpd.getShipIndicateQty();
//
//			// List<Ititmc> ititmcList =
//			// jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//			// lsshpd.getItemId(), lsshpd.getExcAppDt());
//			// List<Ititmc> ititmcList =
//			// jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//			// lsshpd.getItemId(), lsshpd.getExcAppDt());
//
//            
//			// ????????????????????? ?????????????????? ????????? ????????? ?????? ?????? ????????? ?????? ?????????.
//			// ??????????????? ?????? ???????????? ??????????????????????????? ?????????????????????.
////20211217
////			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 ??????, 02 ??????
////				log.debug("????????????????????? ?????? ???????????????????????????.");
////
////				List<Ititmc> ititmcList = jpaItitmcRepository
////						.findByAssortIdAndItemIdAndEffEndDtAndStorageIdOrderByEffEndDtAsc(lsshpd.getAssortId(),
////								lsshpd.getItemId(), lsshpd.getExcAppDt(), lsshpm.getStorageId());
////				if (this.subItitmcQties(ititmcList, shipIndQty).size() == 0) {
////					continue;
////				}
////
////			} else {
////
////				HashMap<String, Object> p = new HashMap<String, Object>();
////
////				p.put("assortId", lsshpd.getAssortId());
////				p.put("itemId", lsshpd.getItemId());
////				p.put("effStaDt", lsshpd.getExcAppDt());
////				p.put("itemGrade", "11");
////				p.put("storageId", lsshpm.getStorageId());
////				p.put("rackNo", lsshpd.getRackNo());
////				p.put("shipQty", shipIndQty);
////
////				int r = jpaStockService.minusShipStockByOrder(p);
////
////
////			}
//
//			HashMap<String, Object> p = new HashMap<String, Object>();
//
//			p.put("assortId", lsshpd.getAssortId());
//			p.put("itemId", lsshpd.getItemId());
//			p.put("effStaDt", lsshpd.getExcAppDt());
//			p.put("itemGrade", "11");
//			p.put("storageId", lsshpm.getStorageId());
//			p.put("rackNo", lsshpd.getRackNo());
//			p.put("shipQty", shipIndQty);
//
//			int r = jpaStockService.minusShipStockByOrder(p);
//            
//
//=======
//        }
//        ititmcList = jpaItitmcRepository.findByAssortIdList(assortIdList);
//        int index = 0;
//        for(Lsshpd lsshpd : lsshpdList){
//            if(this.ititmcProcess(lsshpd, ititmcList)){
//                continue;
//            }
//            this.lsshpdProcess(index, lsshpd, newShipIdList, l2, orderList);
//            index++;
//        }
//
//		// ??????????????????
//		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C03.toString());
//
//		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(l2);
//
//        return newShipIdList;
//    }
//
//    /**
//     * ???????????? ???????????? ititmc ???????????? ??????
//     */
//    private boolean ititmcProcess(Lsshpd lsshpd, List<Ititmc> ititmcList){
//        // ititmc.shipIndicateQty, ititmc.shipQty ??????
//        long shipIndQty = lsshpd.getShipIndicateQty();
//        List<Ititmc> ititmcList2 = ititmcList.stream().filter(x->x.getEffEndDt().equals(lsshpd.getExcAppDt()) && x.getStorageId().equals(lsshpd.getLsshpm().getStorageId())).collect(Collectors.toList());
//
//        // List<Ititmc> ititmcList =
//        // jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//        // lsshpd.getItemId(), lsshpd.getExcAppDt());
//        // List<Ititmc> ititmcList =
//        // jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(),
//        // lsshpd.getItemId(), lsshpd.getExcAppDt());
////        String assortId = lsshpd.getAssortId();
////        String itemId = lsshpd.getItemId();
//        LocalDateTime excAppDt = lsshpd.getExcAppDt();
//        String storageId = lsshpd.getLsshpm().getStorageId();
//
////        List<Ititmc> ititmcList = lsshpd.getItitmcList().stream().filter(x->x.getEffEndDt().equals(excAppDt) && x.getStorageId().equals(storageId)).collect(Collectors.toList());
////        jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtAndStorageIdOrderByEffEndDtAsc(assortId, itemId, excAppDt, storageId);
//        return this.subItitmcQties(excAppDt, storageId, ititmcList2, shipIndQty).size() == 0;
//    }
//
//    /**
//     * ???????????? ???????????? lss* ???????????? ??????
//     */
//    private void lsshpdProcess(int index, Lsshpd lsshpd, List<String> newShipIdList, List<Lsshpd> l2, List<HashMap<String, Object>> orderList){
//>>>>>>> dev
////            //
////            // ititmt ?????? ?????? (???????????? ????????? ????????? ititmt??? tempIndicateQty??? tempQty?????? ????????? ???????????? ??????, ???????????? ????????? ????????? ititmt??? tempQty = 0)
////            Ititmt ititmt1 = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndUpdDt(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpd.getOStorageId(),
////                    StringFactory.getStrEleven(), lsshpm.getUpdDt()); // ???????????? ????????? ????????? ititmt
////            ititmt1.setTempIndicateQty(ititmt1.getTempIndicateQty() - shipIndQty);
////            ititmt1.setTempQty(ititmt1.getTempQty() - shipIndQty);
////            Ititmt ititmt2 = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndRegDt(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpm.getOStorageId(),
////                    StringFactory.getStrEleven(), lsshpm.getRegDt()); // ???????????? ????????? ????????? ititmt
////            ititmt2.setTempQty(ititmt2.getTempQty() - shipIndQty);
////            // ititmc ?????? ?????? (????????? ?????????)
////            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus => 01 : ?????? ??????, 04 : ??????
////                Ititmc ititmc = new Ititmc(lsshpd.getOStorageId(), lsshpd.getAssortId(), lsshpd.getItemId(), lsshpd.getLocalPrice(), shipIndQty);
////                jpaItitmcRepository.save(ititmc);
////            }
//        Lsshpm lsshpm = lsshpd.getLsshpm();
//        lsshpd.setShipQty(lsshpd.getShipIndicateQty());
//        if(index == 0){
//            lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 ????????????
//            lsshpm.setApplyDay(LocalDateTime.now()); // ???????????? now date
//        }
//        newShipIdList.add(lsshpm.getShipId());
//        this.updateLssSeries(index, lsshpd);
//
//        if (lsshpm.getShipOrderGb().equals("01")) {
//            // ??????????????????????????? ??????????????? ??????
//            l2.add(lsshpd);
//        }
//
//        if (lsshpm.getShipOrderGb().equals("01")) {
//            HashMap<String, Object> m = new HashMap<String, Object>();
//            m.put("order_id", lsshpd.getOrderId());
//            m.put("order_seq", lsshpd.getOrderSeq());
//            orderList.add(m);
//        }
//    }
//???????????? ????????? ?????? 2022-02-07

    /**
     * lsshpm??? ????????? ????????? ??? lsshpm??? lsshps(??????)??? ??????
     */
    private void updateLsshpms(Lsshpm lsshpm) {
        Lsshps lsshps = jpaLsshpsRepository.findByShipIdAndEffEndDt(lsshpm.getShipId(), Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        lsshps.setEffEndDt(LocalDateTime.now());
        Lsshps newLsshps = new Lsshps(lsshpm);
        jpaLsshpmRepository.save(lsshpm);
        jpaLsshpsRepository.save(lsshps);
        jpaLsshpsRepository.save(newLsshps);
    }

    /**
     * ???????????????????????? ???????????? ??????
     * @param startDt
     * @param endDt
     * @param storageId
     * @param assortId
     * @param assortNm
     * @return ???????????????????????? ?????? DTO
     */
    public MoveIndicateListResponseData getMoveIndicateList(LocalDate startDt, LocalDate endDt, String shipId, String storageId, String oStorageId, String assortId, String assortNm, String deliMethod) {

        List<Lsshpd> lsshpdList = this.getLsshpdMoveIndList(startDt, endDt, shipId, storageId, oStorageId, assortId, assortNm, deliMethod);

        MoveIndicateListResponseData moveIndicateListResponseData = new MoveIndicateListResponseData(startDt, endDt, storageId, oStorageId, assortId, assortNm);
        List<MoveIndicateListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            moveIndicateListResponseData.setOStorageId(lsshpm.getOStorageId());
            // lsshpm??? shipStatus??? ?????? (01 : ????????????or????????????, 04 : ??????)
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){
                continue;
            }
            MoveIndicateListResponseData.Move move = new MoveIndicateListResponseData.Move(lsshpd);
			// Utilities.setOptionNames(move, lsshpd.getItasrt().getItvariList());
			// //2022-02-09 ????????????
            move = moveIndicateListResponseDataMapper.to(move);
            moveList.add(move);
        }
        moveIndicateListResponseData = moveIndicateListResponseDataMapper.to(moveIndicateListResponseData);
        moveIndicateListResponseData.setMoves(moveList);

        return moveIndicateListResponseData;
    }

    /**
     * ????????? ?????? lsshpd ???????????? ???????????? ??????
    */
    private List<Lsshpd> getLsshpdMoveIndList(LocalDate startDt, LocalDate endDt, String shipId, String storageId, String oStorageId, String assortId, String assortNm, String deliMethod) {

        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
//<<<<<<< HEAD
//        TypedQuery<Lsshpd> query = em.createQuery("select ld from Lsshpd ld " +
//                        "join fetch ld.lsshpm lm " +
//                        "left join fetch ld.tbOrderDetail td " +
//                        "join fetch ld.itasrt it " +
//				// "join fetch it.itvariList ivs " +
//				"join fetch ld.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
//				+ "left join fetch itm.itvari3 itv3 " +
//
//                        "where lm.instructDt between ?1 and ?2 " +
//				"and lm.shipStatus ='02' and lm.masterShipGb in ('03', '04')" // ??????????????? ??????
//                ,Lsshpd.class);
//		query.setParameter(1, start).setParameter(2, end);
//        List<Lsshpd> lsshpdList = query.getResultList();
//=======

		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findMoveIndList(start, end, shipId, storageId, oStorageId, assortId,
				assortNm, deliMethod);// query.getResultList();


        return lsshpdList;
    }

    /**
     * ????????????????????? ?????????????????? ???????????? ???????????? ??????
     * @param shipId
     * @return ?????????????????? DTO
     */
    public MoveIndicateDetailResponseData getMoveIndicateDetail(String shipId) {
        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findByShipId(shipId);
        // 03 : ??????????????????, 04 : ????????????????????? ????????? ?????????
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("?????? ????????????????????? ???????????? ????????????.");
            return null;
        }
        Lsshpd lsshpdOne = lsshpdList.get(0);
        Lsshpm lsshpm = lsshpdOne.getLsshpm();
        MoveIndicateDetailResponseData moveIndicateDetailResponseData = new MoveIndicateDetailResponseData(lsshpm);
        moveIndicateDetailResponseData.setOStorageId(lsshpdOne.getOStorageId());
        moveIndicateDetailResponseData.setDealtypeCd(lsshpdOne.getShipGb()); // ??????????????????

		String purchaseNo = "";

        List<MoveIndicateDetailResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){

			Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsshpd.getAssortId());

            Lsdpsd lsdpsd = jpaLsdpsdRepository.findByDepositDt(lsshpd.getExcAppDt()).size() > 0? jpaLsdpsdRepository.findByDepositDt(lsshpd.getExcAppDt()).get(0) : null;
            //lsshpd.getLsdpsdList().stream().filter(x->x.getLsdpsm().getDepositDt().equals(lsshpd.getExcAppDt())).collect(Collectors.toList()).get(0);
            Lspchd lspchd = lsdpsd.getLspchd();

			if (purchaseNo.equals("")) {
				purchaseNo = lspchd.getPurchaseNo();
			}

            MoveIndicateDetailResponseData.Move move = new MoveIndicateDetailResponseData.Move(lsshpd, lsshpm, lspchd);

			move.setWeight(itasrt.getWeight());

			// Utilities.setOptionNames(move,lsshpd.getItasrt().getItvariList());
            moveList.add(move);
        }

		moveIndicateDetailResponseData.setPurchaseNo(purchaseNo);

        moveIndicateDetailResponseData.setMoves(moveList);
        return moveIndicateDetailResponseData;
    }

    /**
     * ???????????? ?????? ????????? ???????????? ????????? ??????????????? ??????
     * @return ???????????? ?????? ?????? DTO
     */
    public MoveListResponseData getMoveList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod, String blNo, LocalDate staEstiArrDt, LocalDate endEstiArrDt) {
        List<Lsshpd> lsshpdList = this.getLsshpdMoveIndList(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod, StringFactory.getGbTwo(), TrdstOrderStatus.C02.toString(), blNo, staEstiArrDt, endEstiArrDt); // shitStatus = 02
        // 03 : ??????????????????, 04 : ????????????????????? ????????? ?????????
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size()==0){
            log.debug("????????? ?????? ???????????? ???????????? ???????????? ????????????.");
        }
        MoveListResponseData moveListResponseData = new MoveListResponseData(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod);
        List<MoveListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // ????????????or????????????(01)??? ????????? ??????(04)??? ??????
                continue;
            }
            MoveListResponseData.Move move = new MoveListResponseData.Move(lsshpm, lsshpd);
			// Utilities.setOptionNames(move, lsshpd.getItasrt().getItvariList());
            moveList.add(move);
        }
        moveListResponseData.setMoves(moveList);
        return moveListResponseData;
    }

    /**
     * ????????? ?????? lsshpd??? ???????????? ?????? (???????????? ?????? ?????????(=?????????????????????) ????????? ??????)
     */
    private List<Lsshpd> getLsshpdMoveIndList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod, String shipStatus, String statusCd, String blNo, LocalDate staEstiArrvDt, LocalDate endEstiArrvDt) {

		System.out.println("getLsshpdMoveIndList");

        LocalDateTime start = startDt == null? Utilities.strToLocalDateTime(StringFactory.getStartDayT()) : startDt.atStartOfDay();
        LocalDateTime end = endDt == null? Utilities.strToLocalDateTime(StringFactory.getDoomDayT()) : endDt.atTime(23,59,59);
        boolean isEstiArrvDtNotExist = staEstiArrvDt == null && endEstiArrvDt == null;
        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findLsshpdMoveIndList(start, end, shipId, assortId, assortNm, storageId, deliMethod, shipStatus, statusCd, blNo, staEstiArrvDt, endEstiArrvDt, isEstiArrvDtNotExist);

        return lsshpdList;
    }

	/**
	 * ????????? ?????? lsshpd??? ???????????? ?????? (??????????????? ????????? ??????)
	 */
	private List<Lsshpd> getLsshpdMoveList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod, String shipStatus, String statusCd, String blNo, LocalDate staEstiArrvDt, LocalDate endEstiArrvDt) {

		System.out.println("getLsshpdMoveList");

		LocalDateTime start = startDt == null? Utilities.strToLocalDateTime(StringFactory.getStartDayT()) : startDt.atStartOfDay();
		LocalDateTime end = endDt == null? Utilities.strToLocalDateTime(StringFactory.getDoomDayT()) : endDt.atTime(23,59,59);
		boolean isEstiArrvDtNotExist = staEstiArrvDt == null && endEstiArrvDt == null;
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findLsshpdMoveList(start, end, shipId, assortId, assortNm, storageId, deliMethod, shipStatus, statusCd, blNo, staEstiArrvDt, endEstiArrvDt, isEstiArrvDtNotExist);

		return lsshpdList;
	}

    /**
     * ??????????????? ??????
     * @param startDt
     * @param endDt
     * @param shipId
     * @param assortId
     * @param assortNm
     * @param storageId
     * @return ??????????????? ?????? ????????? DTO ??????
     */
    public MoveCompletedLIstReponseData getMovedList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String blNo, LocalDate staEstiArrvDt, LocalDate endEstiArrvDt) {
        MoveCompletedLIstReponseData moveCompletedLIstReponseData = new MoveCompletedLIstReponseData(startDt, endDt, shipId, assortId, assortNm, storageId, blNo);
        List<Lsshpd> lsshpdList = this.getLsshpdMoveList(startDt, endDt, shipId, assortId, assortNm, storageId, null, StringFactory.getGbFour(), TrdstOrderStatus.C03.toString(), blNo, staEstiArrvDt, endEstiArrvDt); // shiptStatus = 04
        // lsshpm??? shipStatus??? 04(??????)??? ?????? ?????????
        lsshpdList = lsshpdList.stream().filter(x->x.getLsshpm().getShipStatus().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        List<MoveCompletedLIstReponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){

			System.out.println("**------------------------------------------------------------------------------");
			System.out.println(lsshpd.getRackNo());
            MoveCompletedLIstReponseData.Move move = new MoveCompletedLIstReponseData.Move(lsshpd.getLsshpm(), lsshpd);
			// Utilities.setOptionNames(move, lsshpd.getItasrt().getItvariList());
            move = moveCompletedListResponseDataMapper.nullToEmpty(move);
            moveList.add(move);
        }
        moveCompletedLIstReponseData.setMoves(moveList);
        moveCompletedLIstReponseData = moveCompletedListResponseDataMapper.nullToEmpty(moveCompletedLIstReponseData);
        return moveCompletedLIstReponseData;
    }

    /**
     * ???????????? ??????
     * @return ???????????? DTO ??????
     */
    public MovedDetailResponseData getMovedDetail(String shipId) {
        List<Lsshpd> lsshpdList = em.createQuery("select lsd from Lsshpd lsd " +
                "join fetch lsd.itasrt ita " +
                "join fetch lsd.lsshpm lsm " +
				"join fetch lsd.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
				+ "left join fetch itm.itvari3 itv3 "
				// "join fetch ita.itvariList iv "
				+ "where lsd.shipId=?1", Lsshpd.class)
                .setParameter(1, shipId).getResultList();
        // lsshpm??? shipStatus??? 04(??????)??? ?????? ?????????
        lsshpdList = lsshpdList.stream().filter(x->x.getLsshpm().getShipStatus().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("????????? ?????? ???????????? ????????????.");
            return null;
        }
        MovedDetailResponseData movedDetailResponseData = new MovedDetailResponseData(shipId);
        List<MovedDetailResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            MovedDetailResponseData.Move move = new MovedDetailResponseData.Move(lsshpd.getLsshpm(), lsshpd);
			// Utilities.setOptionNames(move, lsshpd.getItasrt().getItvariList());
            moveList.add(move);
        }
        movedDetailResponseData.setMoves(moveList);
        return movedDetailResponseData;
    }

    /**
     * ??????????????? ?????? - ????????? ??? ?????? ??? ?????? ???????????? ?????? ????????? ??? ??????
     */
    @Transactional
	public void saveExcelList(MoveListExcelRequestData moveListExcelRequestData, String userId) {
        List<MoveListExcelRequestData.Move> moveList = moveListExcelRequestData.getMoves();
        List<String> shipIdList = new ArrayList<>();
        for(MoveListExcelRequestData.Move move : moveList){
            shipIdList.add(move.getShipId());
        }
        if(shipIdList.size() == 0){
            log.debug("?????? shipIdList??? ???????????? ????????????.");
            return;
        }
        List<Lsshpm> lsshpmList = jpaLsshpmRepository.findShipMasterListByShipIdList(shipIdList);
        for(MoveListExcelRequestData.Move move : moveList){
            if(lsshpmList.stream().filter(x->x.getShipId().equals(move.getShipId())).count() == 0){
                log.debug(move.getShipId() + "??? lsshpm??? ???????????? ????????????.");
                continue;
            }
            Lsshpm lsshpm = lsshpmList.stream().filter(x->x.getShipId().equals(move.getShipId())).collect(Collectors.toList()).get(0);//lsshpmList.get(0);
            lsshpm.setBlNo(Utilities.nullOrEmptyFilter(move.getBlNo()));
            lsshpm.setMovementKd(Utilities.nullOrEmptyFilter(move.getMovementKd()));
            lsshpm.setShipmentDt(Utilities.nullOrEmptyFilter(move.getShipmentDt()));
            lsshpm.setEstiArrvDt(Utilities.nullOrEmptyFilter(move.getEstiArrvDt()));
            lsshpm.setContainerKd(Utilities.nullOrEmptyFilter(move.getContainerKd()));
            lsshpm.setContainerQty(Utilities.nullOrEmptyFilter(move.getContainerQty()));
			lsshpm.setUpdId(userId);
            jpaLsshpmRepository.saveAndFlush(lsshpm);
        }
    }


    /**
     * ?????????????????? ????????? ititmc??? qty ?????? ??????????????? ??????
     */

    public List<Ititmc> subItitmcQties(List<Ititmc> ititmcList, long shipQty) {

		throw new IllegalArgumentException("subItitmcQties use ititmc");

//        List<Ititmc> newItitmcList = new ArrayList<>();
//        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
////        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
//        if(ititmcShipIndQty < shipQty){
//            log.debug("???????????? ?????? ?????? ????????? ???????????????.");
//            return newItitmcList;
//        }
//        for(Ititmc ititmc : ititmcList){
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc ?????????
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc ???????????????
////            long canShipQty = qty - shipIndQty; // ???????????????
//            if(shipIndQty < shipQty){ // ?????? ??????
//                continue;
//            }
//            else { // ??? ???????????? ?????? ?????? ??????
//                ititmc.setShipIndicateQty(shipIndQty - shipQty);
//                ititmc.setQty(qty - shipQty);
//                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
//        }
//        if(newItitmcList.size() == 0){
//           log.debug("???????????? ?????? ?????? ????????? ???????????????.");
//        }
//        return newItitmcList;
    }

    //??????????????? ????????? ?????? ?????? ?????????????????? 2022-02-07
//    
//    public List<Ititmc> subItitmcQties(LocalDateTime excAppDt, String storageId, List<Ititmc> ititmcList, long shipQty) {
//        List<Ititmc> newItitmcList = new ArrayList<>();
//        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
////        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
//        if(ititmcShipIndQty < shipQty){
//            log.debug("???????????? ?????? ?????? ????????? ???????????????.");
//            return null;
//        }
//        for(Ititmc ititmc : ititmcList){
//            if(!ititmc.getStorageId().equals(storageId)){
//                continue;
//            }
//            if(excAppDt != null && !ititmc.getEffEndDt().equals(excAppDt)){
//                continue;
//            }
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc ?????????
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc ???????????????
////            long canShipQty = qty - shipIndQty; // ???????????????
//            if(shipIndQty < shipQty){ // ?????? ??????
////                ititmcList.remove(ititmc);
//                continue;
//            }
//            else { // ??? ???????????? ?????? ?????? ??????
//                ititmc.setShipIndicateQty(shipIndQty - shipQty);
//                ititmc.setQty(qty - shipQty);
////                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
//        }
//        if(newItitmcList.size() == 0){
//           log.debug("???????????? ?????? ?????? ????????? ???????????????.");
//        }
////        return ititmcList;
////        for(Ititmc ititmc : newItitmcList){
////            jpaItitmcRepository.save(ititmc);
////        }
//        return newItitmcList;
//>>>>>>> dev
//    }
    //???????????? ????????? ?????? ?????? ?????????????????? 2022-02-07


    /**
     * lsshpd ?????? ??????, lsshpm shipStatus 01->04 ??????, lsshps ???????????? ??????
     */
	public String updateLssSeries(int index, Lsshpd lsshpd, String userId) {
//         3-1. lsshpd ?????? ??????
//        lsshpd.setShipQty(1l);
//        jpaLsshpdRepository.save(lsshpd);
        // 3-2. lsshpm shipStatus 01 -> 04
        Lsshpm lsshpm = lsshpd.getLsshpm();
        if(index == 0){
            lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 : ????????????or????????????, 04 : ??????. 04 ????????????
			lsshpm.setUpdId(userId);
            jpaLsshpmRepository.save(lsshpm);
        }
        // 2-3. lsshps ????????????

		this.updateLsshps(lsshpm, userId);
        return lsshpd.getShipSeq();
    }


    /**
     * Lsshps??? ???????????? ??????
     */
	private void updateLsshps(Lsshpm lsshpm, String userId) {
        Lsshps newLsshps = new Lsshps(lsshpm);
		newLsshps.setRegId(userId);

        Lsshps lsshps = jpaLsshpsRepository.findByShipIdAndEffEndDt(lsshpm.getShipId(), Utilities.strToLocalDateTime2(StringFactory.getDoomDay()));
        lsshps.setEffEndDt(LocalDateTime.now());

		lsshps.setUpdId(userId);

        jpaLsshpsRepository.save(lsshps);

		newLsshps.setUpdId(userId);
        jpaLsshpsRepository.save(newLsshps);
    }


    /**
     * --------------------------------- ?????? ???????????? ????????? ????????? ---------------------------
     */
    /**
     * ititmc list??? shipIndQty??? ??? ????????? ???????????? ?????? (move??? ship?????? ??????)
     */
    public long getItitmcShipIndQtyByStream(List<Ititmc> ititmcList){
        return ititmcList.stream().map(x-> {
            if (x.getShipIndicateQty() == null) {
                return 0l;
            } else {
                return x.getShipIndicateQty();
            }}).reduce((a,b)->a+b).get();
    }

    /**
     * ititmc list??? qty??? ??? ????????? ???????????? ?????? (move??? ship?????? ??????)
     */
    public long getItitmcQtyByStream(List<Ititmc> ititmcList){
        return ititmcList.stream().map(x-> {
            if (x.getQty() == null) {
                return 0l;
            } else {
                return x.getQty();
            }}).reduce((a,b)->a+b).get();
    }

    /**
     * shipId ?????? ??????
     */
    public String getShipId(){
        String shipId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm());
        shipId = Utilities.getStringNo('L',shipId,9);
        return shipId;
    }

	/**
	 * ???????????? ?????? ???????????? ??? ??????????????????
	 */
	private void changeStatusCdOfTbOrderDetail(List<HashMap<String, Object>> list, String statusCd, String userId) {
		for (HashMap<String, Object> o : list) {
//            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
//            if(tbOrderDetail != null){ // 01 : ????????????, 02 : ????????????
//                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
			this.updateOrderStatusCd(o.get("order_id").toString(), o.get("order_seq").toString(), statusCd, userId);
//                jpaTbOrderDetailRepository.save(tbOrderDetail);
//            }
		}
	}

	private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd, String userId) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
		if (tod == null) {
			log.debug("?????? ????????? ???????????? ????????????. - JpaPurchaseService.updateOrderStatusCd");
			return;
		}
        LocalDateTime date = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
		List<TbOrderHistory> tohs = tbOrderHistoryrRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq,
				date);

		tod.setStatusCd(statusCd);

		LocalDateTime newEffEndDate = LocalDateTime.now();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");

			tohs.get(i).setUpdId(userId);
		}

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));

		toh.setRegId(userId);
		toh.setUpdId(userId);

		tohs.add(toh);

		tod.setUpdId(userId);

		tbOrderDetailRepository.save(tod);

		tbOrderHistoryrRepository.saveAll(tohs);
	}
}
