package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.request.ShipSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.model.ship.response.ShipItemListData;
import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipController {
    private final JpaShipService jpaShipService;

    /**
     * 출고지시 화면 : 출고지시 저장 화면에서 저장하기 위한 리스트를 조건 검색으로 불러오는 api (주문번호 기준으로 불러옴)
     */
    @GetMapping(path = "/deposit/items")
    public ResponseEntity getOrderSaveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date endDt,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String vendorId){
        ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = jpaShipService.getOrderSaveList(startDt, endDt, assortId, assortNm, vendorId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateSaveListResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시 화면 : 저장용. 출고지시 할 출고내역들을 선택 후 저장 버튼을 누르면 호출되는 api (출고번호 기준으로 불러옴)
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
    public ResponseEntity getShipList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                      @RequestParam @Nullable String shipId,
                                       @RequestParam @Nullable String assortId,
                                       @RequestParam @Nullable String assortNm,
                                       @RequestParam @Nullable String vendorId){
//        Date start = java.sql.Timestamp.valueOf(startDt.atStartOfDay());
//        Date end = java.sql.Timestamp.valueOf(endDt.atTime(23,59,59));
        ShipIndicateListData shipIndicateListData = jpaShipService.getShipList(startDt, endDt, shipId, assortId, assortNm, vendorId, StringFactory.getStrC04());
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시내역 화면 : 출고지시번호를 받아 해당 출고지시번호의 내역 마스터(Lsshpm)와 목록(Lsshpd)을 보여줌
     */
    @GetMapping(path = "/indicate/{shipId}")
    public ResponseEntity getShipDetailList(@PathVariable String shipId){
        ShipItemListData shipItemListData = jpaShipService.getShipDetailList(shipId);
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
                                             @RequestParam @Nullable String vendorId){
        ShipIndicateListData shipIndicateListData = jpaShipService.getShipList(startDt, endDt, shipId, assortId, assortNm, vendorId, StringFactory.getStrD01());
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고처리 화면 : 출고 수량을 입력하면 관련된 값을 변경함.
     */
    @PostMapping(path = "")
    public ResponseEntity shipIndToShip(@RequestBody ShipSaveListData shipSaveListData){
        List<String> shipIdList = jpaShipService.shipIndToShip(shipSaveListData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIdList);
        return ResponseEntity.ok(res);
    }
}
