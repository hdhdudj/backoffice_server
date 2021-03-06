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
	 * 주문 이동지시 대상 리스트 가져오는 함수 2021-10-18 사용 안함 jb
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

//    // lspchd에 tbOrderDetail과 itasrt를 엮어 가져오는 쿼리
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
     * 주문 이동지시 화면에서 검색에 맞는 TbOrderDetail들을 가져오는 함수
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
     * 주문 이동지시 화면에서 검색에 맞는 Lsdpsd들을 가져오는 함수
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
     * 주문 이동지시 저장 함수
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

        // 1. 출고 data 생성
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
        // 2. 발주 data 생성
		// jpaPurchaseService.makePurchaseDataFromOrderMoveSave(lsdpsdList, moveList);
		// 이동처리를 할떄 생성함..2021-10-18

		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C02.toString(), userId);

		// moveList

        return newShipIdList;
    }

	/**
     * 주문 이동지시 저장 함수
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

		// 입고건중에 주문번호가 있는것만 처리함.
		// 입고건에 대해 이동지시를 먼저 만들어놓은후 상태를 수정하는 방법으로 처리

		if (lsdpsd.getOrderId() != null) {

			List<String> shipIdList = this.saveOrderMoveSaveDataByDeposit(lsdpsd, userId);
			if (shipIdList.size() > 0) {
				shipIdList.stream().forEach(x -> newShipIdList.add(x));
			}
		}
		


        // 2. 발주 data 생성
		// 이동처리할떄 발주하는걸로 아예 로직변경
		// jpaPurchaseService.makePurchaseDataFromOrderMoveSaveByDeposit(lsdpsdList,
		// moveList);

		// this.changeStatusCdOfTbOrderDetail(orderList, "C02");

		// moveList

        return newShipIdList;
    }

    /**
     * OrderMoveSaveData객체로 lsshpm,s,d 생성
     * lsdpsm,d,s,b, lsdpsp, ititmt(발주데이터) 생성
     * tbOrderDetail를 변경
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
	 * OrderMoveSaveData객체로 lsshpm,s,d 생성 lsdpsm,d,s,b, lsdpsp, ititmt(발주데이터) 생성
	 * tbOrderDetail를 변경
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
	 * depositNo와 depositSeq로 Lsdpsd를 가져오는 함수 2021-10-18 jb 사용안함
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
     * 주문이동 저장, 출고 관련 data 생성 함수 (lsshpm,d,s)
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
		// // ititmc에서 shipIndicateQty 변경해주기
		// if(qty > ititmc.getQty() - ititmc.getShipIndicateQty()){
		// log.debug("이동가능 재고량이 부족합니다.");
		// return null;
		// }
		// ititmc.setShipIndicateQty(ititmc.getShipIndicateQty() + qty);
		// jpaItitmcRepository.save(ititmc);
//        TbOrderMaster tbOrderMaster = lsdpsd.getLspchd().getLsdpsp().get(0).getTbOrderDetail().getTbOrderMaster();

		// 구매수량하나씩 이동처리하지않고 row단위로 처리
		// for (int i = 0; i < qty; i++) { //수량1개씩처리하던 for문 주석처리
            String shipId = getShipId();
//            Lsdpsp lsdpsp = lsdpsd.getLspchd().getLsdpsp().get(i);
            // lsshpm 저장
			Lsshpm lsshpm = new Lsshpm("03", shipId, itasrt, tbOrderDetail);

			lsshpm.setRegId(userId);

			// ostorageId 가 갈곳 to
			// storageId 가 나오는곳 from

			lsshpm.setStorageId(storageId);
			lsshpm.setOStorageId(tbOrderDetail.getStorageId());

            lsshpm.setShipStatus(shipStatus); // 01 : 이동지시, 04 : 출고
            // lsshps 저장
            Lsshps lsshps = new Lsshps(lsshpm);

			lsshps.setRegId(userId);
			lsshps.setUpdId(userId);

            jpaLsshpsRepository.save(lsshps);

			lsshpm.setUpdId(userId);

            jpaLsshpmRepository.save(lsshpm);
            // lsshpd 저장
            String shipSeq = StringFactory.getFourStartCd(); // 0001 하드코딩 //StringUtils.leftPad(Integer.toString(i + 1), 4,'0');
			Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, imc_storage, itasrt);



			lsshpd.setRackNo(lsdpsd.getRackNo());
			lsshpd.setShipIndicateQty(qty);
			lsshpd.setRegId(userId);
			lsshpd.setUpdId(userId);

            jpaLsshpdRepository.save(lsshpd);
            shipIdList.add(shipId);
			// } //수량1개씩처리하던 for문 주석처리
        return shipIdList;
    }

    /**
     * 상품 선택창 : 상품이동지시 상품추가 모달에서 상품을 선택하고 확인을 눌렀을 때 리스트를 반환
     */
    public GoodsModalListResponseData getGoodsList(String storageId, String purchaseVendorId, String assortId, String assortNm) {

		System.out.println("getGoodsList");
		List<Ititmc> ititmcList = this.getItitmc2(storageId, purchaseVendorId, assortId, assortNm);
        List<GoodsModalListResponseData.Goods> goodsList = new ArrayList<>();
        GoodsModalListResponseData goodsModalListResponseData = new GoodsModalListResponseData(storageId, purchaseVendorId, assortId, assortNm);
        for(Ititmc ititmc : ititmcList){

			GoodsModalListResponseData.Goods goods = new GoodsModalListResponseData.Goods(ititmc);
//	          Itasrt itasrt = ititmc.getItasrt();
//            IfBrand ifBrand = itasrt.getIfBrand();//jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(), itasrt.getBrandId()); // 채널은 01 하드코딩

			// 주문관련 이동지시나 출고지시할떄 indicateqty 에 이미 적용이 되어있으므로 밑에 로직 삭제

//            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(),ititmc.getItemId())
			// .stream().filter(x->x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
			// long qtyOfC01 = tbOrderDetailList.size();

			goods.setOrderQty(0L);
			goods.setAvailableQty(goods.getAvailableQty());
//            goods.setStoreCd(goodsModalListResponseData.getStoreCd());
			// Utilities.setOptionNames(goods, itasrt.getItvariList()); //2022-02-09 사용안함
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
//        goodsList = this.removeDuplicate(goodsList); // goodsKey로 group by
        goodsModalListResponseData.setGoods(goodsList);
        return goodsModalListResponseData;
    }

//    /**
//     * goodsKey로 goodsList를 그루핑하는 함수 (이동가능수량과 해외입고완료 주문수량을 goodsKey별로 더함)
//     * @param goodsList 상품이동지시 화면에 보일 goodsKey로 나뉜 row의 리스트
//     * @return goodsKey로 그루핑 처리된 goodsList
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
//     * 상품 이동지시 : 이동지시일자, 출고센터, 입고센터, 이동방법을 입력한 후 상품추가 버튼을 눌렀을 때 상품 목록을 반환
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
//     * 상품 이동지시 : 이동지시일자, 출고센터, 입고센터, 이동방법을 입력한 후 상품추가 버튼을 눌렀을 때 상품 목록을 반환
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
     * 상품이동지시 화면에서 검색에 맞는 Ititmc들을 가져오는 함수
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
	 * 상품이동지시 화면에서 검색에 맞는 Ititmc들을 가져오는 함수
	 */
	public List<Ititmc> getItitmc2(String storageId, String purchaseVendorId, String assortId, String assortNm) {

		// 랙재고를 가져옴.

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
     * 상품이동지시 저장 함수
     */
    @Transactional
	public List<String> saveGoodsMove(GoodsMoveSaveData goodsMoveSaveData, String userId) {

		// 상품이동지시 수정해야함,

        List<String> shipIdList = new ArrayList<>();

        String regId = null;
        LocalDateTime purchaseDt = null;
        List<Lsshpd> lsshpdList = new ArrayList<>();

        for (GoodsMoveSaveData.Goods goods : goodsMoveSaveData.getGoods()) {
            regId = goodsMoveSaveData.getUserId();

            long moveQty = goods.getMoveQty();
            // 1. 출고 data 생성


//하단에서 재고확인처리를 다시하므로 확인필요없음

//            Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(goods.getAssortId(), goods.getItemId(),
//                    goods.getStorageId(), StringFactory.getStrEleven(), goods.getDepositDt());
//
//			// 상품이동지시할떄 주문건의 수량을 뺴기위해서 넣은 로직인데 이동지시를 그전에 만드는걸로 로직을 변경하여 제외
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
//                log.debug("입력량이 이동가능량보다 큽니다.");
//                continue;
//            }

            if(goods.getMoveQty() == 0){
                log.debug("입력량이 0이어서 저장되지 않습니다.");
                continue;
            }
            

			String StorageId = jpaStockService.getUpStorageId(goods.getStorageId());
			String rackNo = goods.getStorageId();
   
            
			Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
					goods.getAssortId(), goods.getItemId(), rackNo, StringFactory.getStrEleven(),
			// Utilities.dateToLocalDateTime(goods.getDepositDt()));
					goods.getDepositDt());
//
//			// 상품이동지시할떄 주문건의 수량을 뺴기위해서 넣은 로직인데 이동지시를 그전에 만드는걸로 로직을 변경하여 제외
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
//                log.debug("입력량이 이동가능량보다 큽니다.");
//                continue;
//            }

            // 1-0. Lsshpm 생성
            String shipId = this.getShipId();
			Lsshpm lsshpm = new Lsshpm("04", shipId, goodsMoveSaveData);
			lsshpm.setRegId(userId);
            purchaseDt = lsshpm.getReceiptDt();

			lsshpm.setInstructDt(LocalDateTime.now()); // 이동지시일자 셋팅
			lsshpm.setShipStatus("02"); // 이동지시상태로 변경 01 은 이동지시접수

//            // 1-1. ititmc 값 변경
//            if(lsshpm != null){
//                ititmc.setShipIndicateQty(shipIndicateQty + moveQty);
//                ititmc.setUpdId(goodsMoveSaveData.getUserId());
//                jpaItitmcRepository.save(ititmc);
//            }
			
			
            String shipSeq = StringFactory.getFourStartCd(); // 0001 하드코딩 //StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd 생성
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, ititmc, goods, regId);

			lsshpd.setRegId(userId);

			lsshpd.setRackNo(rackNo);

            lsshpd.setOStorageId(goodsMoveSaveData.getOStorageId());
            lsshpd.setShipIndicateQty(moveQty);
            lsshpm.setChannelId(goods.getChannelId()); // vendorId는 바깥에서 set
            lsshpdList.add(lsshpd);
//            jpaLsshpdRepository.save(lsshpd);
            // 1-3. Lsshps 생성
            Lsshps lsshps = new Lsshps(lsshpm, regId);
			lsshps.setRegId(userId);


			lsshpm.setUpdId(userId);

            jpaLsshpmRepository.save(lsshpm);


			lsshps.setUpdId(userId);

            jpaLsshpsRepository.save(lsshps);

            shipIdList.add(shipId);


			// 재고차감처리
			HashMap<String, Object> p = new HashMap<String, Object>();

			p.put("assortId", goods.getAssortId());
			p.put("itemId", goods.getItemId());
			p.put("effStaDt", goods.getDepositDt()); // Utilities.dateToLocalDateTime(goods.getDepositDt()));
			p.put("itemGrade", "11");
			p.put("storageId", StorageId);
			p.put("rackNo", rackNo);
			p.put("qty", moveQty);

			jpaStockService.minusIndicateStockByOrder(p, userId);


			// 2. 발주 data 생성 이동 //발주데이타는 이동처리에서 만드므로 처리안함.
//            jpaPurchaseService.makePurchaseDataFromGoodsMoveSave(regId, purchaseDt, lsshpm, lsshpd);
//            List<Lsdpsp> lsdpspList = new ArrayList<>();
//            lsdpspList.add(lsdpsp);
//
//            // 3. ititmt 수량 변경
//            Ititmc ititmc1 = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(lsshpd.getAssortId(),lsshpd.getItemId(),lsshpm.getStorageId(),
//                    StringFactory.getStrEleven(), Utilities.dateToLocalDateTime(goods.getDepositDt())); // ititmc의 생성 시각과 수정해야 할 ititmt의 수정 시각이 같음.
//            Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndUpdDt(lsshpd.getAssortId(), lsshpd.getItemId(),
//                    lsshpd.getOStorageId(),StringFactory.getStrEleven(),ititmc1.getRegDt());
//            ititmt.setTempIndicateQty(moveQty);
//            jpaItitmtRepository.save(ititmt);
        }

		// 상품이동지시의 경우 이동지시에 걸려있는상황에서 발주가 없으면 ititmc 에만 데이타가 있는거기 떄문에 신규주문에 대한 처리가 안됨 그래서
		// 상품발주는 이동발주를 이동지시와 같이 처리함,
		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(lsshpdList, userId);


        return shipIdList;
    }

    /**
     * 출고 data 생성 함수
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
     * 조건에 맞는 ititmc를 찾아서 반환.
     * 조건 : assortId, itemId
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

        // 2. ititmc qty값 변경
        ititmcList = this.calcItitmcQty(ititmcList, goods.getMoveQty());
        if(ititmcList.size() > 0){
            newGoodsList.add(goods);
        }
        else{
            log.debug("이동할 수 있는 재고가 존재하지 않습니다.");
        }

        GoodsMoveSaveData.Goods rowGoods = this.makeItitmcsToOneRow(ititmcList, goods);
        return rowGoods;
    }

    /**
     * 상품이동지시 저장시 ititmc의 qty 값을 변경 (출고에서도 사용)
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
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc 재고량
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc 출고예정량
//            long canShipQty = qty - shipIndQty; // 출고가능량
//            if(canShipQty <= 0){ // 출고 불가
//                log.debug("출고 또는 이동이 불가합니다.");
//                continue;
//            }
//            if(shipQty <= canShipQty){ // 이 차례에서 출고 완료 가능
//                ititmc.setShipIndicateQty(shipIndQty + shipQty);
//                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
////            else{ // 이 차례에선 출고 풀로 했는데 아직도 출고해야 할 양이 남음
////                shipQty -= canShipQty;
////                ititmc.setShipIndicateQty(qty);
////                jpaItitmcRepository.save(ititmc);
////            }
//        }
//        return newItitmcList;
    }

    /**
     * ititmc 리스트를 받아 상품이동지시 화면의 한 줄에 해당하는 객체로 만드는 함수
     */
    private GoodsMoveSaveData.Goods makeItitmcsToOneRow(List<Ititmc> ititmcList, GoodsMoveSaveData.Goods goods) {
        if(ititmcList.size() == 0){
            log.debug("입력 이동지시수량이 유효값보다 큽니다.");
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
     * goods 정보를 받아 출고 data (lsshpd) 생성하는 함수
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
            // 1-2. Lsshpd 생성
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

			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 주문, 02 상품
				log.debug("주문이동처리가 아닌 상품이동지시입니다.");
//				lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 이동지시or출고지시 02 이동지시or출고지시 접수 04 출고
//				jpaLsshpmRepository.save(lsshpm);
//				continue; // 상품이동지시여도 재고처리는 해야함.
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
					// 주문이동지시일경우 발주데이타 생성
					l2.add(lsshpd);

					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("order_id", lsshpd.getOrderId());
					m.put("order_seq", lsshpd.getOrderSeq());
					orderList.add(m);

				}



			}

			lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 하드코딩
			lsshpm.setApplyDay(LocalDateTime.now()); // 출고일자 now date
			newShipIdList.add(lsshpm.getShipId());

			lsshpm.setUpdId(userId);

			jpaLsshpmRepository.save(lsshpm);
			this.updateLsshps(lsshpm, userId);


		}
		// 2022-02-10 주석시작
