package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.spring.data.goods.GoodsRequestData;

@Mapper
public interface GoodsMapper {
	
	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);
}
