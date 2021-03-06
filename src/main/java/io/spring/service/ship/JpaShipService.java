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
     * ???????????? ???????????? ?????????????????? ???????????? ??????????????? ??????
     */
	public ShipIndicateSaveListResponseData getOrderSaveList(LocalDate startDt, LocalDate endDt, String assortId,
			String assortNm, String purchaseVendorId, String channelGoodsNo) {
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
				startDt, endDt, assortId, assortNm, purchaseVendorId, "", channelGoodsNo);
        shipIndicateSaveListResponseData.setShips(shipList);
        return shipIndicateSaveListResponseData;
    }

    /**
     * ?????? ititmcList ??? shipIndicateQty??? ???????????? ???????????? ??????
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
     * ???????????? ???????????? ?????? ????????? ?????? tbOrderDetail ????????? ???????????? ????????? ????????? ????????? ???????????? ??????
     */
    private List<TbOrderDetail> getOrdersByCondition(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String vendorId) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findIndicateShipList(start, end, assortId, vendorId, assortNm, StringFactory.getStrC04());//query.getResultList();
        return tbOrderDetailList;
    }

    /**
     * ???????????? ?????? : ?????? ?????? ??? ???????????? ??????
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

		// addGoods??? ??????
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findAddGoodsByOrderIdList(orderIdList, StringFactory.getThreeTwoCd()); // 002 (????????????) ????????????
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
	 * ???????????? ?????? : ?????? ?????? ??? ???????????? ??????
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

		// todo: 2022-02-09 ????????? ??????????????? ???????????? ??????????????????????????? ???????????????.???????????? ???????????? ???????????? ???????????????.
		// ??????????????? ?????? ??????????????? ????????? ????????????.


		// List<TbOrderDetail> tbOrderDetailList =
		// tbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
//				lsdpsd.getOrderSeq());

		List<String> shipIdList = new ArrayList<>();

		for (int i = 0; i < tbOrderDetailList.size(); i++) {
			TbOrderDetail tbOrderDetail = tbOrderDetailList.get(i);

			// ShipIndicateSaveListData.Ship ship =
			// shipIndicateSaveListData.getShips().get(i);
			if (lsdpsd.getDepositQty() != tbOrderDetailList.get(i).getQty()) {
				log.debug("??????????????? ??? ?????? ????????? ??? ????????????.");
				continue;
			}

			Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);

			// ititmc ??????

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



			// ????????? ????????? ?????????
			 Ititmc imc_storage =  jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
						tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsdpsm.getStoreCd(), "11",
						lsdpsm.getDepositDt());

//			);
			// ??? ????????? ?????????
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
			// // 1. ???????????? ?????? ?????? ??????
			// ititmcList = this.calcItitmcQties(ititmcList, lsdpsd.getDepositQty()); //
			// ??????????????? ???????????? (????????? ititmc?????? ?????? ???????????????
																					// ititmcList??? ?????? ????????? ??? ?????? ??????????????? ???)
			// if (ititmcList.size() == 0) {
			// log.debug("??????????????? ????????? ?????????????????? ?????? ?????? ????????? ????????????.");
			// continue;
			// }
			// 2. ?????? data ??????
			// todo ???????????????????????? rack??? ???????????????.
			String shipId = this.makeShipDataByDeposit(imc_storage, lsdpsd, tbOrderDetail,
					StringFactory.getGbOne(), userId); // 01 :
																													// :
			if (shipId != null) {
				shipIdList.add(shipId);
			}
			// 3. ?????? ?????? ?????? (C04 -> D01)
			// tbOrderDetail.setStatusCd(StringFactory.getStrD01()); // D01 ????????????
		}
		return shipIdList;
	}

    /**
     * ititmc??? qty ????????? ?????????????????? ??????????????? ?????? 
     */
    private List<Ititmc> calcItitmcQties(List<Ititmc> ititmcList, long shipIndQty) {
        long ititmcQty = jpaMoveService.getItitmcQtyByStream(ititmcList); // ?????? ititmc ???????????? ??? ????????????
        long ititmcShipIndQty = jpaMoveService.getItitmcShipIndQtyByStream(ititmcList); // ?????? ititmc ???????????? ??? ??????????????????
        long shipAvailQty = ititmcQty - ititmcShipIndQty;
        if(shipAvailQty < shipIndQty){ // ititmc??? ?????? ?????? ?????? ???????????? ???????????? ?????? ??????
            log.debug("???????????? ???????????? ??????????????? ????????????. ???????????? ????????? : " + shipAvailQty + ", ????????? : " + shipIndQty);
            return ititmcList;
        }
        ititmcList = jpaMoveService.calcItitmcQty(ititmcList, shipIndQty);
        return ititmcList;
    }

    /**
     * ShipIndicateSaveListData????????? TbOrderDetail ???????????? ????????? ??????
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
	 * lsdpsd????????? TbOrderDetail ???????????? ????????? ??????
	 */
	private List<TbOrderDetail> makeTbOrderDetailByShipIndicateSaveListDataByDeposit(Lsdpsd lsdpsd) {
		List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();

		TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lsdpsd.getOrderId(),
				lsdpsd.getOrderSeq());
		tbOrderDetailList.add(tbOrderDetail);

		return tbOrderDetailList;
	}

    /**
     * ?????? ?????? ??? update, ?????? ?????? data ?????? ?????? (lsshpm,d,s)
     * ShipIndicateSaveData ????????? lsshpm,s,d ??????
     */
	private String makeShipData(Ititmc ititmc, ShipIndicateSaveListData.Ship ship, TbOrderDetail tbOrderDetail,
			String shipStatus, String userId) {
        String shipId = this.getShipId();

        Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
        // lsshpm ??????
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);
		lsshpm.setRegId(userId);

        lsshpm.setShipStatus(shipStatus); // 01 : ????????????or????????????, 04 : ??????
        lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());
        // lsshps ??????
        Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

        jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
        jpaLsshpmRepository.save(lsshpm);
        // lsshpd ??????
        String shipSeq = StringUtils.leftPad(Integer.toString(1), 4,'0');
        Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);

		lsshpd.setRegId(userId);

