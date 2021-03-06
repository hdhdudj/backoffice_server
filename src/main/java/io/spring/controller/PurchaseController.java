package io.spring.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import io.spring.model.purchase.response.PurchaseDetailCancelResponse;
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
import io.spring.model.common.request.CommonRequestData;
import io.spring.model.purchase.request.PrintDtRequestData;
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
	public ResponseEntity getOrderListByPurchaseVendor(
			@RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt) {

		HashMap<String, Object> param = new HashMap<String, Object>();



//		LocalDate lst = LocalDate.now();
//		LocalDate lst1 = lst.minusDays(365);
//		LocalDate lst2 = lst.minusDays(0);
//
//		if (startDt == null) {
//
//			param.put("startDt", lst1.toString() + " 00:00:00");
//
//		} else {
//			param.put("startDt", startDt.toString() + " 00:00:00");
//		}
//
//		if (endDt == null) {
//
//			param.put("endDt", lst2.toString() + " 23:59:59");
//		} else {
//			param.put("endDt", endDt.toString() + " 23:59:59");
//
//		}


		List<HashMap<String, Object>> responseData = myBatisPurchaseService.getOrderListByPurchaseVendor(param);

		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);

	}

    /**
     * ????????????(??????)?????? ???????????? ???????????? ??? ???????????? ????????? ???????????? api
     * @param vendorId
     * @return
     */
	@GetMapping(path = "/vendors/{vendorId}")
	public ResponseEntity getOrderListByPurchaseVendorItem(@PathVariable("vendorId") String vendorId,
			@RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @Nullable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt) {

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("vendorId", vendorId);


//		LocalDate lst = LocalDate.now();
//		LocalDate lst1 = lst.minusDays(365);
//		LocalDate lst2 = lst.minusDays(0);
//
//		if (startDt == null) {
//			param.put("startDt", lst1.toString() + " 00:00:00");
//		} else {
//			param.put("startDt", startDt.toString() + " 00:00:00");
//		}
//
//		if (endDt == null) {
//			param.put("endDt", lst2.toString() + " 23:59:59");
//		} else {
//			param.put("endDt", endDt.toString() + " 23:59:59");
//
//		}

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
     * ????????????(??????), ????????????(??????) ?????? ??????
     * @param purchaseInsertRequestData
     * @return
     */
	@PostMapping(path = "") // create 
	public ResponseEntity savePurchaseJpa(@RequestBody @Valid PurchaseInsertRequestData purchaseInsertRequestData) {
        log.debug("insert purchase by jpa");

		String purchaseNo = jpaPurchaseService.createPurchaseSquence(purchaseInsertRequestData.getPurchaseNo(), purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo);
        if(res == null){
            return null;
        }

        return ResponseEntity.ok(res);
    }

    /**
     * ???????????? ???????????? : ????????? ??????, ??????????????? ???????????? ?????????????????????
     */
    @PostMapping(path = "/{purchaseNo}/update") // update
	public ResponseEntity savePurchaseJpa(@PathVariable("purchaseNo") String purchaseNo,
			@RequestBody @Valid PurchaseInsertRequestData purchaseInsertRequestData) {
        log.debug("update purchase by jpa");

		String userId = purchaseInsertRequestData.getUserId();
//        String purchaseNo2 = jpaPurchaseService.createPurchaseSquence(purchaseNo, purchaseInsertRequestData);
		String purchaseNo2 = jpaPurchaseService.updatePurchaseSquence(purchaseNo, purchaseInsertRequestData, userId);

        // jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), purchaseNo2);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

    /**
     * lspchm.printDt ????????? ?????? api
     */
    
    //20220307 rjb80 requestbody ??????
    @GetMapping(path = "/update/printdt") // update printDt
	public ResponseEntity savePrintDt(@RequestBody PrintDtRequestData req) throws Exception {
        log.debug("update purchase.printdt by jpa");

		String purchaseNo = req.getPurchaseNo();
		String printDt = req.getPrintDt();
		String userId = req.getUserId();
		
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


		Date toPrintDt = transFormat.parse(printDt);


		String printDt2 = jpaPurchaseService.savePrintDt(purchaseNo, toPrintDt, userId);

        // jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), printDt2);
        if(res == null){
            return null;
        }
        return ResponseEntity.ok(res);
    }

	@PostMapping(path = "/{purchaseNo}")
	public ResponseEntity updatePurchaseJpa(@PathVariable("purchaseNo") String purchaseNo,
			@RequestBody @Valid PurchaseUpdateRequestData req) {
        log.debug("insert or update purchase by jpa");

//		req.setPurchaseNo(jpaCommonService.getStrNumberId(StringFactory.getCUpperStr(), req.getPurchaseNo(),
		// StringFactory.getPurchaseSeqStr(), StringFactory.getIntEight())); //
		// purchaseNo ??????

//		String purchaseNo1 = jpaPurchaseService.savePurchaseSquence(req);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				purchaseNo);
		if (res == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

    /**
     * ????????????(??????) ?????? detail get
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
     * ?????? list get (??????????????? ??????)
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

    // ?????? list get (mybatis) : ??????????????? ???????????? ?????? ????????? ???????????? api
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

	// 20220307 rjb80 cancelPurchase requestbody ??????
	// ??????????????? api???
	// ???????????????????????? api
	@PostMapping(path = "/orders/{orderId}/{orderSeq}/cancel") // ????????????
	public ResponseEntity cancelPurchase(@PathVariable("orderId") String orderId,
			@PathVariable("orderSeq") String orderSeq
			, @RequestBody CommonRequestData req
	) {

		log.debug("cancelPurchase");

		// 20220307 rjb80 ???????????? ???????????? ???????????? ??????
//		String userId = req.getUserId();
//
//		HashMap<String, Object> p = new HashMap<String, Object>();
//
//		p.put("orderId", "O00043303");
//
//		p.put("orderSeq", "0001");
//		p.put("cancelGb", "00");
//		p.put("cancelMsg", "etc");
//
//		jpaPurchaseService.cancelOrderPurchase(p, userId);

		// 20220307 rjb80 ???????????? ???????????? ???????????? ???

		// String purchaseNo2 = jpaPurchaseService.createPurchaseSquence(purchaseNo,
		// purchaseInsertRequestData);

		// jpaOrderService.updateStatusCd("O2106100714498480", "0001", "B02");

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				"?????????api ????????????!!!!!");
		if (res == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

    /**
     * ?????? ????????? ?????????(vendorId) ????????????
     */
    @PostMapping(path="/{purchaseNo}/{purchaseSeq}/vendor/{vendorId}")
    public ResponseEntity updateVendorId(@PathVariable String purchaseNo, @PathVariable String purchaseSeq, @PathVariable String vendorId,
                                         @RequestParam String userId) {
        String purchaseKey = jpaPurchaseService.updateVendorId(purchaseNo, purchaseSeq, vendorId, userId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
                purchaseKey);
        return ResponseEntity.ok(res);
    }

    /**
     * ?????? ????????? ??????
     */
    @PostMapping(path = "/cancel")
    public ResponseEntity cancelPurchaseDetail(@RequestBody PurchaseDetailCancelResponse purchaseDetailCancelResponse){
        jpaPurchaseService.cancelOrderPurchase(purchaseDetailCancelResponse);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
                true);
        return ResponseEntity.ok(res);
    }
}

