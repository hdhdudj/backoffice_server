package io.spring.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.spring.core.order.OrderRepository;
import io.spring.infrastructure.util.ApiResponseMessageWithTuiGrid;


@RestController
public class OrderApi {

	private OrderRepository orderRepository;

	@Autowired
	public OrderApi(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@RequestMapping(path = "/orderList", method = RequestMethod.GET)
	public ResponseEntity selectOrderListByCondition(@Valid @RequestBody Map<String, Object> param) {

		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

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
