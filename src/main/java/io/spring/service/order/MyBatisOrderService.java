package io.spring.service.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.order.MyBatisOrderDao;
import io.spring.model.order.response.OrderMasterListResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisOrderService {
	private final MyBatisOrderDao myBatisOrderDao;


	public List<OrderMasterListResponseData> getOrderMasterList(HashMap<String, Object> map) {


		List<HashMap<String, Object>> list = myBatisOrderDao.getOrderMasterList(map);

		List<OrderMasterListResponseData> orderMasterListDataListResponse = new ArrayList<>();

		for (HashMap<String, Object> o : list) {
			OrderMasterListResponseData orderMasterListResponseData = new OrderMasterListResponseData(o);
			orderMasterListDataListResponse.add(orderMasterListResponseData);
		}


		return orderMasterListDataListResponse;
	}

}
