package io.spring.dao.purchase;

import java.util.HashMap;
import java.util.List;

public interface MyBatisPurchaseDao {
    List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderListByPurchaseVendor(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderListByPurchaseVendorItem(HashMap<String, Object> param);

	HashMap<String, Object> getPurchase(HashMap<String, Object> param);

	List<HashMap<String, Object>> getPurchaseItems(HashMap<String, Object> param);

}
