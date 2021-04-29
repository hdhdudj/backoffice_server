package io.spring.dao.goods;

import io.spring.infrastructure.mybatis.mapper.GoodsMapper;
import io.spring.model.goods.request.GoodsInsertRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
	public Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData) {
		return goodsMapper.insertGoods(goodsInsertRequestData);
	}

	@Override
	public String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData){return goodsMapper.selectMaxSeqItasrt(goodsInsertRequestData);}

	@Override
	public String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData){ return goodsMapper.selectMaxSeqItvari(goodsInsertRequestData);}

	@Override
	public String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData){return goodsMapper.selectMaxSeqItasrd(goodsInsertRequestData);}

	@Override
	public HashMap<String, Object> selectOneSeqOptionGb(GoodsInsertRequestData.Items items){return goodsMapper.selectOneSeqOptionGb(items);}

	@Override
	public String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData){return goodsMapper.selectMaxItemIdItitmm(goodsInsertRequestData);}

	@Override
	public List<HashMap<String, Object>> getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd){return goodsMapper.getGoodsList(shortageYn, regDtBegin, regDtEnd);};
}
