package io.spring.model.order.response;

import java.math.BigDecimal;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문이동지시 조회 리스트 가져오는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMasterListResponseData {


	public OrderMasterListResponseData(HashMap<String, Object> m) {


		this.channelGb = (String) m.get("channelGb");
		this.orderDate = m.get("orderDate").toString().substring(0, 19);
		this.orderId = (String) m.get("orderId");
		this.channelOrderNo = (String) m.get("channelOrderNo");
		this.custId = m.get("custId") == null ? null : (int) m.get("custId");
		this.custNm = m.get("custNm") == null ? null : (String) m.get("custNm");
		this.goodsNm = (String) m.get("goodsNm");

		this.totalGoodsPrice = ((BigDecimal) m.get("totalGoodsPrice")).doubleValue();
		this.totalDeliveryCharge = ((BigDecimal) m.get("totalDeliveryCharge")).doubleValue();
		this.totalPrice = ((BigDecimal) m.get("totalPrice")).doubleValue();
		this.orderAmt = ((BigDecimal) m.get("orderAmt")).doubleValue();
		
		this.payGb = (String) m.get("payGb");
		this.payStatus = (String) m.get("payStatus");
		

		
	}




	private String channelGb;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
	// timezone = "Asia/Seoul")
	private String orderDate;
    private String orderId;
	private String channelOrderNo;
	private int custId;
	private String custNm;
	private String goodsNm;
	private double totalGoodsPrice;
	private double totalDeliveryCharge;
	private double totalPrice;
	private double orderAmt;
	private String payGb;
	private String payStatus;

}
