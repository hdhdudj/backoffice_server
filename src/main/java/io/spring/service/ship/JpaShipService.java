package io.spring.service.ship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import io.spring.jparepos.goods.JpaIfGoodsMasterRepository;
import io.spring.model.goods.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.mapstruct.ShipItemListDataMapper;
import io.spring.infrastructure.mapstruct.ShipListDataResponseMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpdsRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpssRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.deposit.entity.Lsdpss;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.model.ship.request.InsertShipEtcRequestData;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.request.ShipSaveListData;
import io.spring.model.ship.response.ShipCandidateListData;
import io.spring.model.ship.response.ShipEtcItemListResponseData;
import io.spring.model.ship.response.ShipEtcItemResponseData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.model.ship.response.ShipItemListData;
import io.spring.model.ship.response.ShipListDataResponse;
import io.spring.service.move.JpaMoveService;
import io.spring.service.stock.JpaStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaShipService {
    private final JpaMoveService jpaMoveService;


	private final JpaStockService jpaStockService;

    private final JpaLspchdRepository jpaLspchdRepository;
	private final JpaIfGoodsMasterRepository jpaIfGoodsMasterRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
	private final JpaLsdpsmRepository jpaLsdpsmRepository;
	private final JpaLsdpsdRepository jpaLsdpsdRepository;
	private final JpaLsdpssRepository jpaLsdpssRepository;
	private final JpaLsdpdsRepository jpaLsdpdsRepository;

	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final EntityManager em;

	private final ShipListDataResponseMapper shipListDataResponseMapper;
	private final ShipItemListDataMapper shipItemListDataMapper;

    /**
     * 출고지시 화면에서 조건검색하면 리스트를 반환해주는 함수
     */
    public ShipIndicateSaveListResponseData getOrderSaveList(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String purchaseVendorId) {
        List<ShipIndicateSaveListResponseData.Ship> shipList = new ArrayList<>();
        List<TbOrderDetail> tbOrderDetailList = this.getOrdersByCondition(startDt, endDt, assortId, assortNm, purchaseVendorId);
//        tbOrderDetailList = tbOrderDetailList.stream().filter(x->
//            x.getStatusCd().equals(StringFactory.getStrC04()) || (x.getStatusCd().equals(StringFactory.getStrC01()) && x.getAssortGb().equals(StringFactory.getGbOne()))
//        ).collect(Collectors.toList());
        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
            List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),tbOrderDetail.getItemId());
            long availableQty = this.calcMaxAvailableQty(ititmcList);
            availableQty = tbOrderDetail.getQty() > availableQty? tbOrderDetail.getQty() : availableQty;
            ShipIndicateSaveListResponseData.Ship ship = new ShipIndicateSaveListResponseData.Ship(tbOrderDetail);
            ship.setAvailableQty(availableQty);
            shipList.add(ship);
            Utilities.setOptionNames(ship, tbOrderDetail.getItitmm().getItasrt().getItvariList());
//            List<Itvari> itvariList = tbOrderDetail.getItasrt().getItvariList();
//            if(itvariList.size() > 0){
//                Itvari itvari1 = itvariList.get(0);
//                ship.setOptionNm1(itvari1.getOptionNm());
//            }
//            if(itvariList.size() > 1){
//                Itvari itvari2 = itvariList.get(1);
//                ship.setOptionNm2(itvari2.getOptionNm());
//            }
        }
		ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = new ShipIndicateSaveListResponseData(
				startDt, endDt, assortId, assortNm, purchaseVendorId, "");
        shipIndicateSaveListResponseData.setShips(shipList);
        return shipIndicateSaveListResponseData;
    }

    /**
     * 해당 ititmcList 중 shipIndicateQty의 최댓값을 반환하는 함수
     */
    private long calcMaxAvailableQty(List<Ititmc> ititmcList) {
        long maxShipIndicateQty = -1;
        for(Ititmc ititmc : ititmcList){
            long shipIndicateQty = ititmc.getShipIndicateQty() == null ? 0l : ititmc.getShipIndicateQty();
            long qty = ititmc.getQty() == null ? 0l : ititmc.getQty();
            long availableQty = qty - shipIndicateQty;
            if(availableQty > maxShipIndicateQty){
                maxShipIndicateQty = availableQty;
            }
        }
        return maxShipIndicateQty;
    }

    /**
     * 출고지시 화면에서 검색 조건에 따른 tbOrderDetail 객체를 가져오는 쿼리를 실행해 결과를 반환하는 함수
     */
    private List<TbOrderDetail> getOrdersByCondition(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String vendorId) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findIndicateShipList(start, end, assortId, vendorId, assortNm, StringFactory.getStrC04());//query.getResultList();
        return tbOrderDetailList;
    }

    /**
     * 출고지시 저장 : 수량 입력 후 저장하는 함수
     */
	@Transactional
	public List<String> saveShipIndicate(ShipIndicateSaveListData shipIndicateSaveListData, String userId) {
        if(shipIndicateSaveListData.getShips().size() == 0){
            log.debug("input data is empty.");
            return null;
        }

        List<String> shipIdList = new ArrayList<>();
        List<String> orderIdList = new ArrayList<>();
		List<HashMap<String, Object>> orderList = new ArrayList<>();

		// List<ShipIndicateSaveListData.Ship> l = shipIndicateSaveListData.getShips();

		// String userId = shipIndicateSaveListData.getUserId();

		for (ShipIndicateSaveListData.Ship ship : shipIndicateSaveListData.getShips()) {

			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("order_id", ship.getOrderId());
			m.put("order_seq", ship.getOrderSeq());

			orderIdList.add(ship.getOrderId());
			orderList.add(m);

			List<String> shipIdList1 = this.saveShipIndicateSaveData(ship, userId);
			if (shipIdList1.size() > 0) {
				shipIdList1.stream().forEach(x -> shipIdList.add(x));
			}
		}

		// addGoods도 추가
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findAddGoodsByOrderIdList(orderIdList, StringFactory.getThreeTwoCd()); // 002 (추가상품) 하드코딩
		for(Lsshpd lsshpd : lsshpdList){
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("order_id", lsshpd.getOrderId());
			m.put("order_seq", lsshpd.getOrderSeq());

			orderList.add(m);
		}

		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.D01.toString(), userId);

        return shipIdList;
    }

	/**
	 * 출고지시 저장 : 수량 입력 후 저장하는 함수
	 */
	public List<String> saveShipIndicateByDeposit(Lsdpsd lsdpsd, String userId) {

		System.out.println("----------------------saveShipIndicateByDeposit----------------------");

		if (lsdpsd == null) {
			log.debug("input data is empty.");
			return null;
		}
		// List<TbOrderDetail> tbOrderDetailList =
		// tbOrderDetailRepository.findByTbOrderDetailWithAddGoods(lsdpsd.getOrderId(),
		// lsdpsd.getOrderSeq());//this.makeTbOrderDetailByShipIndicateSaveListDataByDeposit(lsdpsd);

		TbOrderDetail tbo = tbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
				lsdpsd.getOrderSeq());

		List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();

		tbOrderDetailList.add(tbo);

		// todo: 2022-02-09 이후에 추가상품에 대해서도 자동출고처리되도록 처리해야함.그럴려면 입고부터 자동으로 처리되야함.
		// 추가상품에 대한 자동입고가 안되면 못처리함.


		// List<TbOrderDetail> tbOrderDetailList =
		// tbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
