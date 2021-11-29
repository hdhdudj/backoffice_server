package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.order.MyBatisOrderDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.order.entity.OrderStock;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.request.OrderOptionRequestData;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.model.order.response.OrderDetailListResponse;
import io.spring.model.order.response.OrderDetailResponseData;
import io.spring.model.order.response.OrderMasterListResponseData;
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

	// orderId, orderSeq를 받아서 주문 상태를 바꿔주는 주소
	@RequestMapping(path = "/orderstatus", method = RequestMethod.GET)
	public ResponseEntity changeOrderStatus(@RequestParam String orderId, @RequestParam String orderSeq) {
		log.debug("changeOrderStatus 실행.");
		jpaOrderService.changeOrderStatus(orderId, orderSeq);


		TbOrderDetail t = jpaOrderService.getOrderDetail(orderId, orderSeq);

		System.out.println("--------------------------------------------------------------------------------------------");

		System.out.println(t);


		ApiResponseMessage res = null;
		return ResponseEntity.ok(res);
	}


	@GetMapping(path = "/items")
	public ResponseEntity getOrderList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
			  @RequestParam @Nullable String orderId,
			  @RequestParam @Nullable String custNm,
			@RequestParam @Nullable String custHp, @RequestParam @Nullable String channelOrderNo) {

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

		if (orderId != null && !orderId.equals("")) {
			map.put("orderId", orderId);
		}
		if (custNm != null && !custNm.equals("")) {
			map.put("custNm", custNm);
		}
		if (custHp != null && !custHp.equals("")) {
			map.put("custHp", custHp);
		}
		
		if (channelOrderNo != null && !channelOrderNo.equals("")) {
			map.put("channelOrderNo", channelOrderNo);
		}

		List<OrderMasterListResponseData> r = myBatisOrderService.getOrderMasterList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/goods/items")
	public ResponseEntity getOrderDetailList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
			@RequestParam @Nullable String orderId, @RequestParam @Nullable String statusCd,
			@RequestParam @Nullable String channelOrderNo) {

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

		if (orderId != null && !orderId.equals("")) {
			map.put("orderId", orderId);
		}
		if (statusCd != null && !statusCd.equals("")) {
			map.put("statusCd", statusCd);
		}

		if (channelOrderNo != null && !channelOrderNo.equals("")) {
			map.put("channelOrderNo", channelOrderNo);
		}

		List<OrderDetailListResponse> r = myBatisOrderService.getOrderDetailList(map);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
		return ResponseEntity.ok(res);

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

//	@GetMapping(path = "/items/{orderId}/{orderSeq}")
//	public ResponseEntity getOrder(@PathVariable String orderId, @PathVariable String orderSeq) {
//
//		System.out.println("testest");
//
//		TbOrderDetail t = jpaOrderService.getNullTest(orderId, orderSeq);
//
//		// List<OrderMasterListResponseData> r = myBatisOrderService.get(map);
//
//		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), t);
//		return ResponseEntity.ok(res);
//
//	}

//	@GetMapping(path = "/")


	@GetMapping(path = "/test/sms")
	public ResponseEntity smsSendTest(@RequestParam String body, @RequestParam String tbOrderNo){
		jpaOrderService.testSms(body, tbOrderNo);
		return null;
	}

	@PostMapping(path = "/ifoption")
	public ResponseEntity saveOrderOption(
			@RequestBody OrderOptionRequestData req) {

		System.out.println(req);

		Boolean ret = jpaOrderService.saveGoodsIfoption(req.getOrderId(), req.getOrderSeq(), req.getAssortId(),
				req.getChannelGoodsNo(),
				req.getChannelOptionSno());

		if (ret) {
			jpaOrderService.changeOrderStatus(req.getOrderId(), req.getOrderSeq());
		}

		ApiResponseMessage res = null;

		// log.debug(r.size());

		res = new ApiResponseMessage<String>("SUCCESS", "", "");

		return ResponseEntity.ok(res);

	}

}