//=======
//        // lss- 변경
//        for(String shipId : shipNoSet) {
//            // (일단 수정했음) todo 출고건이 무조건 하나라는 가정으로 1개의건만 가져오는데 이부분 리스트로 받아서 처리되도록 수정해야함.
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
//            if(lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 주문, 02 상품
//                log.debug("주문이동처리가 아닌 상품이동지시입니다.");
//            }
////            lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 이동지시or출고지시 02 이동지시or출고지시 접수 04 출고
////            jpaLsshpmRepository.save(lsshpm);
//            // continue; // 상품이동지시여도 재고처리는 해야함.
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
		// 2022-02-10 주석끝

		// 주문상태변경
		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C03.toString(), userId);

		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(l2, userId);

		return newShipIdList;
	}

	// 2022-02-10 주석시작
//=======
//
//        return newShipIdList;
//    }
//
//    /**
//     * 이동처리 저장에서 ititmc 변경하는 함수
//     */
//    private boolean ititmcProcess(Lsshpd lsshpd, List<Ititmc> ititmcList){
//        // ititmc.shipIndicateQty, ititmc.shipQty 차감
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
	// 2022-02-10 주석끝

    /**
     * 이동처리(lsshpm.shipStatus를 01에서 04로 변경)
     * changeShipStatus 가 너무 이상함. 새로 changeShipStatus2 만듬.
     */
	// 여기서부터 통채로 주석 2022-02-07