//				lsdpsd.getOrderSeq());

		List<String> shipIdList = new ArrayList<>();

		for (int i = 0; i < tbOrderDetailList.size(); i++) {
			TbOrderDetail tbOrderDetail = tbOrderDetailList.get(i);

			// ShipIndicateSaveListData.Ship ship =
			// shipIndicateSaveListData.getShips().get(i);
			if (lsdpsd.getDepositQty() != tbOrderDetailList.get(i).getQty()) {
				log.debug("주문량보다 더 많이 출고할 수 없습니다.");
				continue;
			}

			Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);

			// ititmc 처리

			// assortId
			// itemId
			// effStaDt
			// itemGrade
			// storageId
			// rackNo
			// qty

			HashMap<String, Object> p = new HashMap<String, Object>();

			p.put("assortId", tbOrderDetail.getAssortId());
			p.put("itemId", tbOrderDetail.getItemId());
			p.put("effStaDt", lsdpsm.getDepositDt());
			p.put("itemGrade", "11");
			p.put("storageId", lsdpsm.getStoreCd());
			p.put("rackNo", lsdpsd.getRackNo());
			p.put("qty", lsdpsd.getDepositQty());

			int r = jpaStockService.minusIndicateStockByOrder(p, userId);



			// 창고의 재고를 조회함
			 Ititmc imc_storage =  jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
						tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsdpsm.getStoreCd(), "11",
						lsdpsm.getDepositDt());

