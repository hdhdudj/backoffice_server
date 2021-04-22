package io.spring.dao.goods;

import io.spring.infrastructure.mybatis.mapper.GoodsMapper;
import io.spring.model.goods.GoodsRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

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

	@Override
	public String selectMaxSeqItasrt(GoodsRequestData goodsRequestData){return goodsMapper.selectMaxSeqItasrt(goodsRequestData);}

	@Override
	public String selectMaxSeqItvari(GoodsRequestData goodsRequestData){ return goodsMapper.selectMaxSeqItvari(goodsRequestData);}

	@Override
	public String selectMaxSeqItasrd(GoodsRequestData goodsRequestData){return goodsMapper.selectMaxSeqItasrd(goodsRequestData);}

	@Override
	public HashMap<String, Object> selectOneSeqOptionGb(GoodsRequestData.Items items){return goodsMapper.selectOneSeqOptionGb(items);}

	@Override
	public String selectMaxItemIdItitmm(GoodsRequestData goodsRequestData){return goodsMapper.selectMaxItemIdItitmm(goodsRequestData);}
}
