package io.spring.service.ship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import io.spring.enums.TrdstOrderStatus;
import io.spring.model.ship.response.ShipListDataResponse;
import jdk.vm.ci.meta.Local;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
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
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.request.ShipSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.model.ship.response.ShipItemListData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.move.JpaMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaShipService {
    private final JpaCommonService jpaCommonService;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaMoveService jpaMoveService;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
	private final JpaLsdpsmRepository jpaLsdpsmRepository;

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final EntityManager em;

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
            Utilities.setOptionNames(ship, tbOrderDetail.getItasrt().getItvariList());
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
        // tbOrderDetailList 중 statusCd가 C04인 애들 or statusCd가 C01이면서 assortGb가 01인 애만 가져오기
        TypedQuery<TbOrderDetail> query = em.createQuery("select td from TbOrderDetail td " +
                "join fetch td.tbOrderMaster to " +
                "join fetch td.itasrt it " +
                "where to.orderDate between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or td.assortId=?3) "+
                "and (?4 is null or trim(?4)='' or it.vendorId=?4) "+
                "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%')) " +
                "and td.statusCd=?6"
                , TbOrderDetail.class);
        query.setParameter(1,start).setParameter(2,end)
        .setParameter(3,assortId).setParameter(4,vendorId)
        .setParameter(5,assortNm).setParameter(6,StringFactory.getStrC04());
//        .setParameter(7,StringFactory.getStrC01()).setParameter(8,StringFactory.getGbOne());
        List<TbOrderDetail> tbOrderDetailList = query.getResultList();

        return tbOrderDetailList;
    }

    /**
     * 출고지시 저장 : 수량 입력 후 저장하는 함수
     */
    @Transactional
    public List<String> saveShipIndicate(ShipIndicateSaveListData shipIndicateSaveListData) {
        if(shipIndicateSaveListData.getShips().size() == 0){
            log.debug("input data is empty.");
            return null;
        }


        List<String> shipIdList = new ArrayList<>();
		List<HashMap<String, Object>> orderList = new ArrayList<>();

		// List<ShipIndicateSaveListData.Ship> l = shipIndicateSaveListData.getShips();

		for (ShipIndicateSaveListData.Ship ship : shipIndicateSaveListData.getShips()) {

			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("order_id", ship.getOrderId());
			m.put("order_seq", ship.getOrderSeq());

			orderList.add(m);

			List<String> shipIdList1 = this.saveShipIndicateSaveData(ship);
			if (shipIdList1.size() > 0) {
				shipIdList1.stream().forEach(x -> shipIdList.add(x));
			}
		}

		this.changeStatusCdOfTbOrderDetail(orderList, "D01");

        return shipIdList;
    }

	/**
	 * 출고지시 저장 : 수량 입력 후 저장하는 함수
	 */
	public List<String> saveShipIndicateByDeposit(Lsdpsd lsdpsd) {
		if (lsdpsd == null) {
			log.debug("input data is empty.");
			return null;
		}
		List<TbOrderDetail> tbOrderDetailList = this
				.makeTbOrderDetailByShipIndicateSaveListDataByDeposit(lsdpsd);
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

			List<Ititmc> ititmcList = jpaItitmcRepository
					// .findByAssortIdAndItemIdOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),
					// tbOrderDetail.getItemId());
					.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),
							tbOrderDetail.getItemId(), lsdpsm.getStoreCd());
			// 1. 재고에서 출고 차감 계산
			ititmcList = this.calcItitmcQties(ititmcList, lsdpsd.getDepositQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로
																					// ititmcList에 값이 있다면 한 개만 들어있어야 함)
			if (ititmcList.size() == 0) {
				log.debug("출고지시량 이상의 출고가능량을 가진 재고 세트가 없습니다.");
				continue;
			}
			// 2. 출고 data 생성
			String shipId = this.makeShipDataByDeposit(ititmcList.get(0), lsdpsd, tbOrderDetail,
					StringFactory.getGbOne()); // 01 :
																													// 이동지시or출고지시,
																													// 04
																													// :
																													// 출고
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
    private String makeShipData(Ititmc ititmc, ShipIndicateSaveListData.Ship ship, TbOrderDetail tbOrderDetail, String shipStatus) {
        String shipId = this.getShipId();

        Itasrt itasrt = tbOrderDetail.getItasrt();
        // lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);
        lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 04 : 출고
        lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());
        // lsshps 저장
        Lsshps lsshps = new Lsshps(lsshpm);
        jpaLsshpsRepository.save(lsshps);
        jpaLsshpmRepository.save(lsshpm);
        // lsshpd 저장
        String shipSeq = StringUtils.leftPad(Integer.toString(1), 4,'0');
        Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
        lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
        lsshpd.setShipIndicateQty(ship.getAvailableQty());
        jpaLsshpdRepository.save(lsshpd);
        return shipId;
    }


    /**
	 * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s) ShipIndicateSaveData 객체로
	 * lsshpm,s,d 생성
	 */
	private String makeShipDataByDeposit(Ititmc ititmc, Lsdpsd lsdpsd, TbOrderDetail tbOrderDetail, String shipStatus) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItasrt();
		// lsshpm 저장
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipOrderGb("01");
		lsshpm.setMasterShipGb("01");

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);

		lsshpm.setStorageId(lsdpsm.getStoreCd());

		// lsshps 저장
		Lsshps lsshps = new Lsshps(lsshpm);
		jpaLsshpsRepository.save(lsshps);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd 저장
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0');
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
		lsshpd.setShipIndicateQty(lsdpsd.getDepositQty());
		lsshpd.setShipGb("01"); // 주문출고지시
		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

	/**
	 * ShipIndicateSaveData 객체로 lsshpm,s,d 생성 tbOrderDetail를 변경
	 */
	private List<String> saveShipIndicateSaveData(ShipIndicateSaveListData.Ship ship) {

		List<String> ret = new ArrayList<String>();

		Lsshpm lsshpm = jpaLsshpmRepository.findById(ship.getShipId()).orElse(null);
		Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq());

		lsshpm.setInstructDt(LocalDateTime.now());
		lsshpm.setShipStatus(StringFactory.getGbTwo()); // 01 : 이동지시or출고지시, 02 : 이동지시or출고지시 접수, 04 : 출고

		Lsshps lsshps = new Lsshps(lsshpm);
		this.updateLsshps(lsshps);

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
     * 출고 : 출고지시리스트 화면, 출고처리 화면에서 list를 불러오는 함수
     * 출고지시리스트 화면인지 출고처리 화면인지는 statusCd로 구분됨.
     * (C04 : 출고지시리스트 화면, D01 : 출고처리 화면)
     */
    public ShipIndicateListData getShipIndList(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
											   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
											   String shipId, String assortId, String assortNm,
											   String channelId, String statusCd, String orderKey, String shipStatus) {

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
				shipId, assortId, assortNm, channelId, orderId);
