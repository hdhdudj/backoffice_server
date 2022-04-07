package io.spring.dao.goods;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.GoodsMapper;
import io.spring.model.goods.request.GoodsInsertRequestData;

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
	public HashMap<String, Object> selectOneSeqOptionNm(String assortId, String optionNm){return goodsMapper.selectOneSeqOptionNm(assortId, optionNm);}

	@Override
	public String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData){return goodsMapper.selectMaxItemIdItitmm(goodsInsertRequestData);}

	@Override
	public List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param) {
		return goodsMapper.getGoodsList(param);
	};

	@Override
	public List<HashMap<String, Object>> getGoodsItemList(HashMap<String, Object> param) {
		return goodsMapper.getGoodsItemList(param);
	};

	@Override
	public List<HashMap<String, Object>> getGoodsItemListWithCategory(HashMap<String, Object> param) {
		return goodsMapper.getGoodsItemListWithCategory(param);
	};

	@Override
	public List<HashMap<String, Object>> getGoodsStockList(HashMap<String, Object> param) {
		return goodsMapper.getGoodsStockList(param);
	};

	@Override
	public List<HashMap<String, Object>> getItitmc(HashMap<String, Object> param) {
		return goodsMapper.getItitmc(param);
	};

	@Override
	public List<HashMap<String, Object>> getProductsList(HashMap<String, Object> param) {
		return goodsMapper.getProductsList(param);
	};

}