//			);
			// 의 재고를 조회함
			// Ititmc imc_rack =
			// jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
			// tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsdpsd.getRackNo(),
			// "11",
			// lsdpsm.getDepositDt()

			// );

			// List<Ititmc> ititmcList = jpaItitmcRepository
			// // .findByAssortIdAndItemIdOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),
			// // tbOrderDetail.getItemId());
			// .findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),
			// tbOrderDetail.getItemId(), lsdpsm.getStoreCd());
			// // 1. 재고에서 출고 차감 계산
			// ititmcList = this.calcItitmcQties(ititmcList, lsdpsd.getDepositQty()); //
			// 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로
																					// ititmcList에 값이 있다면 한 개만 들어있어야 함)
			// if (ititmcList.size() == 0) {
			// log.debug("출고지시량 이상의 출고가능량을 가진 재고 세트가 없습니다.");
			// continue;
			// }
			// 2. 출고 data 생성
			// todo 출고지시데이타에 rack이 들어가야함.
			String shipId = this.makeShipDataByDeposit(imc_storage, lsdpsd, tbOrderDetail,
					StringFactory.getGbOne(), userId); // 01 :
																													// :
			if (shipId != null) {
				shipIdList.add(shipId);
			}
			// 3. 주문 상태 변경 (C04 -> D01)
			// tbOrderDetail.setStatusCd(StringFactory.getStrD01()); // D01 하드코딩
		}
		return shipIdList;
	}

    /**
     * ititmc의 qty 들에서 주문량만큼을 차감해주는 함수 
     */
    private List<Ititmc> calcItitmcQties(List<Ititmc> ititmcList, long shipIndQty) {
        long ititmcQty = jpaMoveService.getItitmcQtyByStream(ititmcList); // 해당 ititmc 리스트의 총 재고수량
        long ititmcShipIndQty = jpaMoveService.getItitmcShipIndQtyByStream(ititmcList); // 해당 ititmc 리스트의 총 출고예정수량
        long shipAvailQty = ititmcQty - ititmcShipIndQty;
        if(shipAvailQty < shipIndQty){ // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
            log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " + shipIndQty);
            return ititmcList;
        }
        ititmcList = jpaMoveService.calcItitmcQty(ititmcList, shipIndQty);
        return ititmcList;
    }

    /**
     * ShipIndicateSaveListData로부터 TbOrderDetail 리스트를 만들어 반환
     */
    private List<TbOrderDetail> makeTbOrderDetailByShipIndicateSaveListData(ShipIndicateSaveListData shipIndicateSaveData) {
        List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();
        for(ShipIndicateSaveListData.Ship ship:shipIndicateSaveData.getShips()){
            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(ship.getOrderId(),ship.getOrderSeq());
            tbOrderDetailList.add(tbOrderDetail);
        }
        return tbOrderDetailList;
    }

	/**
	 * lsdpsd로부터 TbOrderDetail 리스트를 만들어 반환
	 */
	private List<TbOrderDetail> makeTbOrderDetailByShipIndicateSaveListDataByDeposit(Lsdpsd lsdpsd) {
		List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();

		TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
				lsdpsd.getOrderSeq());
		tbOrderDetailList.add(tbOrderDetail);

		return tbOrderDetailList;
	}

    /**
     * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s)
     * ShipIndicateSaveData 객체로 lsshpm,s,d 생성
     */
	private String makeShipData(Ititmc ititmc, ShipIndicateSaveListData.Ship ship, TbOrderDetail tbOrderDetail,
			String shipStatus, String userId) {
        String shipId = this.getShipId();

        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        // lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);
		lsshpm.setRegId(userId);

        lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 04 : 출고
        lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());
        // lsshps 저장
        Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

        jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
        jpaLsshpmRepository.save(lsshpm);
        // lsshpd 저장
        String shipSeq = StringUtils.leftPad(Integer.toString(1), 4,'0');
        Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);

		lsshpd.setRegId(userId);

//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
        lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
        lsshpd.setShipIndicateQty(ship.getAvailableQty());

		lsshpd.setUpdId(userId);
        jpaLsshpdRepository.save(lsshpd);
        return shipId;
    }


    /**
	 * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s) ShipIndicateSaveData 객체로
	 * lsshpm,s,d 생성
	 */
	private String makeShipDataByDeposit(Ititmc ititmc, Lsdpsd lsdpsd, TbOrderDetail tbOrderDetail, String shipStatus,
			String userId) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setRegId(userId);

		lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipOrderGb(StringFactory.getGbOne());
		lsshpm.setMasterShipGb(StringFactory.getGbOne());

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);

		lsshpm.setStorageId(lsdpsm.getStoreCd());

		// lsshps 저장
		Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

		jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd 저장
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0');
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);

		lsshpd.setRegId(userId);

