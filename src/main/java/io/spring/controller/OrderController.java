package io.spring.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.order.MyBatisOrderDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.order.entity.OrderStock;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.order.JpaOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {
	private final MyBatisOrderDao myBatisOrderDao;
	private final MyBatisCommonDao myBatisCommonDao;

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
	public ResponseEntity selectOrderListByCondition(@RequestParam String channelGb, @RequestParam String orderFromDt,
			@RequestParam String orderEndDt) {
		
		

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
		log.debug(orderFromDt);
		log.debug(orderEndDt);

		long rx = jpaCommonService.getSequence("seq_TMPSEQ");

		System.out.println("rx = " + rx);

		HashMap<String, Object> h = new HashMap<String, Object>();

		h.put("channelGb", channelGb);
		h.put("orderFromDt", orderFromDt);
		h.put("orderEndDt", orderEndDt);

		List<HashMap<String, Object>> r = myBatisOrderDao.selectOrderListByCondition(h);
		
		ApiResponseMessage res = null;

		// log.debug(r.size());


		if (r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS", "", r);
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
	public ResponseEntity changeOrderStatus(@RequestParam String orderId, @RequestParam String orderSeq){
		log.debug("changeOrderStatus 실행.");
		jpaOrderService.changeOrderStatus(orderId, orderSeq);
		ApiResponseMessage res = null;
		return ResponseEntity.ok(res);
	}

}
