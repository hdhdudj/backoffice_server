package io.spring.dao.goods;

import java.util.HashMap;
import java.util.List;

import io.spring.model.goods.request.GoodsInsertRequestData;

public interface MyBatisGoodsDao {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData);

	HashMap<String, Object> selectOneSeqOptionGb(GoodsInsertRequestData.Items items);

	String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData);

	List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param);
}
