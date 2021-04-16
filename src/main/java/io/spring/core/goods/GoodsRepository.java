package io.spring.core.goods;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.data.goods.GoodsRequestData;
import io.spring.data.goods.Itasrt;

public interface GoodsRepository{

	List<HashMap<String, Object>> selectGoodsListAll();
	
	Boolean insertGoods(GoodsRequestData goodsRequestData);
	
}