//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
        lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : ??????, 02 : ??????, 03 : ????????????
        lsshpd.setShipIndicateQty(ship.getAvailableQty());

		lsshpd.setUpdId(userId);
        jpaLsshpdRepository.save(lsshpd);
        return shipId;
    }


    /**
	 * ?????? ?????? ??? update, ?????? ?????? data ?????? ?????? (lsshpm,d,s) ShipIndicateSaveData ?????????
	 * lsshpm,s,d ??????
	 */
	private String makeShipDataByDeposit(Ititmc ititmc, Lsdpsd lsdpsd, TbOrderDetail tbOrderDetail, String shipStatus,
			String userId) {
		String shipId = this.getShipId();

		Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
		// lsshpm ??????
		Lsshpm lsshpm = new Lsshpm("01", shipId, itasrt, tbOrderDetail);

		lsshpm.setRegId(userId);

		lsshpm.setShipStatus(shipStatus); // 01 : ????????????or????????????, 02 : ????????????or???????????? ??????, 04 : ??????
		lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());

		lsshpm.setShipOrderGb(StringFactory.getGbOne());
		lsshpm.setMasterShipGb(StringFactory.getGbOne());

		// lsshpm.setOStorageId(tbOrderDetail.getStorageId());

		Lsdpsm lsdpsm = jpaLsdpsmRepository.findById(lsdpsd.getDepositNo()).orElse(null);

		lsshpm.setStorageId(lsdpsm.getStoreCd());

		// lsshps ??????
		Lsshps lsshps = new Lsshps(lsshpm);

		lsshps.setRegId(userId);

		lsshps.setUpdId(userId);

		jpaLsshpsRepository.save(lsshps);

		lsshpm.setUpdId(userId);
		jpaLsshpmRepository.save(lsshpm);
		// lsshpd ??????
		String shipSeq = StringUtils.leftPad(Integer.toString(1), 4, '0');
		Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);

		lsshpd.setRegId(userId);

