package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.model.purchase.response.PurchaseSelectDetailResponse;
import io.spring.model.purchase.response.PurchaseSelectListResponse;
import io.spring.service.common.JpaCommonService;
import io.spring.service.purchase.JpaPurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    public ResponseEntity getPurchaseDetailPage(@RequestParam String purchaseNo) {
        logger.debug("get purchase detail page");

        PurchaseSelectDetailResponse purchaseSelectDetailResponse = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectDetailResponse);

        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    // 발주 list get
    @GetMapping(path="/purchaselistjpa")
    public ResponseEntity getPurchaseList(@RequestParam String purchaseVendorId, @RequestParam String assortId, @RequestParam String purchaseStatus, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDt, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDt){
        logger.debug("get purchase list");

        PurchaseSelectListResponse purchaseSelectListResponse = jpaPurchaseService.getPurchaseList(purchaseVendorId, assortId, purchaseStatus, startDt, endDt);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectListResponse);
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

