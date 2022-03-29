package io.spring.infrastructure.mybatis.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseMapper {
    List<HashMap<String, Object>> selectPurchaseListByCondition(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderListByPurchaseVendor(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderListByPurchaseVendorItem(HashMap<String, Object> param);

	HashMap<String, Object> getPurchase(HashMap<String, Object> param);

	List<HashMap<String, Object>> getPurchaseItems(HashMap<String, Object> param);

}
