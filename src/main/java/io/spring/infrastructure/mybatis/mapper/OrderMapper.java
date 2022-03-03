package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

	List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderMasterList(HashMap<String, Object> param);

	HashMap<String, Object> getOrderMaster(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderDetail(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderDetailList(HashMap<String, Object> param);
	
	List<HashMap<String, Object>> getOrderCancelList(HashMap<String, Object> param);

	HashMap<String, Object> getOrderStatusDate(HashMap<String, Object> param);

	List<HashMap<String, Object>> getSpecialOrderMasterList(HashMap<String, Object> param);
}
