package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.request.PurchaseUpdateRequestData;
import io.spring.model.purchase.response.PurchaseItemResponseData;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import io.spring.model.purchase.response.PurchaseSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.purchase.MyBatisPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/purchase")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {
    private final JpaPurchaseService jpaPurchaseService;
    private final JpaCommonService jpaCommonService;
    
    private final JpaOrderService jpaOrderService;
    
    private final MyBatisPurchaseService myBatisPurchaseService;

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


    @GetMapping(path = "/{purchaseNo}")
	public ResponseEntity getPurchase(@PathVariable("purchaseNo") String purchaseNo) {
		log.debug("get purchase detail page");

		HashMap<String, Object> m = new HashMap<String, Object>();

		m.put("purchaseNo", purchaseNo);

		PurchaseItemResponseData responseData = myBatisPurchaseService.getPurchase(m);
		// List<HashMap<String, Object>> items =
		// myBatisPurchaseService.getPurchaseItems(m);

		// PurchaseSelectDetailResponseData responseData =
		// jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

    @GetMapping(path = "/getpurchasedetail")
    public ResponseEntity getPurchaseDetailJpa(@RequestParam(required = true) String purchaseNo){
        log.debug("get purchase detail page");

        PurchaseSelectDetailResponseData responseData = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

        ApiResponseMessage res = null;
        if(responseData == null){
            res = new ApiResponseMessage("error","not found error",responseData);
        }
        else {
            res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
        }
        return ResponseEntity.ok(res);
    }

	@PostMapping(path = "") // create
    public ResponseEntity savePurchaseJpa(@RequestBody PurchaseInsertRequestData purchaseInsertRequestData){
        log.debug("insert purchase by jpa");

		String purchaseNo = jpaPurchaseService.createPurchaseSquence(purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/{purchaseNo}/update") // update
    public ResponseEntity savePurchaseJpa(@PathVariable String purchaseNo, @RequestBody PurchaseInsertRequestData purchaseInsertRequestData){
        log.debug("update purchase by jpa");

        jpaPurchaseService.updatePurchaseSquence(purchaseNo, purchaseInsertRequestData);

        // jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

	@PostMapping(path = "/{purchaseNo}")
	public ResponseEntity updatePurchaseJpa(@PathVariable("purchaseNo") String purchaseNo,
			@RequestBody PurchaseUpdateRequestData req) {
        log.debug("insert or update purchase by jpa");



//		req.setPurchaseNo(jpaCommonService.getStrNumberId(StringFactory.getCUpperStr(), req.getPurchaseNo(),
		// StringFactory.getPurchaseSeqStr(), StringFactory.getIntEight())); //
		// purchaseNo 채번

//		String purchaseNo1 = jpaPurchaseService.savePurchaseSquence(req);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				purchaseNo);
		if (res == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

    /**
     * 발주 detail get
     */
    @GetMapping(path="/item/{purchaseNo}")
    public ResponseEntity getPurchaseDetailPage(@PathVariable String purchaseNo) {
        log.debug("get purchase detail page");

        PurchaseSelectDetailResponseData purchaseSelectDetailResponseData = jpaPurchaseService.getPurchaseDetailPage(purchaseNo);

//        PurchaseItemResponseData purchaseItemResponseData = myBatisPurchaseService.getPurchase()
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectDetailResponseData);

        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    /**
     * 발주 list get (발주리스트 화면)
     */
    @GetMapping(path="/items")
    public ResponseEntity getPurchaseListJpa(@RequestParam @Nullable String purchaseVendorId,
                                             @RequestParam @Nullable String assortId,
                                             @RequestParam @Nullable String assortNm,
                                             @RequestParam @Nullable String purchaseStatus,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                             @RequestParam @Nullable String purchaseGb,
                                             @RequestParam @Nullable String dealtypeCd
                                             ){
        log.debug("get purchase list - jpa");

        HashMap<String, Object> param = new HashMap<>();

        param.put("purchaseVendorId", purchaseVendorId);
        param.put("assortId", assortId);
        param.put("assortNm", assortNm);
        param.put("purchaseStatus", purchaseStatus);
        param.put("startDt", startDt);
        param.put("endDt", endDt);
        param.put("purchaseGb", purchaseGb);
        param.put("dealtypeCd", dealtypeCd);
        param.put("purchaseNo", null);

        PurchaseSelectListResponseData purchaseSelectListResponseData = jpaPurchaseService.getPurchaseList(param);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectListResponseData);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    // 발주 list get (mybatis) : 발주리스트 화면에서 검색 누르면 작동하는 api
    @GetMapping(path="/purchaselistmybatis")
	public ResponseEntity getPurchaseListMyBatis(@RequestParam(required = false) String purchaseVendorId,
			@RequestParam(required = false) String dealtypeCd, @RequestParam(required = false) String assortId,
			@RequestParam(required = false) String purchaseStatus,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt
			) {
        log.debug("get purchase list - mybatis");

        HashMap<String, Object> param = new HashMap<>();

        param.put("purchaseVendorId", purchaseVendorId);
		param.put("dealtypeCd", dealtypeCd);
        param.put("assortId", assortId);
        param.put("purchaseStatus", purchaseStatus);
        param.put("startDt", startDt.atStartOfDay());
        param.put("endDt", endDt.atTime(23,59,59));


        List<HashMap<String, Object>> result = myBatisPurchaseService.getPurchaseList(param);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), result);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }
}

