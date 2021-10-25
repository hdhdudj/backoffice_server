package io.spring.dao.order;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.OrderMapper;

@Repository
public class MyBatisOrderDaoImpl implements MyBatisOrderDao {
	private final OrderMapper orderMapper;

	@Autowired
	public MyBatisOrderDaoImpl(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	@Override
	public List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param) {
		return orderMapper.selectOrderListByCondition(param);
	}

	@Override
	public List<HashMap<String, Object>> getOrderMasterList(HashMap<String, Object> param) {
		return orderMapper.getOrderMasterList(param);
	}

}
