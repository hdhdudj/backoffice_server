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

//	HashMap<String, Object> getOrderMaster(HashMap<String, Object> param);

//	List<HashMap<String, Object>> getOrderDetail(HashMap<String, Object> param);

	@Override
	public HashMap<String, Object> getOrderMaster(HashMap<String, Object> param) {
		return orderMapper.getOrderMaster(param);
	}

	@Override
	public List<HashMap<String, Object>> getOrderDetail(HashMap<String, Object> param) {
		return orderMapper.getOrderDetail(param);
	}

	@Override
	public List<HashMap<String, Object>> getOrderDetailList(HashMap<String, Object> param) {
		return orderMapper.getOrderDetailList(param);
	}

	@Override
	public List<HashMap<String, Object>> getOrderCancelList(HashMap<String, Object> param) {
		return orderMapper.getOrderCancelList(param);
	}

	@Override
	public HashMap<String, Object> getOrderStatusDate(HashMap<String, Object> param) {
		return orderMapper.getOrderStatusDate(param);
	}

	@Override
	public List<HashMap<String, Object>> getSpecialOrderMasterList(HashMap<String, Object> param) {
		return orderMapper.getSpecialOrderMasterList(param);
	}

}

