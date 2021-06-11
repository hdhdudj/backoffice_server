package io.spring.dao.purchase;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisPurchaseDaoImpl implements MyBatisPurchaseDao {
    private final PurchaseMapper purchaseMapper;

//    @Autowired
//    public MyBatisPurchaseDaoImpl(PurchaseMapper purchaseMapper) {
//        this.purchaseMapper = purchaseMapper;
//    }

    @Override
    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param){
        return purchaseMapper.selectPurchaseListByCondition(param);
    }

	@Override
	public List<HashMap<String, Object>> getOrderListByPurchaseVendor(HashMap<String, Object> param) {
		return purchaseMapper.getOrderListByPurchaseVendor(param);
	}

	@Override
	public List<HashMap<String, Object>> getOrderListByPurchaseVendorItem(HashMap<String, Object> param) {
		return purchaseMapper.getOrderListByPurchaseVendorItem(param);
	}

}