//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());

		lsshpd.setRackNo(lsdpsd.getRackNo());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
		lsshpd.setShipIndicateQty(lsdpsd.getDepositQty());
		lsshpd.setShipGb(StringFactory.getGbOne()); // 주문출고지시

		lsshpd.setUpdId(userId);
		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

	/**
	 * ShipIndicateSaveData 객체로 lsshpm,s,d 생성 tbOrderDetail를 변경
	 */
	private List<String> saveShipIndicateSaveData(ShipIndicateSaveListData.Ship ship, String userId) {

		List<String> ret = new ArrayList<String>();

//		String shipId = this.getShipId();
		Lsshpm lsshpm = jpaLsshpmRepository.findById(ship.getShipId()).orElseGet(()-> null); //new Lsshpm(shipId, shipIndicateSaveListData);
//		Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq()); //new Lsshpd(ship);

		lsshpm.setInstructDt(LocalDateTime.now());
		lsshpm.setShipStatus(StringFactory.getGbTwo()); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고

		lsshpm.setUpdId(userId);

		this.updateLsshps(lsshpm, userId);

		ret.add(ship.getShipId());

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
	 * 출고 : 출고지시 화면에서 list를 불러오는 함수
	 */
	public ShipCandidateListData getShipCandidateList(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
											   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
											   String storageId, String assortId, String assortNm,
											   String vendorId, String statusCd, String orderKey, String shipStatus) {

//		사용안함
//
//		String orderId = "";
//		String orderSeq = "";
//		if(orderId != null && !orderId.trim().equals("")){
//			String[] order = orderKey.split("-");
//			orderId = order[0];
//			orderSeq = order.length > 1? order[1]:orderSeq;
//		}
//		LocalDateTime start = startDt.atStartOfDay();
//		LocalDateTime end = endDt.atTime(23,59,59);
//		ShipCandidateListData shipCandidateListData = new ShipCandidateListData(startDt, endDt,
//				assortId, assortNm, vendorId, orderId);
//
//		List<Lsdpsd> lsdpsdList = jpaLsdpsdRepository.findShipCandidateList(start, end, assortId, assortNm, vendorId, orderId, orderSeq, storageId);//query.getResultList();
//		lsdpsdList = lsdpsdList.stream().filter(x->x.getLspchd() != null).filter(y->y.getLspchd().getTbOrderDetail() != null).filter(z->z.getLspchd().getTbOrderDetail().getStatusCd().equals(statusCd)).collect(Collectors.toList());
//		List<ShipCandidateListData.Ship> shipList = new ArrayList<>();
//		for(Lsdpsd lsdpsd : lsdpsdList){
//			Ititmm ititmm = lsdpsd.getItitmm();
//			ShipCandidateListData.Ship ship = new ShipCandidateListData.Ship(lsdpsd);
//			Itvari itvari1 = ititmm.getItvari1();
//			Itvari itvari2 = ititmm.getItvari2();
//			Itvari itvari3 = ititmm.getItvari3();
//			List<Itvari> itvariList = new ArrayList<>();
//			itvariList.add(itvari1);
//			itvariList.add(itvari2);
//			itvariList.add(itvari3);
//			Utilities.setOptionNames(ship, itvariList);
//			shipList.add(ship);
//		}
//		shipCandidateListData.setShips(shipList);
		return null;
	}

    /**
     * 출고 : 출고처리 화면에서 list를 불러오는 함수
     */
    public ShipIndicateListData getShipIndList(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
											   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
											   String shipId, String assortId, String assortNm,
											   String vendorId, String statusCd, String orderKey, String shipStatus) {

		String orderId = "";
		String orderSeq = "";
		if(orderId != null && !orderId.trim().equals("")){
			String[] order = orderKey.split("-");
			orderId = order[0];
			orderSeq = order.length > 1? order[1]:orderSeq;
		}
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
		ShipIndicateListData shipIndicateListData = new ShipIndicateListData(start.toLocalDate(), end.toLocalDate(),
				shipId, assortId, assortNm, vendorId, orderId);

        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipIndicateList(start, end, assortId, shipId, assortNm, vendorId, shipStatus, orderId, orderSeq);//query.getResultList();
        lsshpdList = lsshpdList.stream().filter(x->x.getTbOrderDetail().getStatusCd().equals(statusCd)).collect(Collectors.toList());
        List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
		Set<String> assortIdSet = new HashSet<>(); // 고도몰 상품번호 가져오기 위함
		for(Lsshpd lsshpd : lsshpdList){
			assortIdSet.add(lsshpd.getItasrt().getAssortId());
		}
		if(assortIdSet.size() == 0){
			assortIdSet.add("");
		}
		List<IfGoodsMaster> ifGoodsMasterList = jpaIfGoodsMasterRepository.findByAssortIdSet(assortIdSet);
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
			Itasrt itasrt = lsshpd.getItasrt();
			TbOrderDetail tod = lsshpd.getTbOrderDetail();
			TbOrderMaster tom = tod.getTbOrderMaster();
			List<IfGoodsMaster> igmList = ifGoodsMasterList.stream().filter(x->x.getAssortId().equals(itasrt.getAssortId())).collect(Collectors.toList());
			IfGoodsMaster igm = null;
			if(igmList != null && igmList.size() > 0){
				igm = igmList.get(0);
			}

            ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(tod, tom, lsshpm, lsshpd);
            // option set
			// Utilities.setOptionNames(ship,
			// lsshpd.getTbOrderDetail().getItitmm().getItasrt().getItvariList());
			// //2022-02-09 사용안함
            // 출고지시 qty 설정 == 1l
			ship.setChannelGoodsNo(igm != null? igm.getGoodsNo() != null? igm.getGoodsNo() : "" : "");
            ship.setQty(lsshpd.getShipIndicateQty());
            shipList.add(ship);
        }
        shipIndicateListData.setShips(shipList);
        return shipIndicateListData;
    }

	/**
	 *	출고 - 출고리스트
	 */
	public ShipListDataResponse getShipList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String vendorId, String statusCd,String shipStatus, String storageId) {
		ShipListDataResponse shipListDataResponse = new ShipListDataResponse(startDt, endDt, shipId, assortId, assortNm, vendorId);
		String shipId2 = "";
		String shipSeq = "";
		if(shipId != null && !shipId.trim().equals("")){
			String[] shipIdArr = shipId.split("-");
			shipId2 = shipIdArr.length > 1? shipIdArr[0] : shipId;
			shipSeq = shipIdArr.length > 1? shipIdArr[1] : "";
		}
		LocalDateTime start = startDt.atStartOfDay();
		LocalDateTime end = endDt.atTime(23,59,59);
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipList(start, end, shipId2, shipSeq, assortId, assortNm, vendorId, statusCd, storageId);
		List<ShipListDataResponse.Ship> shipList = new ArrayList<>();

		Set<String> assortIdSet = new HashSet<>(); // 고도몰 상품번호 가져오기 위함
		for(Lsshpd lsshpd : lsshpdList){
			assortIdSet.add(lsshpd.getItasrt().getAssortId());
		}
		if(assortIdSet.size() == 0){
			assortIdSet.add("");
		}
		List<IfGoodsMaster> ifGoodsMasterList = jpaIfGoodsMasterRepository.findByAssortIdSet(assortIdSet);

		for(Lsshpd l : lsshpdList){
			ShipListDataResponse.Ship ship = new ShipListDataResponse.Ship(l);
			Itasrt itasrt = l.getItasrt();
			List<IfGoodsMaster> igmList = ifGoodsMasterList.stream().filter(x->x.getAssortId().equals(itasrt.getAssortId())).collect(Collectors.toList());
			IfGoodsMaster igm = null;
			if(igmList != null && igmList.size() > 0){
				igm = igmList.get(0);
			}
			ship.setChannelGoodsNo(igm != null? igm.getGoodsNo() != null? igm.getGoodsNo() : "" : "");
			ship = shipListDataResponseMapper.nullToEmpty(ship);
			shipList.add(ship);
		}
		shipListDataResponse.setShips(shipList);
		shipListDataResponse = shipListDataResponseMapper.nullToEmpty(shipListDataResponse);
		return shipListDataResponse;
	}
