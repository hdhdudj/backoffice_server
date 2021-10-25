package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

	List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderMasterList(HashMap<String, Object> param);

}
