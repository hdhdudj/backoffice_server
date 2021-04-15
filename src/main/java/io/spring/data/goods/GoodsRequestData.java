package io.spring.data.goods;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsRequestData {
	private long assortId;
	private String regDt;
	private String regId;
	private String updId;
	private String updDt;
	
	private String assortNm;
	private String assortColor;
	private String dispCategory;
	private String brandId;
	private String origin;
	private String manufactureNm;
	private String assortModel;
	private String taxGb;
	private String assortState;
	private String shortageYn;
	private String sellSta;
	private String sellEnd;
	private String localPrice;
	private String localSale;
	private String deliPrice;
	private String margin;
	private String vendorId;
	private String mdRrp;
	private String mdYear;
	private String mdTax;
	private String mdVatrate;
	private String mdDiscountRate;
	private String mdGoodsVatrate;
	private String buyWhere;
	private String buySupplyDiscount;
	private String buyRrpIncrement;
	private String buyTax;
	private String mdMargin;
	private String buyExchangeRate;
}
