package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonMapper {

	HashMap<String, Object> getSequence(HashMap<String, Object> param);
	HashMap<String, Object> getCategory(HashMap<String, Object> param);

}
