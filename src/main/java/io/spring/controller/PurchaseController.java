package io.spring.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import io.spring.model.purchase.response.PurchaseSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.purchase.MyBatisPurchaseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final JpaPurchaseService jpaPurchaseService;
    private final JpaCommonService jpaCommonService;
    
    private final JpaOrderService jpaOrderService;
    
    private final MyBatisPurchaseService myBatisPurchaseService;
    
    
    

//    public PurchaseController(JpaPurchaseService jpaPurchaseService, JpaCommonService jpaCommonService, MyBatisPurchaseService myBatisPurchaseService){
//        this.jpaCommonService = jpaCommonService;
//        this.jpaPurchaseService = jpaPurchaseService;
//        this.myBatisPurchaseService = myBatisPurchaseService;
//    }

	@GetMapping(path = "/vendors")
	public ResponseEntity getOrderListByPurchaseVendor() {

		HashMap<String, Object> param = new HashMap<String, Object>();

		List<HashMap<String, Object>> responseData = myBatisPurchaseService.getOrderListByPurchaseVendor(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/vendors/{vendorId}")
	public ResponseEntity getOrderListByPurchaseVendorItem(@PathVariable("vendorId") String vendorId) {

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("vendorId", vendorId);

		List<HashMap<String, Object>> responseData = myBatisPurchaseService.getOrderListByPurchaseVendorItem(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);

	}

    @GetMapping(path = "/getpurchasedetail")
    public ResponseEntity getPurchaseDetailJpa(@RequestParam(required = true) String purchaseNo){
        logger.debug("get purchase detail page");

        PurchaseSelectDetailResponseData responseData = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
        if(responseData == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

	@PostMapping(path = "")
    public ResponseEntity savePurchaseJpa(@RequestBody PurchaseInsertRequestData purchaseInsertRequestData){
        logger.debug("insert or update purchase by jpa");



		purchaseInsertRequestData.setPurchaseNo(
				jpaCommonService.getStrNumberId(StringFactory.getCUpperStr(), purchaseInsertRequestData.getPurchaseNo(),
						StringFactory.getPurchaseSeqStr(), StringFactory.getIntEight())); // purchaseNo 채번
		String purchaseNo = jpaPurchaseService.savePurchaseSquence(purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

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

        PurchaseSelectDetailResponseData purchaseSelectDetailResponseData = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectDetailResponseData);

        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    // 발주 list get (jpa)
    @GetMapping(path="/purchaselistjpa")
    public ResponseEntity getPurchaseListJpa(@RequestParam String purchaseVendorId,@RequestParam String assortId,@RequestParam String purchaseStatus,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDt,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDt){
        logger.debug("get purchase list - jpa");

        HashMap<String, Object> param = new HashMap<>();

        param.put("purchaseVendorId", purchaseVendorId);
        param.put("assortId", assortId);
        param.put("purchaseStatus", purchaseStatus);
        param.put("startDt", startDt);
        param.put("endDt", endDt);

        PurchaseSelectListResponseData purchaseSelectListResponseData = jpaPurchaseService.getPurchaseList(param);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectListResponseData.getPurchaseList());
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    // 발주 list get (mybatis)
    @GetMapping(path="/purchaselistmybatis")
    public ResponseEntity getPurchaseListMyBatis(@RequestParam String purchaseVendorId,@RequestParam String assortId, String purchaseStatus, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDt, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDt){
        logger.debug("get purchase list - mybatis");

        HashMap<String, Object> param = new HashMap<>();

        param.put("purchaseVendorId", purchaseVendorId);
        param.put("assortId", assortId);
        param.put("purchaseStatus", purchaseStatus);
        param.put("startDt", startDt);
        param.put("endDt", endDt);

        List<HashMap<String, Object>> result = myBatisPurchaseService.getPurchaseList(param);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), result);
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

