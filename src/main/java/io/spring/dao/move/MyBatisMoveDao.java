package io.spring.dao.move;

import java.util.HashMap;
import java.util.List;

public interface MyBatisMoveDao {

	List<HashMap<String, Object>> getOrderMoveList(HashMap<String, Object> param);
	

}
