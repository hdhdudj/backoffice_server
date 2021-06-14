package io.spring.service.purchase;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.purchase.MyBatisPurchaseDao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisPurchaseService {
    private final MyBatisPurchaseDao myBatisPurchaseDao;

    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param) {
        List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getPurchaseList(param);
        return purchaseList;
    }

	public List<HashMap<String, Object>> getOrderListByPurchaseVendor(HashMap<String, Object> param) {
		List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getOrderListByPurchaseVendor(param);
		return purchaseList;
	}

	public List<HashMap<String, Object>> getOrderListByPurchaseVendorItem(HashMap<String, Object> param) {
		List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getOrderListByPurchaseVendorItem(param);
		return purchaseList;
	}
}
