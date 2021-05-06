package io.spring.dao.purchase;

import java.util.HashMap;
import java.util.List;

public interface MyBatisPurchaseDao {
    List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param);
}
