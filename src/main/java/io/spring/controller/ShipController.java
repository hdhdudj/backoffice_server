package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.ship.request.InsertShipEtcRequestData;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.request.ShipSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.model.ship.response.ShipItemListData;
import io.spring.model.ship.response.ShipListDataResponse;
import io.spring.service.ship.JpaShipService;
import io.spring.service.ship.MyBatisShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipController {
    private final JpaShipService jpaShipService;
	private final MyBatisShipService myBatisShipService;

    /**
     * 출고지시 화면 : 출고지시 저장 화면에서 저장하기 위한 리스트를 조건 검색으로 불러오는 api (주문번호 기준으로 불러옴)
     */
    @GetMapping(path = "/deposit/items")
    public ResponseEntity getOrderSaveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate endDt,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
			@RequestParam @Nullable String storageId,
			@RequestParam @Nullable String channelId, @RequestParam @Nullable String orderId) {

		HashMap<String, Object> map = new HashMap<>();

		if (startDt != null) {

			LocalDateTime start = startDt.atStartOfDay();

			map.put("startDt", start);
		}
		if (endDt != null) {

			LocalDateTime end = endDt.atTime(23, 59, 59);
			map.put("endDt", end);
		}

		if (assortId != null && !assortId.equals("")) {
			map.put("assortId", assortId);
		}
		if (assortNm != null && !assortNm.equals("")) {
			map.put("assortNm", assortNm);
		}
		if (storageId != null && !storageId.equals("")) {
			map.put("storageId", storageId);
		}
		if (channelId != null && !channelId.equals("")) {
			map.put("channelId", channelId);
		}

        String orderSeq = "";

		if (orderId != null && !orderId.equals("")) {
            String[] orderArr = orderId.split("-");
            if(orderArr.length > 1){
                orderId = orderArr[0];
                orderSeq = orderArr[1];
            }
			map.put("orderId", orderId);
			map.put("orderSeq", orderSeq);
		}

		List<ShipIndicateSaveListResponseData.Ship> l = myBatisShipService.getOrderShipList(map);

		ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = new ShipIndicateSaveListResponseData(
				startDt, endDt, assortId, assortNm, channelId, orderId);
		shipIndicateSaveListResponseData.setShips(l);


        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateSaveListResponseData);
        return ResponseEntity.ok(res);
    }

