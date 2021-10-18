package io.spring.dao.ship;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.ShipMapper;

@Repository
public class MyBatisShipDaoImpl implements MyBatisShipDao {
	private final ShipMapper shipMapper;
	
	@Autowired
	public MyBatisShipDaoImpl(ShipMapper shipMapper) {
		this.shipMapper = shipMapper;
	}
	
	

	@Override
	public List<HashMap<String, Object>> getOrderShipList(HashMap<String, Object> param) {
		return shipMapper.getOrderShipList(param);
	};

}
