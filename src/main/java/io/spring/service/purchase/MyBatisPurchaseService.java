package io.spring.service.purchase;

import io.spring.dao.purchase.MyBatisPurchaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class MyBatisPurchaseService {
    @Autowired
    private MyBatisPurchaseDao myBatisPurchaseDao;

    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param) {
        List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getPurchaseList(param);
        return purchaseList;
    }
}
