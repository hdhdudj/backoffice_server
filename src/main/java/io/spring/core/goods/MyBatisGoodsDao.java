package io.spring.core.goods;

import java.util.HashMap;
import java.util.List;

import io.spring.model.goods.GoodsRequestData;

public interface MyBatisGoodsDao {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);
	
}
