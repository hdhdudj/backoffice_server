package io.spring.dao.goods;

import io.spring.model.goods.request.GoodsInsertRequestData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface MyBatisGoodsDao {

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData);

	HashMap<String, Object> selectOneSeqOptionGb(GoodsInsertRequestData.Items items);

	String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData);

    List<HashMap<String, Object>> getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd);
}
