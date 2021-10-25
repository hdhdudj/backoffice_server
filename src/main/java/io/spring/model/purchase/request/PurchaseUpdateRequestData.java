package io.spring.model.purchase.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 발주 -> 발주내역 화면에서 저장 버튼 눌렀을 때 reqeust DTO (발주내역 udpate 시)
 */
@Getter
@Setter
@ToString
public class PurchaseUpdateRequestData {
	private String purchaseNo; // 발주번호
	private String purchaseStatus; // 발주상태
	private String vendorId; // 구매처
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date purchaseDt; // 발주일자
	private String storageId; // 입고창고
	private String siteOrderNo; // 해외주문번호
	private String terms; // TERMS
	private String delivery; // DELIVERY
	private String payment; // PAYMENT
	private String carrier; // CARRIER
	private String userId; // userId

	private List<Items> items;

	@Getter
	@Setter
	@ToString
	public static class Items {
		private String assortId;
		private String itemId;
		private String itemGrade = "11";
		private String purchaseNo;
		private String purchaseSeq;
		private Long purchaseQty;
		private Float purchaseUnitAmt;
		private String purchaseStatus;
		private String orderId;
		private String orderSeq;
	}

}
