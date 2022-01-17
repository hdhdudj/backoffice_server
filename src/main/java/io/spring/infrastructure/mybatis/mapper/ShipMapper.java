package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShipMapper {
	
	List<HashMap<String, Object>> getOrderShipList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderAddGoodsShipList(HashMap<String, Object> param);
}
