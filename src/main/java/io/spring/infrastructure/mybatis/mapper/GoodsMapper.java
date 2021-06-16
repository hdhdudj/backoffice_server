package io.spring.infrastructure.mybatis.mapper;

import io.spring.model.goods.request.GoodsInsertRequestData;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface GoodsMapper {
	
	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData);

    HashMap<String, Object> selectOneSeqOptionNm(String assortId, String optionNm);

    String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData);

    String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData);

	List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getGoodsItemList(HashMap<String, Object> param);
}
