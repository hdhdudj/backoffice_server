package io.spring.service.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.spring.enums.Scm;
import org.springframework.stereotype.Service;

import io.spring.dao.order.MyBatisOrderDao;
import io.spring.model.order.response.OrderDetailListResponse;
import io.spring.model.order.response.OrderDetailResponseData;
import io.spring.model.order.response.OrderMasterListResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisOrderService {
	private final MyBatisOrderDao myBatisOrderDao;


	public List<OrderMasterListResponseData> getOrderMasterList(HashMap<String, Object> map) {


		List<HashMap<String, Object>> list = myBatisOrderDao.getOrderMasterList(map);

		List<OrderMasterListResponseData> orderMasterListDataListResponseList = new ArrayList<>();

		for (HashMap<String, Object> o : list) {
			OrderMasterListResponseData orderMasterListResponseData = new OrderMasterListResponseData(o);
			orderMasterListResponseData.setScmNm(this.matchScmNoToScmNm(orderMasterListResponseData.getScmNo()));
			orderMasterListDataListResponseList.add(orderMasterListResponseData);
		}


		return orderMasterListDataListResponseList;
	}

	/**
	 * scmNo를 scmNm로 매칭시켜주는 함수
	 */
	private String matchScmNoToScmNm(String scmNo){
		for(Scm key : Scm.values()){
			if(key.getFieldName().equals(scmNo)){
				return key.toString();
			}
		}
		return null;
	}

	public OrderDetailResponseData getOrderDetail(HashMap<String, Object> map) {
		
		HashMap<String, Object> m = myBatisOrderDao.getOrderMaster(map);
		
		List<OrderDetailResponseData.Order> orders = new ArrayList<>();

		List<HashMap<String, Object>> l = myBatisOrderDao.getOrderDetail(map);
		
		OrderDetailResponseData orderDetailResponse = new OrderDetailResponseData(m);
		
		for (HashMap<String, Object> o : l) {
			OrderDetailResponseData.Order o2 = new OrderDetailResponseData.Order(o);

			orders.add(o2);

		}

		orderDetailResponse.setOrders(orders);

		return orderDetailResponse;
		
	}

	public List<OrderDetailListResponse> getOrderDetailList(HashMap<String, Object> map) {

		List<HashMap<String, Object>> list = myBatisOrderDao.getOrderDetailList(map);

		List<OrderDetailListResponse> orderDetailListResponse = new ArrayList<>();

		for (HashMap<String, Object> o : list) {
			OrderDetailListResponse r = new OrderDetailListResponse(o);
			orderDetailListResponse.add(r);
		}

		return orderDetailListResponse;

	}
}
