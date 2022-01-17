package io.spring.dao.ship;

import java.util.HashMap;
import java.util.List;

public interface MyBatisShipDao {

	List<HashMap<String, Object>> getOrderShipList(HashMap<String, Object> param);

	List<HashMap<String, Object>> getOrderAddGoodsShipList(HashMap<String, Object> param);
}
