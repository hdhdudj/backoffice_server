package io.spring.model.purchase.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PurchaseItemResponseData {
	private String purchaseNo;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date purchaseDt;
	private String purchaseVendorId;
	private String purchaseVendorNm;
	private String purchaseRemark;
	private String storageId;
	private String storageNm;
	private String terms;
	private String delivery;
	private String payment;
	private String carrier;
	private String siteOrderNo;
	private String purchaseStatus;
	private String purchaseGb;
	private List<Items> items;

	@Getter
	@Setter
	public static class Items {
		private String purchaseNo;
		private String purchaseSeq;
		private String purchaseGb;

		private String orderId;
		private String orderSeq;
		private String assortId;
		private String itemId;
		private String assortNm;

		private String deliMethod;
		private String optionNm1;
		private String optionNm2;
		private String optionNm3;
		private String purchaseStatus;
		private Float mdRrp;
		private Float buySupplyDiscount;
		private Long purchaseQty;
		private Float purchaseUnitAmt;

		/*
		 * purchaseNo purchaseSeq purchaseGb orderId orderSeq assortId itemId assortNm
		 * deliMethod optionNm1 optionNm2 mdRrp buySupplyDiscount purchaseQty
		 * purchaseUnitAmt
		 */

	}
}
