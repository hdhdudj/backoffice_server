package io.spring.model.order.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.order.request.OrderStockMngInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_stock")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class OrderStock extends CommonProps {

	public OrderStock(OrderStockMngInsertRequestData o) {
		this.purchaseVendor = o.getPurchaseVendor();
		this.brand = o.getBrand();
		this.modelNo = o.getModelNo();
		this.assortNm = o.getAssortNm();
		this.optionInfo = o.getOptionInfo();
		this.textOptionInfo = o.getTextOptionInfo();

		this.qty = o.getQty();
		this.unitAmt = o.getUnitAmt();
		this.optionAmt = o.getOptionAmt();
		this.amt = o.getAmt();
		this.discountRate = o.getDiscountRate();
		this.realAmt = o.getRealAmt();
		/*
		 * if (o.getQty().length() > 0) { this.qty = Long.parseLong(o.getQty()); } if
		 * (o.getUnitAmt().length() > 0) { this.unitAmt =
		 * Long.parseLong(o.getUnitAmt()); } if (o.getQty().length() > 0) {
		 * this.optionAmt = Long.parseLong(o.getOptionAmt()); } if (o.getQty().length()
		 * > 0) { this.amt = Long.parseLong(o.getAmt()); } if (o.getQty().length() > 0)
		 * { this.discountRate = Long.parseLong(o.getDiscountRate()); } if
		 * (o.getQty().length() > 0) { this.realAmt = Long.parseLong(o.getRealAmt()); }
		 */
		this.deliMethod = o.getDeliMethod();
		this.realDeliMethod = o.getRealDeliMethod();
		this.orderNm = o.getOrderNm();
		this.orderId = o.getOrderId();
		this.orderDt = o.getOrderDt();
		this.orderMemo = o.getOrderMemo();
		this.stockNo = o.getStockNo();
		this.purchaseNo = o.getPurchaseNo();
		this.pi = o.getPi();
		this.estimatedProductionDate = o.getEstimatedProductionDate();
		this.estimatedShipmentDate = o.getEstimatedShipmentDate();
		this.estimatedArrivalDate = o.getEstimatedArrivalDate();
		this.expectedDeliveryDate = o.getExpectedDeliveryDate();
		this.blNo = o.getBlNo();
		this.deliveryPeriod = o.getDeliveryPeriod();
		this.statusCd = o.getStatusCd();
		this.carrier = o.getCarrier();
		this.purchaseDt = o.getPurchaseDt();
		this.memo = o.getMemo();
		this.origin = o.getOrigin();
		this.googleDrive = o.getGoogleDrive();

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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
