package io.spring.dao.common;

import java.util.HashMap;

public interface CommonRepository {
	HashMap<String, Object> getSequence(HashMap<String, Object> param);

}