//	/**
//	 * 출고 : 출고지시리스트 화면, 출고처리 화면에서 list를 불러오는 함수 출고지시리스트 화면인지 출고처리 화면인지는 statusCd로 구분됨.
//	 * (C04 : 출고지시리스트 화면, D01 : 출고처리 화면)
//	 */
//	public ShipIndicateListData getShipList2(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
//			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt, String shipId, String assortId, String assortNm,
//			String channelId, String statusCd, String orderId) {
//		LocalDateTime start = startDt.atStartOfDay();
//		LocalDateTime end = endDt.atTime(23, 59, 59);
//		ShipIndicateListData shipIndicateListData = new ShipIndicateListData(start.toLocalDate(), end.toLocalDate(),
//				shipId, assortId, assortNm, channelId, orderId);
//		TypedQuery<Lsshpd> query = em.createQuery("select lsd from Lsshpd lsd " + "join fetch lsd.lsshpm lsm "
//				+ "join fetch lsd.tbOrderDetail td "
//				+ "join fetch td.tbOrderMaster tom "
//				+ "join fetch tom.tbMember tm "
//				+ "join fetch td.itasrt it "
//				+ "join fetch it.itvariList iv "
//				+ "where lsm.applyDay between ?1 and ?2 " + "and (?3 is null or trim(?3)='' or td.assortId=?3) "
//				+ "and (?4 is null or trim(?4)='' or lsd.shipId=?4) "
//				+ "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%')) "
//				+ "and (?6 is null or trim(?6)='' or lsd.ownerId=?6)" + "and lsm.shipStatus='04'"
//				+ "and (?7 is null or trim(?7)='' or lsd.orderId=?7)", Lsshpd.class);
//		query.setParameter(1, start).setParameter(2, end).setParameter(3, assortId).setParameter(4, shipId)
//				.setParameter(5, assortNm).setParameter(6, channelId).setParameter(7, orderId);
//		List<Lsshpd> lsshpdList = query.getResultList();
//		List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
//		for (Lsshpd lsshpd : lsshpdList) {
//			Lsshpm lsshpm = lsshpd.getLsshpm();
//			ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(lsshpd.getTbOrderDetail(), lsshpm, lsshpd);
//			// option set
//			Utilities.setOptionNames(ship, lsshpd.getTbOrderDetail().getItasrt().getItvariList());
//			ship.setQty(lsshpd.getShipIndicateQty());
//			shipList.add(ship);
//		}
//		shipIndicateListData.setShips(shipList);
//		return shipIndicateListData;

//	}
	/**
	 * 출고 : 출고리스트 화면에서 검색 후 list를 불러오는 함수.
	 */
//	public ShipListDataResponse getShipList2(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
//											 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt, String shipId, String assortId, String assortNm,
//											 String channelId) {
//		LocalDateTime start = startDt.atStartOfDay();
//		LocalDateTime end = endDt.atTime(23, 59, 59);
//		ShipListDataResponse shipListDataResponse = new ShipListDataResponse(start.toLocalDate(), end.toLocalDate(), shipId, assortId, assortNm, channelId);
//		TypedQuery<Lsshpd> query = em.createQuery("select lsd from Lsshpd lsd " + "join fetch lsd.lsshpm lsm "
//				+ "join fetch lsd.tbOrderDetail td "
//				+ "join fetch td.tbOrderMaster tom "
//				+ "join fetch tom.tbMember tm "
//				+ "join fetch td.itasrt it "
//				+ "join fetch it.itvariList iv "
//				+ "where lsm.applyDay between ?1 and ?2 " + "and (?3 is null or trim(?3)='' or td.assortId=?3) "
//				+ "and (?4 is null or trim(?4)='' or lsd.shipId=?4) "
//				+ "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%')) "
//				+ "and (?6 is null or trim(?6)='' or lsd.ownerId=?6)" + "and lsm.shipStatus='04'"
//				+ "and (?7 is null or trim(?7)='' or lsd.orderId=?7)", Lsshpd.class);
//		query.setParameter(1, start).setParameter(2, end).setParameter(3, assortId).setParameter(4, shipId)
//				.setParameter(5, assortNm).setParameter(6, channelId);
//		List<Lsshpd> lsshpdList = query.getResultList();
//		List<ShipListDataResponse.Ship> shipList = new ArrayList<>();
//		for (Lsshpd lsshpd : lsshpdList) {
//			Lsshpm lsshpm = lsshpd.getLsshpm();
//			ShipListDataResponse.Ship ship = new ShipListDataResponse.Ship(lsshpd.getTbOrderDetail(), lsshpm, lsshpd);
//			// option set
//			Utilities.setOptionNames(ship, lsshpd.getTbOrderDetail().getItasrt().getItvariList());
//			ship.setQty(lsshpd.getShipIndicateQty());
//			shipList.add(ship);
//		}
//		shipListDataResponse.setShips(shipList);
//		return shipListDataResponse;

