package io.spring.model.goods.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.model.goods.entity.Itvari;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
public class GoodsInsertRequestData {
//	public GoodsInsertRequestData(){}
	private String code;
	private String message;
	// itasrt, itvari, itasrd 공통
	private String assortId;
	private String regId;
	private String updId;

	// itasrt
	private String assortNm;
	private String assortModel;
	private String optionGbName;
	private String margin;
	private String taxGb;
	private String assortGb;
	private String assortState;
	private String asWidth;
	private String asLength;
	private String asHeight;
	private String weight;
	private String origin;
	private String shortageYn; // itasrn에도
	private String brandId;
	private String dispCategoryId;
	private String siteGb;
	private String asVendorId;
	private String manufactureNm;
	private String deliPrice;
	private String localPrice;
	private String localDeliFee;
	private String localSale; // itasrn에도 들어감
	private String assortColor;
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private LocalDateTime sellStaDt;
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private LocalDateTime sellEndDt;
	private String mdRrp;
	private String mdTax;
	private String mdYear;
	private String mdMargin;
	private String mdVatrate;
	private String mdOfflinePrice;
	private String mdOnlinePrice;
	private String mdGoodsVatrate;
	private String buyWhere;
	private String buyTax;
	private String buySupplyDiscount;
	private String buyRrpIncrement;
	private String buyExchangeRate;
	private String sizeType;
	private String mdDiscountRate;
	private String vendorId;
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
//	@SerializedName("items")
//	@Expose
	private List<Items> items;

	// itvari
//	@SerializedName("attributes")
//	@Expose
	@JsonProperty("attributes")
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
		private String variationSeq3;
		private String variationValue1;
		private String variationValue2;
		private String variationValue3;
		private String addPrice;
		private String shortYn;
	}
	@Getter
	@Setter
	public static class Attributes {
		public Attributes(){}
		public Attributes(Itvari itvari){
			this.seq = itvari.getSeq();
			this.value = itvari.getOptionNm();
			this.variationGb = itvari.getVariationGb();
		}
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
