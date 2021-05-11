package io.spring.service.goods;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spring.dao.goods.MyBatisGoodsDao;

@Service
public class MyBatisGoodsService {
    @Autowired
    private MyBatisGoodsDao myBatisGoodsDao;

	public List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsList(param);

        return goodsList;
    }
}
