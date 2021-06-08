package io.spring.service.purchase;

import io.spring.dao.purchase.MyBatisPurchaseDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBatisPurchaseService {
    private final MyBatisPurchaseDao myBatisPurchaseDao;

    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param) {
        List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getPurchaseList(param);
        return purchaseList;
    }
}