//        start = startDt == null? Utilities.strToLocalDate(StringFactory.getStartDay()) : startDt;
//        end = endDt == null? Utilities.strToLocalDate(StringFactory.getStartDay()) : endDt.plusDays(1);
        TypedQuery<Lsshpd> query = em.createQuery("select lsd from Lsshpd lsd " +
                        "join fetch lsd.lsshpm lsm " +
                        "join fetch lsd.tbOrderDetail td " +
                        "join fetch td.itasrt it "+
				"where lsm.instructDt between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or td.assortId=?3) " +
                        "and (?4 is null or trim(?4)='' or lsd.shipId=?4) " +
                        "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%')) " +
				"and (?6 is null or trim(?6)='' or lsd.ownerId=?6)" + "and lsm.shipStatus=:shipStatus "
				+ "and (?7 is null or trim(?7)='' or lsd.orderId=?7)"
				+ "and (?8 is null or trim(?7)='' or lsd.orderSeq=?8)"
                ,Lsshpd.class);
        query.setParameter(1, start).setParameter(2, end)
                .setParameter(3,assortId).setParameter(4,shipId)
				.setParameter(5, assortNm).setParameter(6, channelId).setParameter(7, orderId)
				.setParameter(8, orderSeq).setParameter(StringFactory.getStrShipStatus(), shipStatus);
        List<Lsshpd> lsshpdList = query.getResultList();
        // 출고지시리스트 : C04, 출고처리리스트 : D01, 출고리스트 statusCd = D02인 애들만 남기기
        lsshpdList = lsshpdList.stream().filter(x->x.getTbOrderDetail().getStatusCd().equals(statusCd)).collect(Collectors.toList());
        List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(lsshpd.getTbOrderDetail(), lsshpm, lsshpd);
            // option set
            Utilities.setOptionNames(ship, lsshpd.getTbOrderDetail().getItasrt().getItvariList());
            // 출고지시 qty 설정 == 1l
            ship.setQty(lsshpd.getShipIndicateQty());
            shipList.add(ship);
        }
        shipIndicateListData.setShips(shipList);
        return shipIndicateListData;
    }

	/**
	 *	출고 - 출고리스트
	 */
	public ShipListDataResponse getShipList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String vendorId, String statusCd, String orderKey, String shipStatus) {
		ShipListDataResponse shipListDataResponse = new ShipListDataResponse(startDt, endDt, shipId, assortId, assortNm, vendorId);
		String shipSeq = "";
		if(shipId != null && !shipId.trim().equals("")){
			String[] shipIdArr = shipId.split("-");
			orderKey = shipIdArr.length > 1? orderKey : orderKey;
			shipSeq = !orderKey.trim().equals("")? shipIdArr[1] : "";
		}
		LocalDateTime start = startDt.atStartOfDay();
		LocalDateTime end = endDt.atTime(23,59,59);
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipList(start, end, shipId, shipSeq, assortId, assortNm, vendorId, statusCd);
		List<ShipListDataResponse.Ship> shipList = new ArrayList<>();
		for(Lsshpd l : lsshpdList){
			ShipListDataResponse.Ship ship = new ShipListDataResponse.Ship(l);
			shipList.add(ship);
		}
		shipListDataResponse.setShips(shipList);
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
		Lsshpm lsshpm = jpaLsshpmRepository.findByShipId(shipId);
        ShipItemListData shipItemListData = new ShipItemListData(lsshpm);
        TbOrderMaster tbOrderMaster = lsshpm.getTbOrderMaster();
        shipItemListData.setOrderDt(Utilities.removeTAndTransToStr(tbOrderMaster.getOrderDate()));
        List<Lsshpd> lsshpdList = lsshpm.getLsshpdList();
        List<ShipItemListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd:lsshpdList){
            ShipItemListData.Ship ship = new ShipItemListData.Ship(lsshpd);
            // option
			Utilities.setOptionNames(ship, lsshpd.getTbOrderDetail().getItasrt().getItvariList());
            shipList.add(ship);
        }
        shipItemListData.setShips(shipList);
        return shipItemListData;
    }

    /**
     * 출고처리 - 변한 값을 저장하는 함수
     */
    @Transactional
    public List<String> shipIndToShip(ShipSaveListData shipSaveListData) {
        List<String> shipIdList = new ArrayList<>();
        // 1. ititmc의 두 qty에서 처리된 양만큼 빼기
        List<Lsshpd> lsshpdList = new ArrayList<>();
        for(ShipSaveListData.Ship ship : shipSaveListData.getShips()){
            Lsshpd lsshpd = em.createQuery("select ld from Lsshpd ld join fetch ld.tbOrderDetail to where ld.shipId=?1 and ld.shipSeq=?2",Lsshpd.class)
                    .setParameter(1,ship.getShipId())
                    .setParameter(2, ship.getShipSeq()).getSingleResult();//jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq());

            // 수량 완전입고로 변경
            lsshpd.setShipQty(lsshpd.getShipIndicateQty());
            Lsshpm lsshpm = lsshpd.getLsshpm();
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus가 이미 04(출고)면 패스
                log.debug("요청된 출고처리 " + Utilities.addDashInMiddle(lsshpd.getShipId(), lsshpd.getShipSeq()) + "는 이미 출고된 상태입니다.");
                continue;
            }
            lsshpm.setApplyDay(LocalDateTime.now());
            lsshpdList.add(lsshpd);
            // 2. 해당 tbOrderDetail statusCd 변경
            TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
			List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(
					tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsshpm.getStorageId());
            // 재고에서 출고 차감 계산
            ititmcList = jpaMoveService.subItitmcQties(ititmcList, ship.getQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로 ititmcList에 값이 있다면 한 개만 들어있어야 함)
            if(ititmcList.size()==0){
                log.debug("출고처리량 이상의 출고지시량을 가진 재고 세트가 없습니다.");
                continue;
            }
            else {

				List<HashMap<String, Object>> orderList = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> m = new HashMap<String, Object>();

				m.put("order_id", lsshpd.getOrderId());
				m.put("order_seq", lsshpd.getOrderSeq());

				orderList.add(m);

				this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.D02.toString());

				// tbOrderDetail.setStatusCd(StringFactory.getStrD02()); // D02 하드코딩
				// jpaTbOrderDetailRepository.save(tbOrderDetail);
            }
        }
        // 3. lss- 시리즈 찾아서 수정하고 꺾기
        for(Lsshpd lsshpd : lsshpdList){
            shipIdList.add(jpaMoveService.updateLssSeries(lsshpd));
        }
        return shipIdList;
    }

	private void updateLsshps(Lsshps newLsshps) {
		Lsshps lsshps = jpaLsshpsRepository.findByShipIdAndEffEndDt(newLsshps.getShipId(),
				Utilities.getStringToDate(StringFactory.getDoomDay()));
		lsshps.setEffEndDt(new Date());
		jpaLsshpsRepository.save(lsshps);
		jpaLsshpsRepository.save(newLsshps);
	}

	/**
	 * 이동지시 또는 이동처리 후 주문상태변경
	 */
	private void changeStatusCdOfTbOrderDetail(List<HashMap<String, Object>> list, String statusCd) {
		for (HashMap<String, Object> o : list) {
//            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
//            if(tbOrderDetail != null){ // 01 : 주문이동, 02 : 상품이동
//                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
			this.updateOrderStatusCd(o.get("order_id").toString(), o.get("order_seq").toString(), statusCd);
//                jpaTbOrderDetailRepository.save(tbOrderDetail);
//            }
		}
	}

	private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
		if (tod == null) {
			log.debug("해당 주문이 존재하지 않습니다. - JpaPurchaseService.updateOrderStatusCd");
			return;
		}
		Date date = Utilities.getStringToDate(StringFactory.getDoomDay());
		List<TbOrderHistory> tohs = tbOrderHistoryrRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq,
				date);

		tod.setStatusCd(statusCd);

		Date newEffEndDate = new Date();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
		}

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.getStringToDate(StringFactory.getDoomDay()));

		tohs.add(toh);

		tbOrderDetailRepository.save(tod);

		tbOrderHistoryrRepository.saveAll(tohs);
	}

	/**
	 * shipId 채번 함수
	 */
	private String getShipId(){
		return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
	}
}
