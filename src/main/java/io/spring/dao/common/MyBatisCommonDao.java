package io.spring.dao.common;

import java.util.HashMap;
import java.util.List;

public interface MyBatisCommonDao {
	HashMap<String, Object> getSequence(HashMap<String, Object> param);
	HashMap<String, Object> getCategory(HashMap<String, Object> param);
	List<HashMap<String, Object>> getBrandSearchList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getPurchaseVendorSearchList(HashMap<String, Object> param);
}
