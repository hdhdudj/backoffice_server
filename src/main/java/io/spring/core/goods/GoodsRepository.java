package io.spring.core.goods;

import java.util.HashMap;
import java.util.List;

public interface GoodsRepository {

	List<HashMap<String, Object>> selectGoodsListByCondition(HashMap<String, Object> param);
}
