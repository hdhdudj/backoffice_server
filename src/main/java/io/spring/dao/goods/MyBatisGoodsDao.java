package io.spring.dao.goods;

import io.spring.model.goods.request.GoodsInsertRequestData;

import java.util.HashMap;
import java.util.List;

public interface MyBatisGoodsDao {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData);

	HashMap<String, Object> selectOneSeqOptionNm(String assortId, String optionNm);

	String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData);

	List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getGoodsItemList(HashMap<String, Object> param);
}
