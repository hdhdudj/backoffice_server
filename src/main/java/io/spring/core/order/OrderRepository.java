package io.spring.core.order;

import java.util.HashMap;
import java.util.List;

public interface OrderRepository {
//	  void save(Comment comment);
//
	// Optional<Comment> findById(String articleId, String id);

	// void remove(Comment comment);

	List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param);

}
