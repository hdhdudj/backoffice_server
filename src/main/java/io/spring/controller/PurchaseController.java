package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.service.purchase.JpaPurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private JpaPurchaseService jpaPurchaseService;
    public PurchaseController(JpaPurchaseService jpaPurchaseService){
        this.jpaPurchaseService = jpaPurchaseService;
    }

    @PostMapping(path="/jpasave")
    public ResponseEntity savePurchaseJpa(PurchaseInsertRequest purchaseInsertRequest){
        logger.debug("insert or update purchase by jpa");

        String assortId = jpaPurchaseService.savePurchaseSquence(purchaseInsertRequest);

        ApiResponseMessage res = new ApiResponseMessage("ok", "success", assortId);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }
}