//    @Transactional
//    public List<String> changeShipStatus(MoveListSaveData moveListSaveData) {
//
//    	
//    	// 이소스는 이상해서 그냥 리턴시킴.
//    	
//    	
//		// todo : 이동처리후 발주 생성.주문이동지시는 주문상태변경, 상품이동지시는 주문없음.발주시에도 주문없이 만들어지도록
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
//        // lss- 변경
//        for(String shipId : shipNoSet) {
//            // (일단 수정했음) todo 출고건이 무조건 하나라는 가정으로 1개의건만 가져오는데 이부분 리스트로 받아서 처리되도록 수정해야함.
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
//            if(lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())){ // 01 주문, 02 상품
//                log.debug("주문이동처리가 아닌 상품이동지시입니다.");
//                lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 이동지시or출고지시 02 이동지시or출고지시 접수 04 출고
//                jpaLsshpmRepository.save(lsshpm);
//                // continue; // 상품이동지시여도 재고처리는 해야함.
//            }
//<<<<<<< HEAD
//            // ititmc.shipIndicateQty, ititmc.shipQty 차감
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
//			// 주문이동지시랑 상품이동지시 처리에 대해서 재고 처리 방식을 일단 나눴음.
//			// 화면에서도 랙을 선택해서 출고지시하는부분이 있어야할거같음.
////20211217
////			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 주문, 02 상품
////				log.debug("주문이동처리가 아닌 상품이동지시입니다.");
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
//		// 주문상태변경
//		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.C03.toString());
//
//		jpaPurchaseService.makePurchaseDataFromOrderMoveSave2(l2);
//
//        return newShipIdList;
//    }
//
//    /**
//     * 이동처리 저장에서 ititmc 변경하는 함수
//     */
//    private boolean ititmcProcess(Lsshpd lsshpd, List<Ititmc> ititmcList){
//        // ititmc.shipIndicateQty, ititmc.shipQty 차감
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
//     * 이동처리 저장에서 lss* 변경하는 함수
//     */
//    private void lsshpdProcess(int index, Lsshpd lsshpd, List<String> newShipIdList, List<Lsshpd> l2, List<HashMap<String, Object>> orderList){
//>>>>>>> dev
////            //
////            // ititmt 수치 변경 (해외창고 입고시 생성된 ititmt의 tempIndicateQty와 tempQty에서 이동된 숫자만큼 차감, 국내창고 입고시 생성된 ititmt의 tempQty = 0)
////            Ititmt ititmt1 = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndUpdDt(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpd.getOStorageId(),
////                    StringFactory.getStrEleven(), lsshpm.getUpdDt()); // 해외창고 입고시 생성된 ititmt
////            ititmt1.setTempIndicateQty(ititmt1.getTempIndicateQty() - shipIndQty);
////            ititmt1.setTempQty(ititmt1.getTempQty() - shipIndQty);
////            Ititmt ititmt2 = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndRegDt(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpm.getOStorageId(),
////                    StringFactory.getStrEleven(), lsshpm.getRegDt()); // 국내창고 입고시 생성된 ititmt
////            ititmt2.setTempQty(ititmt2.getTempQty() - shipIndQty);
////            // ititmc 새로 생성 (이동인 경우만)
////            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus => 01 : 일반 출고, 04 : 이동
////                Ititmc ititmc = new Ititmc(lsshpd.getOStorageId(), lsshpd.getAssortId(), lsshpd.getItemId(), lsshpd.getLocalPrice(), shipIndQty);
////                jpaItitmcRepository.save(ititmc);
////            }
//        Lsshpm lsshpm = lsshpd.getLsshpm();
//        lsshpd.setShipQty(lsshpd.getShipIndicateQty());
//        if(index == 0){
//            lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 하드코딩
//            lsshpm.setApplyDay(LocalDateTime.now()); // 출고일자 now date
//        }
//        newShipIdList.add(lsshpm.getShipId());
//        this.updateLssSeries(index, lsshpd);
//
//        if (lsshpm.getShipOrderGb().equals("01")) {
//            // 주문이동지시일경우 발주데이타 생성
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
//여기까지 통채로 주석 2022-02-07

    /**
     * lsshpm의 상태가 변했을 때 lsshpm과 lsshps(이력)를 저장
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
     * 이동지시리스트를 가져오는 함수
     * @param startDt
     * @param endDt
     * @param storageId
     * @param assortId
     * @param assortNm
     * @return 이동지시리스트를 가진 DTO
     */
    public MoveIndicateListResponseData getMoveIndicateList(LocalDate startDt, LocalDate endDt, String shipId, String storageId, String oStorageId, String assortId, String assortNm, String deliMethod) {

        List<Lsshpd> lsshpdList = this.getLsshpdMoveIndList(startDt, endDt, shipId, storageId, oStorageId, assortId, assortNm, deliMethod);

        MoveIndicateListResponseData moveIndicateListResponseData = new MoveIndicateListResponseData(startDt, endDt, storageId, oStorageId, assortId, assortNm);
        List<MoveIndicateListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            moveIndicateListResponseData.setOStorageId(lsshpm.getOStorageId());
            // lsshpm의 shipStatus를 확인 (01 : 출고지시or이동지시, 04 : 출고)
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){
                continue;
            }
            MoveIndicateListResponseData.Move move = new MoveIndicateListResponseData.Move(lsshpd);
			// Utilities.setOptionNames(move, lsshpd.getItasrt().getItvariList());
			// //2022-02-09 사용안함
            move = moveIndicateListResponseDataMapper.to(move);
            moveList.add(move);
        }
        moveIndicateListResponseData = moveIndicateListResponseDataMapper.to(moveIndicateListResponseData);
        moveIndicateListResponseData.setMoves(moveList);

        return moveIndicateListResponseData;
    }

    /**
     * 조건에 맞는 lsshpd 리스트를 반환하는 함수
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
//				"and lm.shipStatus ='02' and lm.masterShipGb in ('03', '04')" // 지시상태만 조회
//                ,Lsshpd.class);
//		query.setParameter(1, start).setParameter(2, end);
//        List<Lsshpd> lsshpdList = query.getResultList();
//=======

		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findMoveIndList(start, end, shipId, storageId, oStorageId, assortId,
				assortNm, deliMethod);// query.getResultList();


        return lsshpdList;
    }

    /**
     * 이동지시번호로 이동지시내역 리스트를 가져오는 함수
     * @param shipId
     * @return 이동지시내역 DTO
     */
    public MoveIndicateDetailResponseData getMoveIndicateDetail(String shipId) {
        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findByShipId(shipId);
        // 03 : 주문이동지시, 04 : 상품이동지시인 애들만 남겨둠
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("해당 이동지시내역이 존재하지 않습니다.");
            return null;
        }
        Lsshpd lsshpdOne = lsshpdList.get(0);
        Lsshpm lsshpm = lsshpdOne.getLsshpm();
        MoveIndicateDetailResponseData moveIndicateDetailResponseData = new MoveIndicateDetailResponseData(lsshpm);
        moveIndicateDetailResponseData.setOStorageId(lsshpdOne.getOStorageId());
        moveIndicateDetailResponseData.setDealtypeCd(lsshpdOne.getShipGb()); // 이동지시구분

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
     * 이동처리 화면 조회시 이동지시 목록을 반환해주는 함수
     * @return 이동지시 목록 반환 DTO
     */
    public MoveListResponseData getMoveList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod, String blNo, LocalDate staEstiArrDt, LocalDate endEstiArrDt) {
        List<Lsshpd> lsshpdList = this.getLsshpdMoveIndList(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod, StringFactory.getGbTwo(), TrdstOrderStatus.C02.toString(), blNo, staEstiArrDt, endEstiArrDt); // shitStatus = 02
        // 03 : 주문이동지시, 04 : 상품이동지시인 애들만 남겨둠
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size()==0){
            log.debug("조건에 맞는 이동지시 데이터가 존재하지 않습니다.");
        }
        MoveListResponseData moveListResponseData = new MoveListResponseData(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod);
        List<MoveListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // 출고지시or이동지시(01)가 아니고 출고(04)면 패스
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
     * 조건에 맞는 lsshpd의 리스트를 반환 (이동처리 대상 리스트(=이동지시리스트) 호출시 사용)
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
	 * 조건에 맞는 lsshpd의 리스트를 반환 (이동리스트 호출시 사용)
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
     * 이동리스트 조회
     * @param startDt
     * @param endDt
     * @param shipId
     * @param assortId
     * @param assortNm
     * @param storageId
     * @return 이동리스트 조회 리스트 DTO 반환
     */
    public MoveCompletedLIstReponseData getMovedList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String blNo, LocalDate staEstiArrvDt, LocalDate endEstiArrvDt) {
        MoveCompletedLIstReponseData moveCompletedLIstReponseData = new MoveCompletedLIstReponseData(startDt, endDt, shipId, assortId, assortNm, storageId, blNo);
        List<Lsshpd> lsshpdList = this.getLsshpdMoveList(startDt, endDt, shipId, assortId, assortNm, storageId, null, StringFactory.getGbFour(), TrdstOrderStatus.C03.toString(), blNo, staEstiArrvDt, endEstiArrvDt); // shiptStatus = 04
        // lsshpm의 shipStatus가 04(출고)인 놈만 남기기
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
     * 이동내역 조회
     * @return 이동내역 DTO 반환
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
        // lsshpm의 shipStatus가 04(출고)인 놈만 남기기
        lsshpdList = lsshpdList.stream().filter(x->x.getLsshpm().getShipStatus().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("조건에 맞는 데이터가 없습니다.");
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
     * 이동리스트 화면 - 엑셀에 값 입력 후 엑셀 업로드로 특정 컬럼들 값 저장
     */
    @Transactional
	public void saveExcelList(MoveListExcelRequestData moveListExcelRequestData, String userId) {
        List<MoveListExcelRequestData.Move> moveList = moveListExcelRequestData.getMoves();
        List<String> shipIdList = new ArrayList<>();
        for(MoveListExcelRequestData.Move move : moveList){
            shipIdList.add(move.getShipId());
        }
        if(shipIdList.size() == 0){
            log.debug("조건 shipIdList가 존재하지 않습니다.");
            return;
        }
        List<Lsshpm> lsshpmList = jpaLsshpmRepository.findShipMasterListByShipIdList(shipIdList);
        for(MoveListExcelRequestData.Move move : moveList){
            if(lsshpmList.stream().filter(x->x.getShipId().equals(move.getShipId())).count() == 0){
                log.debug(move.getShipId() + "인 lsshpm은 존재하지 않습니다.");
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
     * 상품이동지시 저장시 ititmc의 qty 값을 차감해주는 함수
     */

    public List<Ititmc> subItitmcQties(List<Ititmc> ititmcList, long shipQty) {

		throw new IllegalArgumentException("subItitmcQties use ititmc");

//        List<Ititmc> newItitmcList = new ArrayList<>();
//        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
////        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
//        if(ititmcShipIndQty < shipQty){
//            log.debug("재고량이 맞지 않아 출고가 불가합니다.");
//            return newItitmcList;
//        }
//        for(Ititmc ititmc : ititmcList){
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc 재고량
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc 출고예정량
////            long canShipQty = qty - shipIndQty; // 출고가능량
//            if(shipIndQty < shipQty){ // 출고 불가
//                continue;
//            }
//            else { // 이 차례에서 출고 완료 가능
//                ititmc.setShipIndicateQty(shipIndQty - shipQty);
//                ititmc.setQty(qty - shipQty);
//                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
//        }
//        if(newItitmcList.size() == 0){
//           log.debug("재고량이 맞지 않아 출고가 불가합니다.");
//        }
//        return newItitmcList;
    }

    //여기서부터 통채로 주석 이건 사용안할것임 2022-02-07
//    
//    public List<Ititmc> subItitmcQties(LocalDateTime excAppDt, String storageId, List<Ititmc> ititmcList, long shipQty) {
//        List<Ititmc> newItitmcList = new ArrayList<>();
//        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
////        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
//        if(ititmcShipIndQty < shipQty){
//            log.debug("재고량이 맞지 않아 출고가 불가합니다.");
//            return null;
//        }
//        for(Ititmc ititmc : ititmcList){
//            if(!ititmc.getStorageId().equals(storageId)){
//                continue;
//            }
//            if(excAppDt != null && !ititmc.getEffEndDt().equals(excAppDt)){
//                continue;
//            }
//            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc 재고량
//            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc 출고예정량
////            long canShipQty = qty - shipIndQty; // 출고가능량
//            if(shipIndQty < shipQty){ // 출고 불가
////                ititmcList.remove(ititmc);
//                continue;
//            }
//            else { // 이 차례에서 출고 완료 가능
//                ititmc.setShipIndicateQty(shipIndQty - shipQty);
//                ititmc.setQty(qty - shipQty);
////                jpaItitmcRepository.save(ititmc);
//                newItitmcList.add(ititmc);
//                break;
//            }
//        }
//        if(newItitmcList.size() == 0){
//           log.debug("재고량이 맞지 않아 출고가 불가합니다.");
//        }
////        return ititmcList;
////        for(Ititmc ititmc : newItitmcList){
////            jpaItitmcRepository.save(ititmc);
////        }
//        return newItitmcList;
//>>>>>>> dev
//    }
    //여기까지 통채로 주석 이건 사용안할것임 2022-02-07


    /**
     * lsshpd 수량 수정, lsshpm shipStatus 01->04 수정, lsshps 꺾어주는 함수
     */
	public String updateLssSeries(int index, Lsshpd lsshpd, String userId) {
//         3-1. lsshpd 수량 수정
//        lsshpd.setShipQty(1l);
//        jpaLsshpdRepository.save(lsshpd);
        // 3-2. lsshpm shipStatus 01 -> 04
        Lsshpm lsshpm = lsshpd.getLsshpm();
        if(index == 0){
            lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 : 출고지시or이동지시, 04 : 출고. 04 하드코딩
			lsshpm.setUpdId(userId);
            jpaLsshpmRepository.save(lsshpm);
        }
        // 2-3. lsshps 꺾어주기

		this.updateLsshps(lsshpm, userId);
        return lsshpd.getShipSeq();
    }


    /**
     * Lsshps를 꺾어주는 함수
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
     * --------------------------------- 해당 서비스의 유틸성 함수들 ---------------------------
     */
    /**
     * ititmc list의 shipIndQty를 다 더해서 반환하는 함수 (move와 ship에서 사용)
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
     * ititmc list의 qty를 다 더해서 반환하는 함수 (move와 ship에서 사용)
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
     * shipId 채번 함수
     */
    public String getShipId(){
        String shipId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm());
        shipId = Utilities.getStringNo('L',shipId,9);
        return shipId;
    }

	/**
	 * 이동지시 또는 이동처리 후 주문상태변경
	 */
	private void changeStatusCdOfTbOrderDetail(List<HashMap<String, Object>> list, String statusCd, String userId) {
		for (HashMap<String, Object> o : list) {
//            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
//            if(tbOrderDetail != null){ // 01 : 주문이동, 02 : 상품이동
//                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
			this.updateOrderStatusCd(o.get("order_id").toString(), o.get("order_seq").toString(), statusCd, userId);
//                jpaTbOrderDetailRepository.save(tbOrderDetail);
//            }
		}
	}

	private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd, String userId) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
		if (tod == null) {
			log.debug("해당 주문이 존재하지 않습니다. - JpaPurchaseService.updateOrderStatusCd");
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
