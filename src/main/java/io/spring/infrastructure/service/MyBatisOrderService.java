package io.spring.infrastructure.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.core.order.OrderRepository;
import io.spring.infrastructure.mybatis.mapper.OrderMapper;

@Repository
public class MyBatisOrderService implements OrderRepository {
	private final OrderMapper orderMapper;

	@Autowired
	public MyBatisOrderService(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	@Override
	public List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param) {
		return orderMapper.selectOrderListByCondition(param);
	}

}
