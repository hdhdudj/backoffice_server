package io.spring.controller;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.order.MyBatisOrderDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.service.common.JpaCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping(value = "/order")
public class OrderController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private MyBatisOrderDao myBatisOrderDao;
	private MyBatisCommonDao myBatisCommonDao;

	private JpaCommonService jpaCommonService;

	@Autowired
	public OrderController(MyBatisOrderDao myBatisOrderDao, MyBatisCommonDao myBatisCommonDao,
			JpaCommonService jpaCommonService) {
		this.myBatisOrderDao = myBatisOrderDao;
		this.myBatisCommonDao = myBatisCommonDao;
		this.jpaCommonService = jpaCommonService;
	}

	@RequestMapping(path = "/orders", method = RequestMethod.GET)
	// public ResponseEntity selectOrderListByCondition(@Valid @RequestBody
	// Map<String, Object> param) {

	public ResponseEntity selectOrderListByCondition(@RequestParam String channelGb, @RequestParam String orderFromDt,
			@RequestParam String orderEndDt) {
		
		

		/*
		 * 
		 * logger.debug("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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

		logger.debug(channelGb);
		logger.debug(orderFromDt);
		logger.debug(orderEndDt);

		long rx = jpaCommonService.getSequence("seq_TMPSEQ");

		System.out.println("rx = " + rx);

		HashMap<String, Object> h = new HashMap<String, Object>();

		h.put("channelGb", channelGb);
		h.put("orderFromDt", orderFromDt);
		h.put("orderEndDt", orderEndDt);

		List<HashMap<String, Object>> r = myBatisOrderDao.selectOrderListByCondition(h);
		
		ApiResponseMessage res = null;

		// logger.debug(r.size());


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


	@RequestMapping(path = "/orderstatus", method = RequestMethod.GET)
	public ResponseEntity changeOrderStatus(@RequestParam String assortGb, @RequestParam String assortId){

		return null;
	}

}