//	}

    /**
     * 출고 - 출고지시내역 : shipId를 받아 출고마스터와 출고디테일 내역을 반환
     * 출고 - 출고내역 : shipId를 받아 출고내역을 반환
     */
    public ShipItemListData getShipIndicateDetailList(String shipId) {
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipListByShipId(shipId);
		Lsshpm lsshpm;
		if(lsshpdList.size() > 0){
			lsshpm = lsshpdList.get(0).getLsshpm();
		}
		else{
			log.debug("출고내역 : 해당 shipId에 해당하는 출고내역이 없습니다.");
			return null;
		}
        ShipItemListData shipItemListData = new ShipItemListData(lsshpm);
        TbOrderMaster tbOrderMaster = lsshpm.getTbOrderMaster();
        shipItemListData.setOrderDt(Utilities.removeTAndTransToStr(tbOrderMaster.getOrderDate()));
        List<ShipItemListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd:lsshpdList){
			Itasrt itasrt = lsshpd.getItasrt();
			Ititmm ititmm = lsshpd.getTbOrderDetail().getItitmm();
            ShipItemListData.Ship ship = new ShipItemListData.Ship(lsshpd);
			ship.setAssortNm(itasrt.getAssortNm());
			List<Itvari> itvariList = new ArrayList<>();
			itvariList.add(ititmm.getItvari1());
			if(ititmm.getVariationSeq2() != null){
				itvariList.add(ititmm.getItvari2());
			}
			if(ititmm.getVariationSeq3() != null){
				itvariList.add(ititmm.getItvari3());
			}
            // option
			Utilities.setOptionNames(ship, itvariList);
			ship = shipItemListDataMapper.nullToEmpty(ship);
			shipList.add(ship);
		}
		shipItemListData = shipItemListDataMapper.nullToEmpty(shipItemListData);
        shipItemListData.setShips(shipList);
        return shipItemListData;
    }

    
    /**
     * 출고처리2 - 변한 값을 저장하는 함수
     */
    @Transactional
	public List<String> shipIndToShip2(ShipSaveListData shipSaveListData, String userId) {

		List<String> newShipIdList = new ArrayList<>();
		
        List<String> shipIdList = new ArrayList<>();
        List<Lsshpd> lsshpdList = new ArrayList<>();


    	
        List<ShipSaveListData.Ship> shipList = shipSaveListData.getShips();
        
        shipList.stream().forEach(x -> shipIdList.add(x.getShipId()));
        

		Set<String> shipNoSet = new HashSet(shipIdList);
        
    	List<HashMap<String, Object>> orderList = new ArrayList<>();
        
		for (String shipId : shipNoSet) {
			
			
			Lsshpm lsshpm = jpaLsshpmRepository.findById(shipId).orElse(null);

			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 주문, 02 상품
				log.debug("상품출고입니다.");
//				lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 이동지시or출고지시 02 이동지시or출고지시 접수 04 출고
//				jpaLsshpmRepository.save(lsshpm);
//				continue; // 상품이동지시여도 재고처리는 해야함.
			}

			List<Lsshpd> lsshpdList2 = jpaLsshpdRepository.findByShipId(shipId);
			for (Lsshpd lsshpd : lsshpdList2) {
				
				HashMap<String, Object> p = new HashMap<String, Object>();

				p.put("assortId", lsshpd.getAssortId());
				p.put("itemId", lsshpd.getItemId());
				p.put("effStaDt", lsshpd.getExcAppDt());
				p.put("itemGrade", "11");
				p.put("storageId", lsshpm.getStorageId());
				p.put("rackNo", lsshpd.getRackNo());
				p.put("shipQty",lsshpd.getShipIndicateQty());
				p.put("userId", userId);

				int r = jpaStockService.minusShipStockByOrder(p, userId);
				

				lsshpd.setShipQty(lsshpd.getShipIndicateQty());

				lsshpd.setUpdId(userId);


				jpaLsshpdRepository.save(lsshpd);
				
				if (lsshpm.getShipOrderGb().equals("01")) {
					//주문출고에 대한 상태변경처리를 위해 리스트정리
				

					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("order_id", lsshpd.getOrderId());
					m.put("order_seq", lsshpd.getOrderSeq());
					orderList.add(m);

				}

				
			}

			lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 하드코딩
			lsshpm.setApplyDay(LocalDateTime.now()); // 출고일자 now date

			lsshpm.setUpdId(userId);

			newShipIdList.add(lsshpm.getShipId());
			jpaLsshpmRepository.save(lsshpm);
			this.updateLsshps(lsshpm, userId);
			
			
		}
		
		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.D02.toString(), userId);
		
		
		  return newShipIdList;
    }
    
    /**
     * 출고처리 - 변한 값을 저장하는 함수
     * 몬가소스가 이상해서 shipIndToShip2 로 새로 만듬
     */
    //여기서부터 통채로 주석 2022-02-07
    
