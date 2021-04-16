package io.spring.infrastructure.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.core.goods.GoodsRepository;
import io.spring.data.goods.GoodsRequestData;
import io.spring.infrastructure.mybatis.mapper.GoodsMapper;

@Repository
public class MyBatisGoodsService implements GoodsRepository {
	private final GoodsMapper goodsMapper;
	
	@Autowired
	public MyBatisGoodsService(GoodsMapper goodsMapper) {
		this.goodsMapper = goodsMapper;
	}
	
	@Override
	public List<HashMap<String, Object>> selectGoodsListAll(){
		return goodsMapper.selectGoodsListAll();
	}
	
	@Override
	public Boolean insertGoods(GoodsRequestData goodsRequestData) {
		return goodsMapper.insertGoods(goodsRequestData);
	}
}
