package io.spring.model.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

import io.spring.enums.DeliveryMethod;
import io.spring.infrastructure.util.Utilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문이동지시 조회 리스트 가져오는 DTO
 */
// 결제금액,총상품가,총배송비,총할인금액,총부가결제금액
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMasterListResponseData {
	private DeliveryMethod d;

	public OrderMasterListResponseData(HashMap<String, Object> m) {
		this.channelGb = (String) m.get("channelGb"); // 채널번호
		LocalDateTime dateTime = (LocalDateTime)m.get("orderDate");
		this.orderDate = m.get("orderDate") == null? null : Utilities.removeTAndTransToStr(dateTime).substring(0, 19); // 주문일자
		this.orderId = (String) m.get("orderId"); // 주문번호
		this.orderSeq = (String) m.get("orderSeq"); // 주문순번
		this.orderKey = Utilities.addDashInMiddle(this.orderId, this.orderSeq);
		this.channelOrderNo = (String) m.get("channelOrderNo"); // 고도몰 주문번호
		this.channelOrderSeq = (String) m.get("channelOrderSeq"); // 고도몰 주문순번
//		this.custId = m.get("custId") == null ? null : (int) m.get("custId");
		this.custNm = m.get("custNm") == null ? null : (String) m.get("custNm"); // 주문자
		this.custHp = m.get("custHp") == null ? null : (String) m.get("custHp"); // 주문자 휴대폰번호
		this.custTel = m.get("custTel") == null ? null : (String) m.get("custTel"); // 주문자 폰번호
		this.custAddr = m.get("custAddr") == null ? null : (String) m.get("custAddr");
		this.assortId = m.get("assortId") == null ? null : (String) m.get("assortId"); // 상품 주문번호
		this.itemId = m.get("itemId") == null ? null : (String) m.get("itemId");
		this.itemKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
		this.optionNm1 = m.get("optionNm1") == null ? null : (String) m.get("optionNm1"); // 옵션1
		this.optionNm2 = m.get("optionNm2") == null ? null : (String) m.get("optionNm2"); // 옵션2
		this.orderStatus = m.get("orderStatus") == null ? null : (String) m.get("orderStatus"); // 주문상태
		this.qty = m.get("qty") == null ? null : Long.parseLong(Integer.toString((Integer)m.get("qty"))); // 수량
		this.goodsPrice = m.get("goodsPrice") == null ? null : ((BigDecimal) m.get("goodsPrice")).doubleValue(); // 금액, 총상품가
		this.deliPrice = m.get("deliPrice") == null ? null : ((BigDecimal) m.get("deliPrice")).doubleValue(); // 배송비
		this.deliMethod = Utilities.convertFieldNameToEnum(DeliveryMethod.values() ,m.get("deliMethod") == null ? null : (String) m.get("deliMethod")); // 배송방법
		this.deliveryInfo = m.get("deliveryInfo") == null ? null : (String) m.get("deliveryInfo"); // 배송구분
		this.scmNo = m.get("scmNo") == null ? null : Integer.toString((Integer)m.get("scmNo")); // 공급사 번호
		this.deliNm = m.get("deliNm") == null ? null : (String) m.get("deliNm"); // 수령자
		this.deliHp = m.get("deliHp") == null ? null : (String) m.get("deliHp"); // 수령자 휴대폰번호
		this.deliTel = m.get("deliTel") == null ? null : (String) m.get("deliTel"); // 수령자 전화번호
		this.deliAddr = m.get("deliAddr") == null ? null : (String) m.get("deliAddr"); // 수령자 주소
		this.goodsNm = (String) m.get("goodsNm"); // 주문상품명
		this.totalGoodsPrice = ((BigDecimal) m.get("totalGoodsPrice")).doubleValue();
		this.totalDeliveryCharge = ((BigDecimal) m.get("totalDeliveryCharge")).doubleValue(); // 총배송비
		this.totalPrice = ((BigDecimal) m.get("totalPrice")).doubleValue();
		this.orderAmt = ((BigDecimal) m.get("orderAmt")).doubleValue(); // 결제금액

		this.payGb = (String) m.get("payGb"); // 결제방법
		this.payStatus = (String) m.get("payStatus");
		
		this.channelOrderNo = (String) m.get("channelOrderNo");

	}




	private String channelGb;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
	// timezone = "Asia/Seoul")
	private String orderDate;
    private String orderId;
    private String orderKey;
	private String channelOrderNo;
//	private int custId;
	private String custNm;
	private String custHp;
	private String custTel;
	private String goodsNm;
	private double totalGoodsPrice;
	private double totalDeliveryCharge;
	private double totalPrice;
	private double orderAmt;
	private String payGb;
	private String payStatus;

	// 21-12-06 추가
	private String orderSeq;
	private String custAddr;
	private String channelOrderSeq;
	private String assortId;
	private String itemId;
	private String itemKey;
	private String optionNm1;
	private String optionNm2;
	private String orderStatus;
	private Long qty;
	private Double goodsPrice;
	private Double deliPrice;
	private String deliMethod;
	private String deliveryInfo;
	private String scmNo;
	private String scmNm;
	private String deliNm;
	private String deliHp;
	private String deliTel;
	private String deliAddr;
//		private String purchaseCompleteDt;
	// private String makeCompleteDt;
	// private String shipmentDt;
	// private String estiArrvDt;
	// private String cancelDt;

}
