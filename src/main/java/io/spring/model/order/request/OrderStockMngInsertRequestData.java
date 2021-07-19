package io.spring.model.order.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class OrderStockMngInsertRequestData {

	private String id;
	private String purchaseVendor;
	private String brand;
	private String modelNo;
	private String assortNm;
	private String optionInfo;
	private String textOptionInfo;
	private String qty;
	private String unitAmt;
	private String optionAmt;
	private String amt;
	private String discountRate;
	private String realAmt;
	private String deliMethod;
	private String realDeliMethod;
	private String orderNm;
	private String orderId;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone
	// = "Asia/Seoul")
	private String orderDt;
	private String orderMemo;
	private String stockNo;
	private String purchaseNo;
	private String pi;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone
	// = "Asia/Seoul")
	private String estimatedProductionDate;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone
	// = "Asia/Seoul")
	private String estimatedShipmentDate;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone
	// = "Asia/Seoul")
	private String estimatedArrivalDate;
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone
	// = "Asia/Seoul")
	private String expectedDeliveryDate;
	private String blNo;
	private String deliveryPeriod;
	private String statusCd;
	private String carrier;
	private String purchaseDt;
	private String memo;
	private String origin;
	private String googleDrive;

}
