package io.spring.dao.purchase;

import io.spring.infrastructure.mybatis.mapper.PurchaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class MyBatisPurchaseDaoImpl implements MyBatisPurchaseDao {
    private final PurchaseMapper purchaseMapper;

    @Autowired
    public MyBatisPurchaseDaoImpl(PurchaseMapper purchaseMapper) {
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param){
        return purchaseMapper.selectPurchaseListByCondition(param);
    }
}
