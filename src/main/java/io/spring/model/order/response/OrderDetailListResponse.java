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
public class OrderDetailListResponse {




	public OrderDetailListResponse(HashMap<String, Object> map) {
		this.channelGb = (String) map.get("channelGb");
		this.orderDate = map.get("orderDate").toString().substring(0, 19);
			this.orderId = (String) map.get("orderId");
			this.orderSeq = (String) map.get("orderSeq");
			this.orderKey = (String) map.get("orderKey");
			this.statusCd = (String) map.get("statusCd");
			this.custNm = (String) map.get("custNm");
			this.assortId = (String) map.get("assortId");
			this.itemId = (String) map.get("itemId");
			this.goodsNm = (String) map.get("goodsNm");
			this.optionInfo = (String) map.get("optionInfo");
			this.optionNm1 = (String) map.get("optionNm1");
			this.optionNm2 = (String) map.get("optionNm2");
			this.qty = Long.valueOf((int) map.get("qty"));
			this.salePrice = ((BigDecimal) map.get("salePrice")).doubleValue();

			this.deliPrice = ((BigDecimal) map.get("deliPrice")).doubleValue();

			this.dcSumPrice = ((BigDecimal) map.get("dcSumPrice")).doubleValue();
			this.totalPrice = ((BigDecimal) map.get("totalPrice")).doubleValue();

			this.listImageData = (String) map.get("listImageData");

			this.channelOrderNo = (String) map.get("channelOrderNo");
			this.channelOrderSeq = (String) map.get("channelOrderSeq");

		}

		private String channelGb;
		// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
		// timezone = "Asia/Seoul")
		private String orderDate;
		private String orderId; // 주문번호
		private String orderSeq; // 주문순번
		private String orderKey; // 주문키
		private String statusCd; // 주문상태
		private String custNm;
		private String assortId; // 품목코드
		private String itemId; // 상품코드
		private String goodsNm; // 상품명
		private String optionInfo; // 제휴옵션정보
		private String optionNm1; // 옵션1
		private String optionNm2; // 옵션2
		private Long qty; // 수량
		private double salePrice; // 판매가
		private double deliPrice; // 배송비
		private double dcSumPrice;
		private double totalPrice;
		private String listImageData; // 이미지
		private String channelOrderNo;
		private String channelOrderSeq;



}
