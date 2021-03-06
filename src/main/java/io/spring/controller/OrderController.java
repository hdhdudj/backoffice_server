package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.order.MyBatisOrderDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.order.entity.OrderStock;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.request.CancelOrderRequestData;
import io.spring.model.order.request.OrderOptionRequestData;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.model.order.response.CancelOrderListResponse;
import io.spring.model.order.response.OrderDetailResponseData;
import io.spring.model.order.response.OrderMasterListResponseData;
import io.spring.model.order.response.OrderStatusWatingItemListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.order.MyBatisOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {
	private final MyBatisOrderDao myBatisOrderDao;
	private final MyBatisCommonDao myBatisCommonDao;

	private final MyBatisOrderService myBatisOrderService;

	private final JpaCommonService jpaCommonService;
	private final JpaOrderService jpaOrderService;


//	@Autowired
//	public OrderController(MyBatisOrderDao myBatisOrderDao, MyBatisCommonDao myBatisCommonDao,
//			JpaCommonService jpaCommonService) {
//		this.myBatisOrderDao = myBatisOrderDao;
//		this.myBatisCommonDao = myBatisCommonDao;
//		this.jpaCommonService = jpaCommonService;
//	}


	@PostMapping(path = "/order-stock")
	public ResponseEntity saveOrderStockMng(@RequestBody List<OrderStockMngInsertRequestData> req) {


		for (OrderStockMngInsertRequestData o : req) {

				jpaOrderService.saveOrderStock(o);

		}

		ApiResponseMessage res = null;

		// log.debug(r.size());

		res = new ApiResponseMessage<String>("SUCCESS", "", "");

		return ResponseEntity.ok(res);

	}

	@RequestMapping(path = "/order-stock", method = RequestMethod.GET)
	public ResponseEntity selectOrderStock() {

		ApiResponseMessage res = null;

		List<OrderStock> r = jpaOrderService.getOrderStock();

		// log.debug(r.size());

		if (r.size() > 0) {
			res = new ApiResponseMessage<List<OrderStock>>("SUCCESS", "", r);
		} else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);

	}

	@RequestMapping(path = "/orders", method = RequestMethod.GET)
	// public ResponseEntity selectOrderListByCondition(@Valid @RequestBody
	// Map<String, Object> param) {
	public ResponseEntity selectOrderListByCondition(@RequestParam String channelGb, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderFromDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderEndDt) {
		LocalDateTime from = orderFromDt.atStartOfDay();
		LocalDateTime end = orderEndDt.atTime(23,59,59);

		/*
		 * 
		 * log.debug("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		 * 
		 * HashMap<String, Object> arr = new HashMap<String, Object>();
		 * 
		 * arr.put("seqName", "seq_ITASRT");
		 * 
		 * HashMap<String, Object> x1 = commonRepository.getSequence(arr);
		 * 
		 * System.out.println("x1 = " + x1.get("nextval"));
		 * 
		 * HashMap<String, Object> h = new HashMap<String, Object>();
		 * 
		 * for (Map.Entry<String, Object> entry : param.entrySet()) {
		 * System.out.println("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue());
		 * 
		 * h.put(entry.getKey(), entry.getValue());
		 * 
		 * }
		 * 
		 */

		// HashMap<String, Object> h = new HashMap<String, Object>();

//		for (Map.Entry<String, Object> entry : param.entrySet()) {
		// System.out.println("Key = " + entry.getKey() + ", Value = " +
		// entry.getValue());

		// h.put(entry.getKey(), entry.getValue());

		// }

		log.debug(channelGb);
		log.debug(orderFromDt.toString());
		log.debug(orderEndDt.toString());

		long rx = jpaCommonService.getSequence("seq_TMPSEQ");

		System.out.println("rx = " + rx);

		HashMap<String, Object> h = new HashMap<String, Object>();

		h.put("channelGb", channelGb);
		h.put("orderFromDt", from);
		h.put("orderEndDt", end);

		List<HashMap<String, Object>> r = myBatisOrderDao.selectOrderListByCondition(h);
		
		ApiResponseMessage res = null;

		// log.debug(r.size());


		if (r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>(StringFactory.getStrOk(),StringFactory.getStrSuccess(), r);
		} else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);

		// UserData userData = userQueryService.findById(user.getId()).get();
		// return ResponseEntity.status(201).body(userResponse(new
		// UserWithToken(userData, jwtService.toToken(user))));
	}

	// orderId, orderSeq??? ????????? ?????? ????????? ???????????? ??????
	@RequestMapping(path = "/orderstatus", method = RequestMethod.GET)
	public ResponseEntity changeOrderStatus(@RequestParam String orderId, @RequestParam String orderSeq) {
		log.debug("changeOrderStatus ??????.");

		String userId = "batch did";

		jpaOrderService.changeOrderStatus(orderId, orderSeq, userId);


		TbOrderDetail t = jpaOrderService.getOrderDetail(orderId, orderSeq);

		System.out.println("--------------------------------------------------------------------------------------------");

		System.out.println(t);


		ApiResponseMessage res = null;
		return ResponseEntity.ok(res);
	}


	/**
	 * ???????????????????????? ???????????? api
	 */
	@GetMapping(path = "/items")
	// ????????????,????????????,????????????,( ????????????????????? ??????????????????(?????????),????????????,????????????????????????,?????????????????????,????????????,????????????????????????,????????? ????????????,????????????
	public ResponseEntity getOrderList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
			  @RequestParam @Nullable String statusCd,
			  @RequestParam @Nullable String orderId,
			  @RequestParam @Nullable String channelOrderNo,
			  @RequestParam @Nullable String custNm,
			@RequestParam @Nullable String custHp,
			@RequestParam @Nullable String custTel,
			@RequestParam @Nullable String deliNm,
			@RequestParam @Nullable String deliHp,
			@RequestParam @Nullable String deliTel
									   ) {

		System.out.println("getOrderList");

		HashMap<String, Object> map = new HashMap<>();

		if (startDt != null) {

			LocalDateTime start = startDt.atStartOfDay();

			map.put("startDt", start);
		}
		if (endDt != null) {

			LocalDateTime end = endDt.atTime(23, 59, 59);
			map.put("endDt", end);
		}
		if (statusCd != null && !statusCd.equals("")) {
			map.put("orderStatus", statusCd);
		}
		if (orderId != null && !orderId.equals("")) {
			map.put("orderId", orderId);
		}
		if (custNm != null && !custNm.equals("")) {
			map.put("custNm", custNm);
		}
		if (custHp != null && !custHp.equals("")) {
			map.put("custHp", custHp);
		}
		if (custTel != null && !custTel.equals("")) {
			map.put("custTel", custTel);
		}
		if (deliNm != null && !deliNm.equals("")) {
			map.put("deliNm", deliNm);
		}
		if (deliHp != null && !deliHp.equals("")) {
			map.put("deliHp", deliHp);
		}
		if (deliTel != null && !deliTel.equals("")) {
			map.put("deliTel", deliTel);
		}
		if (channelOrderNo != null && !channelOrderNo.equals("")) {
			map.put("channelOrderNo", channelOrderNo);
		}

		List<OrderMasterListResponseData> r = myBatisOrderService.getOrderMasterList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

	}

	/**
	 * ???????????????????????? ???????????? api
	 */
	@GetMapping(path = "/goods/items")
	public ResponseEntity getOrderDetailList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
	@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
	@RequestParam @Nullable String statusCd,
	@RequestParam @Nullable String orderId,
	@RequestParam @Nullable String channelOrderNo,
	@RequestParam @Nullable String custNm,
	@RequestParam @Nullable String custHp,
	@RequestParam @Nullable String custTel,
	@RequestParam @Nullable String deliNm,
	@RequestParam @Nullable String deliHp,
			@RequestParam @Nullable String deliTel, @RequestParam @Nullable String assortId,
			@RequestParam @Nullable String assortNm
									   ) {

		System.out.println("getOrderDetailList");

		HashMap<String, Object> map = new HashMap<>();

		if (startDt != null) {

			LocalDateTime start = startDt.atStartOfDay();

			map.put("startDt", start);
		}
		if (endDt != null) {

			LocalDateTime end = endDt.atTime(23, 59, 59);
			map.put("endDt", end);
		}
		if (statusCd != null && !statusCd.equals("")) {
			map.put("orderStatus", statusCd);
		}
		if (orderId != null && !orderId.equals("")) {
			map.put("orderId", orderId);
		}
		if (custNm != null && !custNm.equals("")) {
			map.put("custNm", custNm);
		}
		if (custHp != null && !custHp.equals("")) {
			map.put("custHp", custHp);
		}
		if (custTel != null && !custTel.equals("")) {
			map.put("custTel", custTel);
		}
		if (deliNm != null && !deliNm.equals("")) {
			map.put("deliNm", deliNm);
		}
		if (deliHp != null && !deliHp.equals("")) {
			map.put("deliHp", deliHp);
		}
		if (deliTel != null && !deliTel.equals("")) {
			map.put("deliTel", deliTel);
		}
		if (channelOrderNo != null && !channelOrderNo.equals("")) {
			map.put("channelOrderNo", channelOrderNo);
		}


		if (assortId != null && !assortId.equals("")) {
			map.put("assortId", assortId);
		}

		if (assortNm != null && !assortNm.equals("")) {
			map.put("assortNm", assortNm);
		}

		List<OrderMasterListResponseData> r = myBatisOrderService.getOrderMasterList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

//		HashMap<String, Object> map = new HashMap<>();
//
//		if (startDt != null) {
//
//			LocalDateTime start = startDt.atStartOfDay();
//
//			map.put("startDt", start);
//		}
//		if (endDt != null) {
//
//			LocalDateTime end = endDt.atTime(23, 59, 59);
//			map.put("endDt", end);
//		}
//
//		if (orderId != null && !orderId.equals("")) {
//			map.put("orderId", orderId);
//		}
//		if (statusCd != null && !statusCd.equals("")) {
//			map.put("statusCd", statusCd);
//		}
//
//		if (channelOrderNo != null && !channelOrderNo.equals("")) {
//			map.put("channelOrderNo", channelOrderNo);
//		}
//
//		List<OrderMasterListResponseData> r = myBatisOrderService.getOrderDetailList2(map);
//
//		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
//		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/items/{orderId}")
	public ResponseEntity getOrder(@PathVariable String orderId) {

		System.out.println("getOrder");

		HashMap<String, Object> map = new HashMap<>();

		map.put("orderId", orderId);

		OrderDetailResponseData r = myBatisOrderService.getOrderDetail(map);

		// List<OrderMasterListResponseData> r = myBatisOrderService.get(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/items/{orderId}/{orderSeq}")
	public ResponseEntity getOrder(@PathVariable String orderId, @PathVariable String orderSeq) {

		System.out.println("testest");

		TbOrderDetail t = jpaOrderService.getNullTest(orderId, orderSeq);

		// List<OrderMasterListResponseData> r = myBatisOrderService.get(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), t);
		return ResponseEntity.ok(res);

	}

//	@GetMapping(path = "/")


	@GetMapping(path = "/test/sms")
	public ResponseEntity smsSendTest(@RequestParam String body, @RequestParam String tbOrderNo){
		jpaOrderService.testSms(body, tbOrderNo, "test");
		return null;
	}

	// 20220307 rjb80 requestbody ??????
	// 20220307 rjb80 ????????????????????? api???.
	@PostMapping(path = "/ifoption")
	public ResponseEntity saveOrderOption(
			@RequestBody OrderOptionRequestData req) {

		System.out.println(req);

		String userId = req.getUserId();

		Boolean ret = jpaOrderService.saveGoodsIfoption(req.getOrderId(), req.getOrderSeq(), req.getAssortId(),
				req.getChannelGoodsNo(),
				req.getChannelOptionSno(), userId);

		if (ret) {
			jpaOrderService.noOptionChangeOrderStatus(req.getOrderId(), req.getOrderSeq(), userId);
		}

		ApiResponseMessage res = null;

		// log.debug(r.size());

		res = new ApiResponseMessage<String>("SUCCESS", "", "");

		return ResponseEntity.ok(res);

	}

	@PostMapping(path = "/items/cancel")
	public ResponseEntity cancelOrder(
			@RequestBody @Valid CancelOrderRequestData param) {


//CancelOrderRequestData

		System.out.println("cancelOrder");

		String userId = param.getUserId();

		// List<HashMap<String, Object>> l = new ArrayList<HashMap<String, Object>>();

		String chk = "";

		for (CancelOrderRequestData.Item o : param.getItems()) {
			HashMap<String, Object> m = new HashMap<String, Object>();

			m.put("orderId", o.getOrderId());
			m.put("orderSeq", o.getOrderSeq());
			m.put("cancelGb", o.getCancelGb());
			m.put("cancelMsg", o.getCancelMsg());
			// m.put("cancelQty", o.getCancelQty());
			// m.put("ifCancelGb", o.getIfCancelGb());
			m.put("seq", o.getSeq());
			m.put("userId", userId);

			if (o.getChannelGb().equals("01")) {
				boolean r = jpaOrderService.cancelGodoOrder(m, userId);
				System.out.println(r);
				if (r == false) {
					chk = "error";
				}
			} else {
				System.out.println("???????????? ??????");
			}

			// l.add(m);
		}


		// HashMap

		// param

		// TbOrderDetail t = jpaOrderService.getNullTest(orderId, orderSeq);

		// List<OrderMasterListResponseData> r = myBatisOrderService.get(map);

		ApiResponseMessage res = null;

		if (chk.equals("error")) {
			res = new ApiResponseMessage(StringFactory.getStrOk(), "error", "");
		} else {
			res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), "");
		}

//		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), t);
		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/cancel/items")
	public ResponseEntity getOrderCancelList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
			@RequestParam @Nullable String ifStatus, @RequestParam @Nullable String orderName) {

		System.out.println("getOrderDetailList");

		HashMap<String, Object> map = new HashMap<>();

		if (startDt != null) {

			LocalDateTime start = startDt.atStartOfDay();

			map.put("startDt", start);
		}
		if (endDt != null) {

			LocalDateTime end = endDt.atTime(23, 59, 59);
			map.put("endDt", end);
		}
		if (ifStatus != null && !ifStatus.equals("")) {
			map.put("ifStatus", ifStatus);
		}
		if (orderName != null && !orderName.trim().equals("")) {
			map.put("orderName", orderName);
		}

		CancelOrderListResponse r = myBatisOrderService.getOrderCancelList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

	}

	// ?????????
	// unpurchased
	@GetMapping(path = "/waitStatus/items")
	public ResponseEntity getOrderStatusWatingItems(@RequestParam String statusCd, @RequestParam int waitCnt,
			@RequestParam @Nullable String assortGb) {

		OrderStatusWatingItemListResponseData r = jpaOrderService.getOrderStatusWatingItems(statusCd, waitCnt,
				assortGb);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		if (res == null) {
			return null;
		}

		return ResponseEntity.ok(res);
	}

	/**
	 * ???????????????????????? ???????????? api
	 */
	@GetMapping(path = "/goods/special-items")
	// ????????????,????????????,????????????,( ????????????????????? ??????????????????(?????????),????????????,????????????????????????,?????????????????????,????????????,????????????????????????,?????????
	// ????????????,????????????
	public ResponseEntity getSpecialOrderMasterList(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt) {

		System.out.println("getSpecialOrderMasterList");

		HashMap<String, Object> map = new HashMap<>();

		if (startDt != null) {

			LocalDateTime start = startDt.atStartOfDay();

			map.put("startDt", start);
		}
		if (endDt != null) {

			LocalDateTime end = endDt.atTime(23, 59, 59);
			map.put("endDt", end);
		}


		List<OrderMasterListResponseData> r = myBatisOrderService.getSpecialOrderMasterList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

	}

}
