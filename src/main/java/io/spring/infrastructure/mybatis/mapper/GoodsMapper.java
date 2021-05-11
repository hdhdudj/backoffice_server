package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.spring.model.goods.request.GoodsInsertRequestData;

@Mapper
public interface GoodsMapper {
	
	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItvari(GoodsInsertRequestData goodsInsertRequestData);

	String selectMaxSeqItasrd(GoodsInsertRequestData goodsInsertRequestData);

    HashMap<String, Object> selectOneSeqOptionGb(GoodsInsertRequestData.Items items);

    String selectMaxItemIdItitmm(GoodsInsertRequestData goodsInsertRequestData);

    String selectMaxSeqItasrt(GoodsInsertRequestData goodsInsertRequestData);

	List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param);
}
