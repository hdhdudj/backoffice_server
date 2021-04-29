package io.spring.service.goods;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.model.goods.response.GoodsInsertResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class MyBatisGoodsService {
    @Autowired
    private MyBatisGoodsDao myBatisGoodsDao;
    public GoodsInsertResponseData getGoodsList(String shortageYn, Date regDtBegin, Date regDtEnd) {
        List<HashMap<String, String>> goodsList = myBatisGoodsDao.getGoodsList(shortageYn, regDtBegin, regDtEnd);
        GoodsInsertResponseData goodsInsertResponseData = null;//makeGoodsSelectListResponseData(goodsList);
        return goodsInsertResponseData;
    }
}
