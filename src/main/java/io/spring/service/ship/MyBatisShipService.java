package io.spring.service.ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.ship.MyBatisShipDao;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisShipService {
	private final MyBatisShipDao myBatisShipDao;

//	public List<HashMap<String, Object>> getOrderMoveList(HashMap<String, Object> param) {
	// List<HashMap<String, Object>> List = myBatisMoveDao.getOrderMoveList(param);

//		return List;
	// }

	public List<ShipIndicateSaveListResponseData.Ship> getOrderShipList(HashMap<String, Object> map) {


		List<ShipIndicateSaveListResponseData.Ship> shipList = new ArrayList<>();

		List<HashMap<String, Object>> list = myBatisShipDao.getOrderShipList(map);

		// List<OrderMoveListResponseData> orderMoveListDataListResponse = new
		// ArrayList<>();

		for (HashMap<String, Object> o : list) {

			ShipIndicateSaveListResponseData.Ship ship = new ShipIndicateSaveListResponseData.Ship(o);

			shipList.add(ship);

			// OrderMoveListResponseData orderMoveListResponseData = new
			// OrderMoveListResponseData(o);
			// orderMoveListDataListResponse.add(orderMoveListResponseData);
		}


		return shipList;
	}

}
