package io.spring.core.common;

import java.util.HashMap;

public interface CommonRepository {
	HashMap<String, Object> getSequence(HashMap<String, Object> param);

}
