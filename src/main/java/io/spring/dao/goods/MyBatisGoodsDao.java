package io.spring.dao.goods;

import io.spring.model.goods.GoodsRequestData;

import java.util.HashMap;
import java.util.List;

public interface MyBatisGoodsDao {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);

	String selectMaxSeqItvari(GoodsRequestData goodsRequestData);

	String selectMaxSeqItasrd(GoodsRequestData goodsRequestData);

	HashMap<String, Object> selectOneSeqOptionGb(GoodsRequestData.Items items);

	public String selectMaxItemIdItitmm(GoodsRequestData goodsRequestData);
}
