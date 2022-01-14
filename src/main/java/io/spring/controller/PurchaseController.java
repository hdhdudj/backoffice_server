package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.request.PurchaseUpdateRequestData;
import io.spring.model.purchase.response.PurchaseItemResponseData;
import io.spring.model.purchase.response.PurchaseMasterListResponseData;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.purchase.MyBatisPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * 발주등록(주문)에서 구매처를 선택했을 때 해당하는 주문을 뿌려주는 api
     * @param vendorId
     * @return
     */
	@GetMapping(path = "/vendors/{vendorId}")
	public ResponseEntity getOrderListByPurchaseVendorItem(@PathVariable("vendorId") String vendorId) {

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("vendorId", vendorId);

		List<HashMap<String, Object>> responseData = myBatisPurchaseService.getOrderListByPurchaseVendorItem(param);
        for(HashMap<String, Object> map : responseData){
            Utilities.changeNullToEmpty(map);
            if(map.get(StringFactory.getStrOrderDate()) != null){
                map.put(StringFactory.getStrOrderDate(), Utilities.removeTAndTransToStr((LocalDateTime)map.get(StringFactory.getStrOrderDate())));
            }
        }
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

    /**
     * 발주등록(주문), 발주등록(상품) 저장 공통
     * @param purchaseInsertRequestData
     * @return
     */
	@PostMapping(path = "") // create 
    public ResponseEntity savePurchaseJpa(@RequestBody PurchaseInsertRequestData purchaseInsertRequestData){
        log.debug("insert purchase by jpa");

		String purchaseNo = jpaPurchaseService.createPurchaseSquence(null, purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo);
        if(res == null){
            return null;
        }

        return ResponseEntity.ok(res);
    }

    /**
     * 발주사후 업데이트 : 마스터 부분, 디테일에선 발주가만
     */
    @PostMapping(path = "/{purchaseNo}/update") // update
    public ResponseEntity savePurchaseJpa(@PathVariable("purchaseNo") String purchaseNo, @RequestBody PurchaseInsertRequestData purchaseInsertRequestData){
        log.debug("update purchase by jpa");

        String purchaseNo2 = jpaPurchaseService.createPurchaseSquence(purchaseNo, purchaseInsertRequestData);

        // jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo2);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    /**
     * lspchm.printDt 저장을 위한 api
     */
    @GetMapping(path = "/update/printdt") // update printDt
    public ResponseEntity savePrintDt(@RequestParam String purchaseNo, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date printDt){
        log.debug("update purchase.printdt by jpa");

        String printDt2 = jpaPurchaseService.savePrintDt(purchaseNo, printDt);

        // jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), printDt2);
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
     * 발주사후(관리) 발주 detail get
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
    @GetMapping(path = "/items")
    public ResponseEntity getChoosePurchaseModalList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                                     @RequestParam @Nullable String siteOrderNo,
                                                     @RequestParam @Nullable String purchaseNo,
                                                     @RequestParam @Nullable String unifiedOrderNo,
                                                     @RequestParam @Nullable String brandId,
                                                     @RequestParam @Nullable String vendorId,
			@RequestParam @Nullable String purchaseGb, @RequestParam @Nullable String orderNm) {


		System.out.println("getChoosePurchaseModalList");
        PurchaseMasterListResponseData purchaseMasterListResponseData = jpaPurchaseService
                .getPurchaseMasterList2(startDt, endDt, siteOrderNo, unifiedOrderNo, brandId, vendorId, purchaseGb, orderNm, purchaseNo);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),purchaseMasterListResponseData);
        return ResponseEntity.ok(res);
    }
//    @GetMapping(path="/items")
//    public ResponseEntity getPurchaseListJpa(@RequestParam @Nullable String vendorId,
//                                             @RequestParam @Nullable String assortId,
//                                             @RequestParam @Nullable String purchaseNo,
//                                             @RequestParam @Nullable String channelOrderNo,
//                                             @RequestParam @Nullable String siteOrderNo,
//                                             @RequestParam @Nullable String orderNm,
//                                             @RequestParam @Nullable String assortNm,
//                                             @RequestParam @Nullable String purchaseStatus,
//                                             @RequestParam @Nullable String brandId,
//                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
//                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
//                                             @RequestParam @Nullable String purchaseGb,
//                                             @RequestParam @Nullable String dealtypeCd
//                                             ){
//        log.debug("get purchase list - jpa");
//
//        PurchaseSelectListResponseData purchaseSelectListResponseData = jpaPurchaseService.getPurchaseList(vendorId, assortId, purchaseNo, channelOrderNo, siteOrderNo, orderNm, assortNm, purchaseStatus, brandId,
//                startDt, endDt, purchaseGb, dealtypeCd);
//
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectListResponseData);
//        if(res == null){
//            return null;
//        }
//        return ResponseEntity.ok(res);
//    }

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

	@PostMapping(path = "/orders/{orderId}/{orderSeq}/cancel") // 취소처리
	public ResponseEntity cancelPurchase(@PathVariable("orderId") String orderId,
			@PathVariable("orderSeq") String orderSeq
	// ,@RequestBody PurchaseCancelRequestData purchaseCancelRequestData
	) {
		log.debug("cancelPurchase");

		HashMap<String, Object> p = new HashMap<String, Object>();

		p.put("orderId", "O00043303");

		p.put("orderSeq", "0001");
		p.put("cancelGb", "00");
		p.put("cancelMsg", "etc");

		jpaPurchaseService.cancelOrderPurchase(p);

		// String purchaseNo2 = jpaPurchaseService.createPurchaseSquence(purchaseNo,
		// purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				"");
		if (res == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

}

