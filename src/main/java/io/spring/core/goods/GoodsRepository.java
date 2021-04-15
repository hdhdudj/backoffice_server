package io.spring.core.goods;

import java.util.HashMap;
import java.util.List;

import io.spring.data.goods.GoodsRequestData;

public interface GoodsRepository {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);
}
