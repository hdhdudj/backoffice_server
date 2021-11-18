package io.spring.model.goods.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
public class GoodsInsertRequestData {
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
	private String assortModel;
	private String optionGbName;
	private Float margin;
	private String taxGb;
	private String assortGb;
	private String assortState;
	private Float asWidth;
	private Float asLength;
	private Float asHeight;
	private Float weight;
	private String origin;
	private String shortageYn; // itasrn에도
	private String brandId;
	private String dispCategoryId;
	private String siteGb;
	private String asVendorId;
	private String manufactureNm;
	private Float deliPrice;
	private Float localPrice;
	private Float localDeliFee; 
	private Float localSale; // itasrn에도 들어감
	private String assortColor;
	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
	private Date sellStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Seoul")
	private Date sellEndDt;
	private Float mdRrp;
	private String mdTax;
	private String mdYear;
	private Float mdMargin;
	private Float mdVatrate;
	private Float mdOfflinePrice;
	private Float mdOnlinePrice;
	private Float mdGoodsVatrate;
	private String buyWhere;
	private String buyTax;
	private Float buySupplyDiscount;
	private Float buyRrpIncrement;
	private Float buyExchangeRate;
	private String sizeType;
	private Float mdDiscountRate;
	private String purchaseVendorId;
	private String optionUseYn;


	// itasrd
	private List<Description> description; // html (메모 상세) - long memo, text (메모 간략) - short memo
	@Getter
	@Setter
	public static class Description{
		private String seq;
		private String ordDetCd;
		private String textHtmlGb;
		private String memo;
	}

	// ititmm
	@SerializedName("items")
	@Expose
	private List<Items> items;

	// itvari
	@SerializedName("attributes")
	@Expose
	private List<Attributes> attributes;

	// ititmm
	@Getter
	@Setter
	public static class Items{
//		private String assortId;
		private String itemId;
//		@Expose // object 중 해당 값이 null일 경우, json으로 만들 필드를 자동 생략
		private String variationSeq1;
		private String variationSeq2;
		private String variationValue1;
		private String variationValue2;
		private Float addPrice;
		private String shortYn;
	}
	@Getter
	@Setter
	public static class Attributes {
		private String seq;
		private String value;
		private String variationGb;
	}

	// image 관련
	private List<UploadMainImage> uploadMainImage;
	private List<UploadAddImage> uploadAddImage;
	private List<Long> deleteImage;
	@Getter
	@Setter
	public static class UploadMainImage{
		private Long uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
	}
	@Getter
	@Setter
	public static class UploadAddImage{
		private Long uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
	}

//	@Getter
//	@Setter
//	public static class SeqAndValue{
//		@SerializedName("seq")
//		@Expose
//		private String seq;
//		@SerializedName("value")
//		@Expose
//		private String value;
//	}

//	private String optionGb;
//	private String optionNm;
//	private String seq;
//	private String imgYn;
//	private String delYn;
//	private String variationGb;
//	private String memo;
//	private String textHtmlGb;
//	private String memo2;
//	private String attributes;
//	private String items;
//	private String dispCategory;
}