//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());

		lsshpd.setRackNo(lsdpsd.getRackNo());
		lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : ??????, 02 : ??????, 03 : ????????????
		lsshpd.setShipIndicateQty(lsdpsd.getDepositQty());
		lsshpd.setShipGb(StringFactory.getGbOne()); // ??????????????????

		lsshpd.setUpdId(userId);
		jpaLsshpdRepository.save(lsshpd);
		return shipId;
	}

	/**
	 * ShipIndicateSaveData ????????? lsshpm,s,d ?????? tbOrderDetail??? ??????
	 */
	private List<String> saveShipIndicateSaveData(ShipIndicateSaveListData.Ship ship, String userId) {

		List<String> ret = new ArrayList<String>();

//		String shipId = this.getShipId();
		Lsshpm lsshpm = jpaLsshpmRepository.findById(ship.getShipId()).orElseGet(()-> null); //new Lsshpm(shipId, shipIndicateSaveListData);
//		Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq()); //new Lsshpd(ship);

		lsshpm.setInstructDt(LocalDateTime.now());
		lsshpm.setShipStatus(StringFactory.getGbTwo()); // 01 : ????????????or????????????, 02 : ????????????or???????????? ??????, 04 : ??????

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
     * ?????? : ?????????????????????, ???????????? ???????????? list??? ???????????? ??????
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

		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipIndicateList(start, end, assortId, shipId, assortNm,
				vendorId, shipStatus, orderId, orderSeq);// query.getResultList();
        lsshpdList = lsshpdList.stream().filter(x->x.getTbOrderDetail().getStatusCd().equals(statusCd)).collect(Collectors.toList());
        List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
			Itasrt itasrt = lsshpd.getItasrt();
			TbOrderDetail tod = lsshpd.getTbOrderDetail();
			TbOrderMaster tom = tod.getTbOrderMaster();

            ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(tod, tom, lsshpm, lsshpd);
            // option set
			// Utilities.setOptionNames(ship,
			// lsshpd.getTbOrderDetail().getItitmm().getItasrt().getItvariList());
			// //2022-02-09 ????????????
            // ???????????? qty ?????? == 1l
			ship.setChannelGoodsNo(itasrt.getChannelGoodsNo() ==  null? "":itasrt.getChannelGoodsNo());
            ship.setQty(lsshpd.getShipIndicateQty());
            shipList.add(ship);
        }
        shipIndicateListData.setShips(shipList);
        return shipIndicateListData;
    }

	/**
	 *	?????? - ???????????????
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

		for(Lsshpd l : lsshpdList){
			ShipListDataResponse.Ship ship = new ShipListDataResponse.Ship(l);
			Itasrt itasrt = l.getItasrt();
			ship.setChannelGoodsNo(itasrt.getChannelGoodsNo() ==  null? "":itasrt.getChannelGoodsNo());
			ship = shipListDataResponseMapper.nullToEmpty(ship);
			shipList.add(ship);
		}
		shipListDataResponse.setShips(shipList);
		shipListDataResponse = shipListDataResponseMapper.nullToEmpty(shipListDataResponse);
		return shipListDataResponse;
	}
//	/**
//	 * ?????? : ????????????????????? ??????, ???????????? ???????????? list??? ???????????? ?????? ????????????????????? ???????????? ???????????? ??????????????? statusCd??? ?????????.
//	 * (C04 : ????????????????????? ??????, D01 : ???????????? ??????)
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
	 * ?????? : ??????????????? ???????????? ?????? ??? list??? ???????????? ??????.
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
     * ?????? - ?????????????????? : shipId??? ?????? ?????????????????? ??????????????? ????????? ??????
     * ?????? - ???????????? : shipId??? ?????? ??????????????? ??????
     */
    public ShipItemListData getShipIndicateDetailList(String shipId) {
		List<Lsshpd> lsshpdList = jpaLsshpdRepository.findShipListByShipId(shipId);
		Lsshpm lsshpm;
		if(lsshpdList.size() > 0){
			lsshpm = lsshpdList.get(0).getLsshpm();
		}
		else{
			log.debug("???????????? : ?????? shipId??? ???????????? ??????????????? ????????????.");
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
     * ????????????2 - ?????? ?????? ???????????? ??????
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

			if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 ??????, 02 ??????
				log.debug("?????????????????????.");
//				lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 ????????????or???????????? 02 ????????????or???????????? ?????? 04 ??????
//				jpaLsshpmRepository.save(lsshpm);
//				continue; // ???????????????????????? ??????????????? ?????????.
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
					//??????????????? ?????? ????????????????????? ?????? ???????????????
				

					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("order_id", lsshpd.getOrderId());
					m.put("order_seq", lsshpd.getOrderSeq());
					orderList.add(m);

				}

				
			}

			lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 ????????????
			lsshpm.setApplyDay(LocalDateTime.now()); // ???????????? now date

			lsshpm.setUpdId(userId);

			newShipIdList.add(lsshpm.getShipId());
			jpaLsshpmRepository.save(lsshpm);
			this.updateLsshps(lsshpm, userId);
			
			
		}
		
		this.changeStatusCdOfTbOrderDetail(orderList, TrdstOrderStatus.D02.toString(), userId);
		
		
		  return newShipIdList;
    }
    
    /**
     * ???????????? - ?????? ?????? ???????????? ??????
     * ??????????????? ???????????? shipIndToShip2 ??? ?????? ??????
     */
    //??????????????? ????????? ?????? 2022-02-07
    
