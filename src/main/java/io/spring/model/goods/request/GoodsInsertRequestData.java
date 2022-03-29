package io.spring.model.goods.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.entity.TbGoodsOptionValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;

	// 상품기본설정 화면
	private String assortNm; // 상품명
	private String assortModel; // 모델번호
	private String taxGb; // 과세/면세
	private String assortState; // 상품상태 : 진행중(01), 일시중지(02), 단품(03), 품절(04)
	private String shortageYn; // 판매상태 : 진행중(01), 중지(02)
	private String asLength; // 깊이
	private String asHeight; // 높이
	private String asWidth; // 너비
	private String weight; // 무게
	private String origin; // 원산지
	private String brandId; // 브랜드 코드
	private String dispCategoryId; // erp 카테고리 코드
	private String manufactureNm; // 제조회사
	private String localSale; // 판매가
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private LocalDateTime sellStaDt; // 판매기간 - 시작
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private LocalDateTime sellEndDt; // 판매기간 - 끝
	private String deliPrice; // 매입가
	private String localPrice; // 정가
	private String margin; // 마진율

	// 이미지 설정 화면

	// 상품 가격 관리(MD팀) 화면
	private String mdRrp; // RRP
	private String mdYear; // 자료연도
	private String mdTax; // TAX(자료)
	private String mdVatrate; // 부가세율
	private String mdDiscountRate; // 할인율
	private String mdGoodsVatrate; // 상품마진율

	// 상품 가격 관리(구매팀) 화면
	private String buyRrpIncrement; // RRP 인상률
	private String buySupplyDiscount; //
	private String buyTax; // TAX(구매)
	private String mdMargin; // 정기마진율
	private String buyExchangeRate; // 적용환율

	// 옵션 화면
	// 설명 화면
	private String assortGb;
	private String optionGbName;
	private String siteGb;
	private String asVendorId;
	private String localDeliFee;
	private String assortColor;
	private String mdOfflinePrice;
	private String mdOnlinePrice;
	private String buyWhere;
	private String sizeType;
	private String vendorId;
	private String optionUseYn;
	private String goodsDescription;
	private String shortDescription;


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

	private List<AddInfo> addInfos;

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
		List<itemSupplier> itemSupplier;
	}

	@Getter
	@Setter
	public static class itemSupplier {
		private Long sno; // key
		private String itemId;
		private String supplierId;
		private float salePrice;
		private Long stockCnt;
		private String saleYn;


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

		public Attributes(TbGoodsOptionValue tgov) {
			this.seq = tgov.getSeq();
			this.value = tgov.getOptionNm();
			this.variationGb = tgov.getVariationGb();
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
		private Long sno;
	}
	@Getter
	@Setter
	public static class UploadAddImage{
		private Long uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
	}

	@Getter
	@Setter
	public static class AddInfo {
		private Long sno;
		private String infoTitle;
		private String infoValue;

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
