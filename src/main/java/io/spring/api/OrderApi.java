package io.spring.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.spring.core.common.CommonRepository;
import io.spring.core.order.OrderRepository;
import io.spring.infrastructure.util.ApiResponseMessageWithTuiGrid;


@RestController
public class OrderApi {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private OrderRepository orderRepository;
	private CommonRepository commonRepository;

	@Autowired
	public OrderApi(OrderRepository orderRepository, CommonRepository commonRepository) {
		this.orderRepository = orderRepository;
		this.commonRepository = commonRepository;
	}

	@RequestMapping(path = "/orderList", method = RequestMethod.GET)
	public ResponseEntity selectOrderListByCondition(@Valid @RequestBody Map<String, Object> param) {

		// System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

		logger.debug("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

		HashMap<String, Object> arr = new HashMap<String, Object>();

		arr.put("seqName", "seq_ITASRT");

		HashMap<String, Object> x1 = commonRepository.getSequence(arr);

		System.out.println("x1 = " + x1.get("nextval"));

		HashMap<String, Object> h = new HashMap<String, Object>();

		for (Map.Entry<String, Object> entry : param.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

			h.put(entry.getKey(), entry.getValue());

		}

		
		List<HashMap<String, Object>> r = orderRepository.selectOrderListByCondition(h);
		

		
		

		return ResponseEntity.ok(new ApiResponseMessageWithTuiGrid<List<HashMap<String, Object>>>(true, "", r));

		// UserData userData = userQueryService.findById(user.getId()).get();
		// return ResponseEntity.status(201).body(userResponse(new
		// UserWithToken(userData, jwtService.toToken(user))));
	}

}
