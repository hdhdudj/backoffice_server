package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MoveMapper {
	
	List<HashMap<String, Object>> getOrderMoveList(HashMap<String, Object> param);
	
}
