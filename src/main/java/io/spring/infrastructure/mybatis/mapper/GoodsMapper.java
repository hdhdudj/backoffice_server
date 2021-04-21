package io.spring.infrastructure.mybatis.mapper;

import io.spring.model.goods.GoodsRequestData;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface GoodsMapper {
	
	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);

	String selectMaxSeqItvari(GoodsRequestData goodsRequestData);

	String selectMaxSeqItasrd(GoodsRequestData goodsRequestData);

    HashMap<String, Object> selectOneSeqOptionGb(GoodsRequestData.Items items);

    String selectMaxItemIdItitmm(GoodsRequestData goodsRequestData);
}