//    /**
//     * 출고지시 화면 : 출고지시 저장 화면에서 저장하기 위한 리스트를 조건 검색으로 불러오는 api (주문번호 기준으로 불러옴)
//     */
//    @GetMapping(path = "/deposit/items")
//    public ResponseEntity getShipCandidateList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
//                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
////                                      @RequestParam @Nullable String shipId,
//                                              @RequestParam @Nullable String assortId,
//                                              @RequestParam @Nullable String assortNm,
//                                              @RequestParam @Nullable String storageId,
//                                              @RequestParam @Nullable String vendorId){
////        Date start = java.sql.Timestamp.valueOf(startDt.atStartOfDay());
////        Date end = java.sql.Timestamp.valueOf(endDt.atTime(23,59,59));
//        ShipCandidateListData shipCandidateListData = jpaShipService.getShipCandidateList(startDt, endDt, storageId, assortId,
//                assortNm, vendorId, StringFactory.getStrC04(), "", StringFactory.getGbTwo());
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipCandidateListData);
//        return ResponseEntity.ok(res);
//    }


   /**
    * 출고지시 화면 : 출고지시 저장용. 출고지시 할 출고내역들을 선택 후 저장 버튼을 누르면 호출되는 api (출고번호 기준으로 불러옴)
    */
    @PostMapping(path = "/indicate")
    public ResponseEntity saveShipIndicate(@RequestBody ShipIndicateSaveListData shipIndicateSaveDataList){
        List<String> shipIdList = jpaShipService.saveShipIndicate(shipIndicateSaveDataList);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시리스트 화면 : 출고지시리스트 화면에서 조건 검색으로 출고지시 리스트를 불러오는 api
     */
    @GetMapping(path = "/indicate/items")
    public ResponseEntity getShipIndicateList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                              @RequestParam @Nullable String shipId,
                                              @RequestParam @Nullable String assortId,
                                              @RequestParam @Nullable String assortNm,
                                              @RequestParam @Nullable String vendorId){
//        Date start = java.sql.Timestamp.valueOf(startDt.atStartOfDay());
//        Date end = java.sql.Timestamp.valueOf(endDt.atTime(23,59,59));
        ShipIndicateListData shipIndicateListData = jpaShipService.getShipIndList(startDt, endDt, shipId, assortId,
                assortNm, vendorId, StringFactory.getStrD01(), "", StringFactory.getGbTwo());
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시내역 화면 : 출고지시번호를 받아 해당 출고지시번호의 내역 마스터(Lsshpm)와 목록(Lsshpd)을 보여줌
     */
    @GetMapping(path = "/indicate/{shipId}")
    public ResponseEntity getShipIndicateDetailList(@PathVariable String shipId){
        ShipItemListData shipItemListData = jpaShipService.getShipIndicateDetailList(shipId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipItemListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고처리 화면 : 출고지시일자, 출고지시번호, 상품코드, 구매처를 받아서 조회하면 출고지시 목록을 보여줌
     */
    @GetMapping(path = "/items")
    public ResponseEntity getShipIndSaveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                             @RequestParam @Nullable String shipId,
                                             @RequestParam @Nullable String assortId,
                                             @RequestParam @Nullable String assortNm,
			@RequestParam @Nullable String vendorId, @RequestParam @Nullable String orderId) {
		ShipIndicateListData shipIndicateListData = jpaShipService.getShipIndList(startDt, endDt, shipId, assortId,
				assortNm, vendorId, StringFactory.getStrD01(), orderId, StringFactory.getGbTwo());
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고처리 화면 : 출고 수량을 입력하면 관련된 값을 변경함.
     */
    @PostMapping(path = "")
    public ResponseEntity shipIndToShip(@RequestBody ShipSaveListData shipSaveListData){
		List<String> shipIdList = jpaShipService.shipIndToShip2(shipSaveListData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
	 * 출고리스트 화면 : 출고지시일자, 출고지시번호, 상품코드, 구매처를 받아서 조회하면 출고 목록을 보여줌
     * todo(완) : 2021-10-27 출고리스트 서비스 안만들어짐 지금쓰는건 출고지시용임,새로 출고리스트 조회하는거 하나 새로 만들어야함,.
	 */
    @GetMapping(path = "/ship/items")
    public ResponseEntity getShipList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                             @RequestParam @Nullable String shipId,
                                             @RequestParam @Nullable String assortId,
                                             @RequestParam @Nullable String assortNm,
                                             @RequestParam @Nullable String storageId,
                                             @RequestParam @Nullable String vendorId){
		ShipListDataResponse shipListDataResponse = jpaShipService.getShipList(startDt, endDt, shipId, assortId,
				assortNm, vendorId, TrdstOrderStatus.D02.toString(), StringFactory.getGbFour(), storageId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipListDataResponse);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고내역 화면 : shipId로 검색하면 출고 디테일 목록을 보여줌
     */
    @GetMapping(path = "/ship/{shipId}")
    public ResponseEntity getShipDetailList(@PathVariable String shipId){
        ShipItemListData shipItemListData = jpaShipService.getShipIndicateDetailList(shipId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipItemListData);
        return ResponseEntity.ok(res);
    }

	/**
	 * 입고처리 : 화면에서 입고수량 입력 후 저장을 눌렀을 때 타는 api (create)
	 * 
	 * @throws Exception
	 */
	@PostMapping(path = "/etc")
	public ResponseEntity insertEtcShip(@RequestBody InsertShipEtcRequestData reqData) throws Exception {
		log.debug("출고처리 호출");

		System.out.println(reqData);

		String depositNo = "";

		List<String> messageList = new ArrayList<>();
		depositNo = jpaShipService.insertEtcShip(reqData);
		// depositNo = jpaDepositService.insertEtcDeposit(reqData);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				depositNo);

		return ResponseEntity.ok(res);
	}

}
