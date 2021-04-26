package io.spring.model.goods;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GoodsRequestData {
	private String code;
	private String message;
	// itasrt, itvari, itasrd 공통
	private String assortId;
	private Date regDt;
	private String regId;
	private String updId;
	private Date updDt;

	// itasrt
	private String assortNm;
	private String assortColor;
	private String brandId;
	private String origin;
	private String manufactureNm;
	private String assortModel;
	private String taxGb;
	private String assortState;
	private String shortageYn;
	private String localPrice;
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

	//itasrn
	private String localSale;

	// itasrd
	private String longDesc; // html (메모 상세) - long memo
	private String shortDesc; // text (메모 간략) - short memo

	// ititmm
	@SerializedName("items")
	@Expose
	private List<Items> items;

	// ititmd
	private Date sellSta;
	private Date sellEnd;

	// itvari
	private String optionGb;
	private String optionNm;
	private String seq;
	private String imgYn;
	private String delYn;
	private String variationGb;
	@SerializedName("attributes")
	@Expose
	private List<Attributes> attributes;

	@Getter
	@Setter
	public static class Items{
		@SerializedName("color")
		@Expose
		private String color;
		@SerializedName("size")
		@Expose
		private String size;
		@SerializedName("addPrice")
		@Expose // object 중 해당 값이 null일 경우, json으로 만들 필드를 자동 생략
		private String addPrice;
		private String optionNm;
		private String assortId;
		private String shortYn;
	}

	@Getter
	@Setter
	public static class Attributes {
		@SerializedName("color")
		@Expose
		private List<SeqAndValue> color;
		@SerializedName("size")
		@Expose
		private List<SeqAndValue> size;
	}

	@Getter
	@Setter
	public static class SeqAndValue{
		@SerializedName("seq")
		@Expose
		private String seq;
		@SerializedName("value")
		@Expose
		private String value;
	}
//	private String attributes;
//	private String items;
//	private String dispCategory;
}
