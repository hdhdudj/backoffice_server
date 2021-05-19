package io.spring.dao.common;

import java.util.HashMap;

public interface MyBatisCommonDao {
	HashMap<String, Object> getSequence(HashMap<String, Object> param);
	HashMap<String, Object> getCategory(HashMap<String, Object> param);
}
