package io.spring.controller;

import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipController {
    private final JpaShipService jpaShipService;

//    @GetMapping(path = "/")
//    public ResponseEntity getOrderListByPurchaseVendor() {
//        HashMap<String, Object> param = new HashMap<String, Object>();
//        List<HashMap<String, Object>> responseData = myBatisPurchaseService.getOrderListByPurchaseVendor(param);
//        ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
//        if (responseData == null) {
//            return null;
//        }
//        return ResponseEntity.ok(res);
//    }
}
