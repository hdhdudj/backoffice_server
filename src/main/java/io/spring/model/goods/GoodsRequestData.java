package io.spring.model.goods;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GoodsRequestData {
	// itasrt, itvari, itasrd 공통
	private long assortId;
	private String regDt;
	private String regId;
	private String updId;
	private String updDt;

	// itasrt
	private String assortNm;
	private String assortColor;
	private String brandId;
	private String origin;
	private String manufactureNm;
	private String assortModel;
	private String taxGb;

	// itasrd
	private String longDesc;
	private String shortDesc;

	// ititmm
	@SerializedName("items")
	@Expose
	private List<Items> items;

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

	@Data
	public static class Items{
		@SerializedName("color")
		@Expose
		private String color;
		@SerializedName("size")
		@Expose
		private String size;
		@SerializedName("addPrice")
		@Expose
		private Integer addPrice;
	}

	@Data
	public static class Attributes {
		@SerializedName("color")
		@Expose
		private List<String> color;
		@SerializedName("size")
		@Expose
		private List<String> size;
	}

//	@JsonProperty("attributes") @JsonSerialize(using = ToStringSerializer.class)
//	private String attributes;
//	@JsonProperty("items") @JsonSerialize(using = ToStringSerializer.class)
//	private String items;
//	private String dispCategory;
//	private String assortState;
//	private String shortageYn;
//	private String sellSta;
//	private String sellEnd;
//	private String localPrice;
//	private String localSale;
//	private String deliPrice;
//	private String margin;
//	private String vendorId;
//	private String mdRrp;
//	private String mdYear;
//	private String mdTax;
//	private String mdVatrate;
//	private String mdDiscountRate;
//	private String mdGoodsVatrate;
//	private String buyWhere;
//	private String buySupplyDiscount;
//	private String buyRrpIncrement;
//	private String buyTax;
//	private String mdMargin;
//	private String buyExchangeRate;
}
