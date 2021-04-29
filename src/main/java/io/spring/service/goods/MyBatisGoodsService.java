package io.spring.service.goods;

import io.spring.dao.goods.MyBatisGoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class MyBatisGoodsService {
    @Autowired
    private MyBatisGoodsDao myBatisGoodsDao;

    public List<HashMap<String, Object>> getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd) {
        List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsList(shortageYn, regDtBegin, regDtEnd);

        return goodsList;
    }
}
