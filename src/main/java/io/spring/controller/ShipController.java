package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipController {
    private final JpaShipService jpaShipService;

    @GetMapping(path = "/ship")
    public ResponseEntity getOrderListByPurchaseVendor() {
        HashMap<String, Object> param = new HashMap<String, Object>();
        ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
        if (responseData == null) {
            return null;
        }
        return ResponseEntity.ok(res);
    }
}
