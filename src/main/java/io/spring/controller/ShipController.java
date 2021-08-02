package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.ship.request.ShipIndicateListData;
import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipController {
    private final JpaShipService jpaShipService;

    @GetMapping(path = "/save/get/list")
    public ResponseEntity getOrderSaveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable Date endDt,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String vendorId){
        List<ShipIndicateListData> shipIndicateListDataList = jpaShipService.getOrderSaveList(startDt, endDt, assortId, assortNm, vendorId);
        ApiResponseMessage res = new ApiResponseMessage("ok", "success", shipIndicateListDataList);
        return ResponseEntity.ok(res);
    }
}
