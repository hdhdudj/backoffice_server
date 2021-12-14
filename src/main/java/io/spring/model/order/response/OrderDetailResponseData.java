package io.spring.model.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import io.spring.infrastructure.util.Utilities;
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
public class OrderDetailResponseData {

	public OrderDetailResponseData(HashMap<String, Object> map) {

		System.out.println(map);

		this.channelGb = (String) map.get("channelGb");
		this.orderId = (String) map.get("orderId");
		LocalDateTime dateTime = (LocalDateTime)map.get("orderDate");
		this.orderDate = map.get("orderDate") == null? null : Utilities.removeTAndTransToStr(dateTime).substring(0, 19); // 주문일자
		this.channelOrderNo = (String) map.get("channelOrderNo");
		this.custId = (int) map.get("custId");
		this.custNm = (String) map.get("custNm");
		this.custPcode = (String) map.get("custPcode");
		this.custTel = (String) map.get("custTel");
		this.custHp = (String) map.get("custHp");
		this.custZipcode = (String) map.get("custZipcode");
		this.custAddr1 = (String) map.get("custAddr1");
		this.custAddr2 = (String) map.get("custAddr2");
		this.deliNm = (String) map.get("deliNm");
		this.deliTel = (String) map.get("deliTel");
		this.deliHp = (String) map.get("deliHp");
		this.deliZipcode = (String) map.get("deliZipcode");
		this.deliAddr1 = (String) map.get("deliAddr1");
		this.deliAddr2 = (String) map.get("deliAddr2");
		this.payGb = (String) map.get("payGb");
		this.orderAmt = ((BigDecimal) map.get("orderAmt")).doubleValue();
		this.totalGoodsPrice = ((BigDecimal) map.get("totalGoodsPrice")).doubleValue();
		this.totalDeliveryCharge = ((BigDecimal) map.get("totalDeliveryCharge")).doubleValue();
		this.totalDiscountPrice = ((BigDecimal) map.get("totalDiscountPrice")).doubleValue();
		this.totalUseMileage = ((BigDecimal) map.get("totalUseMileage")).doubleValue();
		
	}


	private String channelGb; // 채널구분
	private String orderId; // 주문번호
	private String orderDate; // 주문일자
	private String channelOrderNo; // 제휴주문번호
	private int custId; // 고객번호
	private String custNm; // 주문자명
	private String custPcode; // 개인고유통관번호
	private String custTel; // 주문자 전화번호
	private String custHp; // 주문자 휴대폰번호
	private String custZipcode; // 주문자 우편번호
	private String custAddr1; // 주문자 주소1
	private String custAddr2; // 주문자 주소2
	private String deliNm; // 수취인명
	private String deliTel; // 수취인전화번호
	private String deliHp; // 수취인핸드폰
	private String deliZipcode; // 배송지우편번호
	private String deliAddr1; // 배송지주소1
	private String deliAddr2; // 배송지주소2
	private String payGb; // 결제구분
	private double orderAmt; // 총결제가
	private double totalGoodsPrice; // 총상품가
	private double totalDeliveryCharge; // 총배송비
	private double totalDiscountPrice; // 총 할인가
	private double totalUseMileage; // 마일리지 사용금액
	private List<Order> orders;

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Order {

		public Order(HashMap<String, Object> map) {
			this.orderId = (String) map.get("orderId");
			this.orderSeq = (String) map.get("orderSeq");
			this.orderKey = (String) map.get("orderKey");
			this.channelOrderNo = (String) map.get("channelOrderNo");
			this.channelOrderSeq = (String) map.get("channelOrderSeq");
			this.statusCd = (String) map.get("statusCd");
			this.assortId = (String) map.get("assortId");
			this.itemId = (String) map.get("itemId");
			this.goodsNm = (String) map.get("goodsNm");
			this.optionInfo = (String) map.get("optionInfo");
			this.optionNm1 = (String) map.get("optionNm1");
			this.optionNm2 = (String) map.get("optionNm2");
			this.optionNm3 = (String) map.get("optionNm3");
			this.qty = Long.valueOf((int) map.get("qty"));
			this.salePrice = ((BigDecimal) map.get("salePrice")).doubleValue();
			this.deliveryInfo = (String) map.get("deliveryInfo");
			this.scmType = (String) map.get("scmType");

			this.deliPrice = ((BigDecimal) map.get("deliPrice")).doubleValue();
			this.deliMethod = (String) map.get("deliMethod");
			this.listImageData = (String) map.get("listImageData");

		}

		private String orderId; // 주문번호
		private String orderSeq; // 주문순번
		private String orderKey; // 주문키
		private String channelOrderNo; // 제휴주문번호
		private String channelOrderSeq; // 제휴주문순번
		private String statusCd; // 주문상태
		private String assortId; // 품목코드
		private String itemId; // 상품코드
		private String goodsNm; // 상품명
		private String optionInfo; // 제휴옵션정보
		private String optionNm1; // 옵션1
		private String optionNm2; // 옵션2
		private String optionNm3; // 옵션3
		private Long qty; // 수량
		private double salePrice; // 판매가
		private String deliveryInfo; // 제휴배송정보
		private String scmType; // 공급사구분

		private double deliPrice; // 배송비
		private String deliMethod; // 배송구분
		private String listImageData; // 이미지

	}

}
