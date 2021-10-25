package io.spring.service.move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.move.MyBatisMoveDao;
import io.spring.model.move.response.OrderMoveListResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisMoveService {
	private final MyBatisMoveDao myBatisMoveDao;

//	public List<HashMap<String, Object>> getOrderMoveList(HashMap<String, Object> param) {
	// List<HashMap<String, Object>> List = myBatisMoveDao.getOrderMoveList(param);

//		return List;
	// }

	public List<OrderMoveListResponseData> getOrderMoveList(HashMap<String, Object> map) {


		List<HashMap<String, Object>> list = myBatisMoveDao.getOrderMoveList(map);

		List<OrderMoveListResponseData> orderMoveListDataListResponse = new ArrayList<>();

		for (HashMap<String, Object> o : list) {
			OrderMoveListResponseData orderMoveListResponseData = new OrderMoveListResponseData(o);
			orderMoveListDataListResponse.add(orderMoveListResponseData);
		}


		return orderMoveListDataListResponse;
	}

}
