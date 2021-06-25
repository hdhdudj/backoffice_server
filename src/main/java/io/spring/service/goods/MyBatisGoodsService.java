package io.spring.service.goods;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.goods.MyBatisGoodsDao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisGoodsService {
    private final MyBatisGoodsDao myBatisGoodsDao;

	public List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsList(param);

        return goodsList;
    }

	public List<HashMap<String, Object>> getGoodsItemList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsItemList(param);

		return goodsList;
	}

	public List<HashMap<String, Object>> getGoodsItemListWithCategory(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsItemListWithCategory(param);

		return goodsList;
	}

}
