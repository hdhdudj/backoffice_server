package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper {
	List<HashMap<String, Object>> selectGoodsListByCondition(HashMap<String, Object> param);
}
