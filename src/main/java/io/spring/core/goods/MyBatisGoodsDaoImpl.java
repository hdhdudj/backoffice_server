package io.spring.core.goods;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.model.goods.GoodsRequestData;
import io.spring.infrastructure.mybatis.mapper.GoodsMapper;

@Repository
public class MyBatisGoodsDaoImpl implements MyBatisGoodsDao {
	private final GoodsMapper goodsMapper;
	
	@Autowired
	public MyBatisGoodsDaoImpl(GoodsMapper goodsMapper) {
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