//    @Transactional
//    public List<String> shipIndToShip(ShipSaveListData shipSaveListData) {
//        List<String> shipIdList = new ArrayList<>();
//        // 1. ititmc의 두 qty에서 처리된 양만큼 빼기
//        List<Lsshpd> lsshpdList = new ArrayList<>();
//        for(ShipSaveListData.Ship ship : shipSaveListData.getShips()){
//            Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq());
//
//            // 수량 완전입고로 변경
//            lsshpd.setShipQty(lsshpd.getShipIndicateQty());
//            Lsshpm lsshpm = lsshpd.getLsshpm();
//            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus가 이미 04(출고)면 패스
//                log.debug("요청된 출고처리 " + Utilities.addDashInMiddle(lsshpd.getShipId(), lsshpd.getShipSeq()) + "는 이미 출고된 상태입니다.");
//                continue;
//            }
//            lsshpm.setApplyDay(LocalDateTime.now());
//            lsshpdList.add(lsshpd);
//            // 2. 해당 tbOrderDetail statusCd 변경
//            TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
//
//            /*
//            <<<<<<< HEAD
//=======
//			List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(
//					tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsshpm.getStorageId());
//            // 재고에서 출고 차감 계산
//            ititmcList = jpaMoveService.subItitmcQties(null, lsshpm.getStorageId(), ititmcList, ship.getQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로 ititmcList에 값이 있다면 한 개만 들어있어야 함)
//            if(ititmcList.size()==0){
//                log.debug("출고처리량 이상의 출고지시량을 가진 재고 세트가 없습니다.");
//                continue;
//            }
//            else {
//>>>>>>> dev
//            
//            */
//            
//
//            
//        	if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 주문, 02 상품
//				log.debug(
//						"**********************************************상품출고처리입니다.확인이 필요합니다!***********************************");
//				// 여기를 타면 오류일거같은데!!정상적인 프로세스에서는 주문이 있을경우에만 출고처리가 가능.그외는 기타 출고
//				
////				
////				List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(
////						tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsshpm.getStorageId());
////	            // 재고에서 출고 차감 계산
////	            ititmcList = jpaMoveService.subItitmcQties(ititmcList, ship.getQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로 ititmcList에 값이 있다면 한 개만 들어있어야 함)
////	            if(ititmcList.size()==0){
////	                log.debug("출고처리량 이상의 출고지시량을 가진 재고 세트가 없습니다.");
////	                continue;
////				} else {
////
////					// 하나로 합쳐도 되지만 그냥 냅둠
////					List<HashMap<String, Object>> orderList = new ArrayList<HashMap<String, Object>>();
////					HashMap<String, Object> m = new HashMap<String, Object>();
////
////					m.put("order_id", lsshpd.getOrderId());
////					m.put("order_seq", lsshpd.getOrderSeq());
////
////					orderList.add(m);
////
////					this.changeStatusCdOfTbOrderDetail(orderList, "D02");
////
////					// tbOrderDetail.setStatusCd(StringFactory.getStrD02()); // D02 하드코딩
////					// jpaTbOrderDetailRepository.save(tbOrderDetail);
////	            }
//	        
//			} else {
//				log.debug("주문출고처리.");
//
//				HashMap<String, Object> p = new HashMap<String, Object>();
//
//				p.put("assortId", lsshpd.getAssortId());
//				p.put("itemId", lsshpd.getItemId());
//				p.put("effStaDt", lsshpd.getExcAppDt());
//				p.put("itemGrade", "11");
//				p.put("storageId", lsshpm.getStorageId());
//				p.put("rackNo", lsshpd.getRackNo());
//				p.put("shipQty",ship.getQty());
//
//				int r = jpaStockService.minusShipStockByOrder(p);
//
//				// 하나로 합쳐도 되지만 그냥 냅둠
//				List<HashMap<String, Object>> orderList = new ArrayList<HashMap<String, Object>>();
//				HashMap<String, Object> m = new HashMap<String, Object>();
//
//				m.put("order_id", lsshpd.getOrderId());
//				m.put("order_seq", lsshpd.getOrderSeq());
//
//				orderList.add(m);
//
//
//				this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.D02.toString());
//
//			}     
//            
//		}
//            
//		
//        // 3. lss- 시리즈 찾아서 수정하고 꺾기
//		int index = 0;
//        for(Lsshpd lsshpd : lsshpdList){
//            shipIdList.add(jpaMoveService.updateLssSeries(index, lsshpd));
//			index++;
//        }
//        return shipIdList;
//    }
    
    //여기까지 통채로 주석 2022-02-07

	private void updateLsshps(Lsshpm lsshpm, String userId) {
		Lsshps lsshps = jpaLsshpsRepository.findByShipIdAndEffEndDt(lsshpm.getShipId(),
				Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
		Lsshps newLsshps = new Lsshps(lsshpm);

		newLsshps.setRegId(userId);

		lsshps.setEffEndDt(LocalDateTime.now());

		lsshps.setUpdId(userId);
		jpaLsshpsRepository.save(lsshps);

		newLsshps.setUpdId(userId);
		jpaLsshpsRepository.save(newLsshps);
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

	/**
	 * shipId 채번 함수
	 */
	private String getShipId(){
		return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
	}

	@Transactional
	public String insertEtcShip(InsertShipEtcRequestData p, String userId) throws Exception {

		// depositNo 채번
		String no = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
		String depositNo = Utilities.getStringNo('D', no, 9);
		Lsdpsm lsdpsm = new Lsdpsm(depositNo, p);

		lsdpsm.setRegId(userId);

		lsdpsm.setUpdId(userId);

		jpaLsdpsmRepository.save(lsdpsm);

		String depositStatus = "01";

		Lsdpss lsdpss = jpaLsdpssRepository.findByDepositNoAndEffEndDt(lsdpsm.getDepositNo(),
				Utilities.getStringToDate(StringFactory.getDoomDay()));

		if (lsdpss == null) {
			Lsdpss newLsdpss = new Lsdpss(lsdpsm, depositStatus);

			newLsdpss.setRegId(userId);

			newLsdpss.setUpdId(userId);

			jpaLsdpssRepository.save(newLsdpss);

		} else {

			lsdpss.setUpdId(userId);
			jpaLsdpssRepository.save(lsdpss);

			Lsdpss newLsdpss = new Lsdpss(lsdpsm, depositStatus);

			newLsdpss.setRegId(userId);

			newLsdpss.setUpdId(userId);

			jpaLsdpssRepository.save(newLsdpss);
		}

		int index = 1;

		List<Lsdpsd> lsdpsdList = new ArrayList<>();

		for (InsertShipEtcRequestData.Item ship : p.getItems()) {

			String depositSeq = StringUtils.leftPad(Integer.toString(index), 4, '0');
			Lsdpsd lsdpsd = new Lsdpsd(lsdpsm, depositSeq, ship);

			lsdpsd.setRegId(userId);

			lsdpsd.setUpdId(userId);

			jpaLsdpsdRepository.save(lsdpsd);

			lsdpsdList.add(lsdpsd);

			index++;
		}

		int ind = lsdpsdList.size();
		List<Lsdpds> lsdpdsList = new ArrayList<>();
		for (int i = 0; i < ind; i++) {
			Lsdpsd lsdpsd = lsdpsdList.get(i);

			Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(),
					lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
			if (lsdpds == null) {
				Lsdpds newLsdpds = new Lsdpds(lsdpsd, depositStatus);

				newLsdpds.setRegId(userId);

				newLsdpds.setUpdId(userId);

				jpaLsdpdsRepository.save(newLsdpds);
			} else {

				lsdpds.setUpdId(userId);

				jpaLsdpdsRepository.save(lsdpds);

				Lsdpds newLsdpds = new Lsdpds(lsdpsd, depositStatus);

				newLsdpds.setRegId(userId);

				newLsdpds.setUpdId(userId);
				jpaLsdpdsRepository.save(newLsdpds);
			}

		}


		// 5.재고입력
		for (Lsdpsd o : lsdpsdList) {

			HashMap<String, Object> m = new HashMap<String, Object>();

			m.put("storageId", p.getStorageId());

			m.put("effStaDt", o.getExcAppDt());
			m.put("assortId", o.getAssortId());
			m.put("itemId", o.getItemId());
			m.put("itemGrade", o.getItemGrade()); // 일단 정상품만 입고
			m.put("shipQty", o.getDepositQty());
			m.put("price", o.getExtraUnitcost());
			m.put("vendorId", p.getVendorId());

			// String rackNo = this.getDefaultRack(p.getStorageId(), o.getRackNo()); //
			// System.out.println("*************************************-----------------------------------------");
			// System.out.println(rackNo);

			m.put("rackNo", o.getRackNo());

			jpaStockService.minusEtcShipStockByGoods(m, userId);

		}

		return lsdpsm.getDepositNo();
	}

	public ShipEtcItemResponseData getShipEtcItem(String etcId, String depositGb) {
		Lsdpsm lsdpsm = jpaLsdpsmRepository.findByDepositNoAndDepositGb(etcId, depositGb);
		List<Lsdpsd> l = jpaLsdpsdRepository.findEtcItem(etcId, depositGb);

		if (lsdpsm == null) {
			return null;
		}

		ShipEtcItemResponseData r = new ShipEtcItemResponseData(lsdpsm);

		List<ShipEtcItemResponseData.Item> items = new ArrayList<>();

		for (Lsdpsd o : l) {
			ShipEtcItemResponseData.Item item = new ShipEtcItemResponseData.Item(o);

			items.add(item);
		}

		r.setItems(items);

		return r;

	}


	public ShipEtcItemListResponseData getShipEtcItems(LocalDate startDt, LocalDate endDt, String depositNo,
			String assortId, String assortNm, String storageId, String depositGb) {

		LocalDateTime start = startDt.atStartOfDay();
		LocalDateTime end = endDt.atTime(23, 59, 59);

		List<Lsdpsd> l = jpaLsdpsdRepository.findEtcItems(start, end, depositNo, depositGb, assortId, assortNm,
				storageId);


		List<ShipEtcItemListResponseData.Item> items = new ArrayList<>();

		ShipEtcItemListResponseData r = new ShipEtcItemListResponseData(startDt, endDt, assortId, assortNm,
				depositNo, depositGb, storageId);

		for (Lsdpsd o : l) {
			ShipEtcItemListResponseData.Item item = new ShipEtcItemListResponseData.Item(o);
			items.add(item);
		}

		r.setItems(items);

		return r;

	}

}
