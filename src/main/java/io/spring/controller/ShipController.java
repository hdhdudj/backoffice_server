package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping(path = "/orders/list")
    public ResponseEntity getOrderSaveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date endDt,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String purchaseVendorId){
        ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = jpaShipService.getOrderSaveList(startDt, endDt, assortId, assortNm, purchaseVendorId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateSaveListResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시 화면 : 출고지시 할 출고내역들을 선택 후 저장 버튼을 누르면 호출되는 api (출고번호 기준으로 불러옴)
     */
    @PostMapping(path = "/save/save")
    public ResponseEntity saveShipIndicate(@RequestBody ShipIndicateSaveListData shipIndicateSaveDataList){
        List<String> shipIdList = jpaShipService.saveShipIndicate(shipIndicateSaveDataList);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 출고지시리스트 화면 : 출고지시리스트 화면에서 조건 검색으로 출고지시 리스트를 불러오는 api
     */
    @GetMapping(path = "/items")
    public ResponseEntity getShipList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date endDt,
                                           @RequestParam @Nullable String shipId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String vendorId){
        ShipIndicateListData shipIndicateListData = jpaShipService.getShipList(startDt, endDt, shipId, assortId, assortNm, vendorId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),shipIndicateListData);
        return ResponseEntity.ok(res);
    }
}
