package io.spring.model.goods.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GoodsInsertRequestData {
	private String code;
	private String message;
	// itasrt, itvari, itasrd 怨듯넻
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
	private String shortageYn; // itasrn�뿉�룄
	private String brandId;
	private String dispCategoryId;
	private String siteGb;
	private String asVendorId;
	private String manufactureNm;
	private Float deliPrice;
	private Float localPrice;
	private Float localDeliFee;
	private Float localSale; // itasrn�뿉�룄 �뱾�뼱媛�
	private String assortColor;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date sellStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
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


	// itasrd
	private List<Description> description; // html (硫붾え �긽�꽭) - long memo, text (硫붾え 媛꾨왂) - short memo
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
//		@Expose // object 以� �빐�떦 媛믪씠 null�씪 寃쎌슦, json�쑝濡� 留뚮뱾 �븘�뱶瑜� �옄�룞 �깮�왂
		private String value;
		private String addPrice;
		private String shortYn;
	}

	// image 愿��젴
	private List<String> uploadImage;
	private List<String> deleteImage;

	@Getter
	@Setter
	public static class Attributes {
		private String seq;
		private String value;
		private String variationGb;
	}

	@Override
	public String toString() {
		return "GoodsInsertRequestData [code=" + code + ", message=" + message + ", assortId=" + assortId + ", regDt="
				+ regDt + ", regId=" + regId + ", updId=" + updId + ", updDt=" + updDt + ", assortNm=" + assortNm
				+ ", assortModel=" + assortModel + ", optionGbName=" + optionGbName + ", margin=" + margin + ", taxGb="
				+ taxGb + ", assortGb=" + assortGb + ", assortState=" + assortState + ", asWidth=" + asWidth
				+ ", asLength=" + asLength + ", asHeight=" + asHeight + ", weight=" + weight + ", origin=" + origin
				+ ", shortageYn=" + shortageYn + ", brandId=" + brandId + ", dispCategoryId=" + dispCategoryId
				+ ", siteGb=" + siteGb + ", asVendorId=" + asVendorId + ", manufactureNm=" + manufactureNm
				+ ", deliPrice=" + deliPrice + ", localPrice=" + localPrice + ", localDeliFee=" + localDeliFee
				+ ", localSale=" + localSale + ", assortColor=" + assortColor + ", sellStaDt=" + sellStaDt
				+ ", sellEndDt=" + sellEndDt + ", mdRrp=" + mdRrp + ", mdTax=" + mdTax + ", mdYear=" + mdYear
				+ ", mdMargin=" + mdMargin + ", mdVatrate=" + mdVatrate + ", mdOfflinePrice=" + mdOfflinePrice
				+ ", mdOnlinePrice=" + mdOnlinePrice + ", mdGoodsVatrate=" + mdGoodsVatrate + ", buyWhere=" + buyWhere
				+ ", buyTax=" + buyTax + ", buySupplyDiscount=" + buySupplyDiscount + ", buyRrpIncrement="
				+ buyRrpIncrement + ", buyExchangeRate=" + buyExchangeRate + ", sizeType=" + sizeType
				+ ", mdDiscountRate=" + mdDiscountRate + ", description=" + description + ", items=" + items
				+ ", attributes=" + attributes + ", uploadImage=" + uploadImage + ", deleteImage=" + deleteImage + "]";
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
