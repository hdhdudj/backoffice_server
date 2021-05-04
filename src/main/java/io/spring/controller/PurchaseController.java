package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.model.purchase.response.PurchaseSelectDetailResponse;
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

    @GetMapping(path = "/getpurchasedetail")
    public ResponseEntity getPurchaseDetailJpa(@RequestParam(required = true) String purchaseNo){
        logger.debug("get purchase detail page");

        PurchaseSelectDetailResponse responseData = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
        if(responseData == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping(path="/jpasave")
    public ResponseEntity savePurchaseJpa(@RequestBody PurchaseInsertRequest purchaseInsertRequest){
        logger.debug("insert or update purchase by jpa");
        purchaseInsertRequest.setPurchaseNo(jpaCommonService.getStrNumberId(StringFactory.getCUpperStr(), purchaseInsertRequest.getPurchaseNo(), StringFactory.getPurchaseSeqStr(), StringFactory.getIntEight())); // purchaseNo 채번
        String purchaseNo = jpaPurchaseService.savePurchaseSquence(purchaseInsertRequest);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }
    // 발주 detail page
    @GetMapping(path="/purchasedetailjpa")
    public ResponseEntity getPurchaseDetailPage(String purchaseNo) {
        logger.debug("get purchase detail page");

        PurchaseSelectDetailResponse purchaseSelectDetailResponse = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectDetailResponse);

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