//    @Transactional
//    public List<String> shipIndToShip(ShipSaveListData shipSaveListData) {
//        List<String> shipIdList = new ArrayList<>();
//        // 1. ititmc??? ??? qty?????? ????????? ????????? ??????
//        List<Lsshpd> lsshpdList = new ArrayList<>();
//        for(ShipSaveListData.Ship ship : shipSaveListData.getShips()){
//            Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq());
//
//            // ?????? ??????????????? ??????
//            lsshpd.setShipQty(lsshpd.getShipIndicateQty());
//            Lsshpm lsshpm = lsshpd.getLsshpm();
//            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus??? ?????? 04(??????)??? ??????
//                log.debug("????????? ???????????? " + Utilities.addDashInMiddle(lsshpd.getShipId(), lsshpd.getShipSeq()) + "??? ?????? ????????? ???????????????.");
//                continue;
//            }
//            lsshpm.setApplyDay(LocalDateTime.now());
//            lsshpdList.add(lsshpd);
//            // 2. ?????? tbOrderDetail statusCd ??????
//            TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
//
//            /*
//            <<<<<<< HEAD
//=======
//			List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(
//					tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsshpm.getStorageId());
//            // ???????????? ?????? ?????? ??????
//            ititmcList = jpaMoveService.subItitmcQties(null, lsshpm.getStorageId(), ititmcList, ship.getQty()); // ??????????????? ???????????? (????????? ititmc?????? ?????? ??????????????? ititmcList??? ?????? ????????? ??? ?????? ??????????????? ???)
//            if(ititmcList.size()==0){
//                log.debug("??????????????? ????????? ?????????????????? ?????? ?????? ????????? ????????????.");
//                continue;
//            }
//            else {
//>>>>>>> dev
//            
//            */
//            
//
//            
//        	if (lsshpm.getShipOrderGb().equals(StringFactory.getGbTwo())) { // 01 ??????, 02 ??????
//				log.debug(
//						"**********************************************???????????????????????????.????????? ???????????????!***********************************");
//				// ????????? ?????? ?????????????????????!!???????????? ????????????????????? ????????? ?????????????????? ??????????????? ??????.????????? ?????? ??????
//				
////				
////				List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(
////						tbOrderDetail.getAssortId(), tbOrderDetail.getItemId(), lsshpm.getStorageId());
////	            // ???????????? ?????? ?????? ??????
////	            ititmcList = jpaMoveService.subItitmcQties(ititmcList, ship.getQty()); // ??????????????? ???????????? (????????? ititmc?????? ?????? ??????????????? ititmcList??? ?????? ????????? ??? ?????? ??????????????? ???)
////	            if(ititmcList.size()==0){
////	                log.debug("??????????????? ????????? ?????????????????? ?????? ?????? ????????? ????????????.");
////	                continue;
////				} else {
////
////					// ????????? ????????? ????????? ?????? ??????
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
////					// tbOrderDetail.setStatusCd(StringFactory.getStrD02()); // D02 ????????????
////					// jpaTbOrderDetailRepository.save(tbOrderDetail);
////	            }
//	        
//			} else {
//				log.debug("??????????????????.");
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
//				// ????????? ????????? ????????? ?????? ??????
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
//        // 3. lss- ????????? ????????? ???????????? ??????
//		int index = 0;
//        for(Lsshpd lsshpd : lsshpdList){
//            shipIdList.add(jpaMoveService.updateLssSeries(index, lsshpd));
//			index++;
//        }
//        return shipIdList;
//    }
    
    //???????????? ????????? ?????? 2022-02-07

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

	/**
	 * shipId ?????? ??????
	 */
	private String getShipId(){
		return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
	}

	@Transactional
	public String insertEtcShip(InsertShipEtcRequestData p, String userId) throws Exception {

		// depositNo ??????
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


		// 5.????????????
		for (Lsdpsd o : lsdpsdList) {

			HashMap<String, Object> m = new HashMap<String, Object>();

			m.put("storageId", p.getStorageId());

			m.put("effStaDt", o.getExcAppDt());
			m.put("assortId", o.getAssortId());
			m.put("itemId", o.getItemId());
			m.put("itemGrade", o.getItemGrade()); // ?????? ???????????? ??????
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
