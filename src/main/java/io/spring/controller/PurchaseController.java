package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.service.common.JpaCommonService;
import io.spring.service.purchase.JpaPurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private JpaPurchaseService jpaPurchaseService;
    private JpaCommonService jpaCommonService;
    public PurchaseController(JpaPurchaseService jpaPurchaseService, JpaCommonService jpaCommonService){
        this.jpaCommonService = jpaCommonService;
        this.jpaPurchaseService = jpaPurchaseService;
    }

    @PostMapping(path="/jpasave")
    public ResponseEntity savePurchaseJpa(@RequestBody PurchaseInsertRequest purchaseInsertRequest){
        logger.debug("insert or update purchase by jpa");
        purchaseInsertRequest.setPurchaseNo(jpaCommonService.getPurchaseNo(purchaseInsertRequest, StringFactory.getPurchaseSeqStr())); // purchaseNo 채번
        String purchaseNo = jpaPurchaseService.savePurchaseSquence(purchaseInsertRequest);

        ApiResponseMessage res = new ApiResponseMessage("ok", "success", purchaseNo);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping(path="/init")
    public void initTabled(){
        jpaPurchaseService.initTables();
    }
}

