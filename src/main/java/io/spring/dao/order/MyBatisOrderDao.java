package io.spring.dao.order;

import java.util.HashMap;
import java.util.List;

public interface MyBatisOrderDao {
//	  void save(Comment comment);
//
	// Optional<Comment> findById(String articleId, String id);

	// void remove(Comment comment);

	List<HashMap<String, Object>> selectOrderListByCondition(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderMasterList(HashMap<String, Object> param);

	HashMap<String, Object> getOrderMaster(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderDetail(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderDetailList(HashMap<String, Object> param);

}
