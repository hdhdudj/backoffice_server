package io.spring.model.goods.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.model.goods.entity.Itvari;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsInsertRequestData2 {
	//	public GoodsInsertRequestData(){}
	private Optional<String> code;
	private Optional<String> message;
	// itasrt, itvari, itasrd 공통
	private Optional<String> assortId;
	private Optional<String> regId;
	private Optional<String> updId;

	@NotNull(message = "userId는 필수 값입니다.")
	private Optional<String> userId;

	// 상품기본설정 화면
	private Optional<String> assortNm; // 상품명
	private Optional<String> assortModel; // 모델번호
	private Optional<String> taxGb; // 과세/면세
	private Optional<String> assortState; // 상품상태 : 진행중(01), 일시중지(02), 단품(03), 품절(04)
	private Optional<String> shortageYn; // 판매상태 : 진행중(01), 중지(02)
	private Optional<String> asLength; // 깊이
	private Optional<String> asHeight; // 높이
	private Optional<String> asWidth; // 너비
	private Optional<String> weight; // 무게
	private Optional<String> origin; // 원산지
	private Optional<String> brandId; // 브랜드 코드
	private Optional<String> dispCategoryId; // erp 카테고리 코드
	private Optional<String> manufactureNm; // 제조회사
	private Optional<String> localSale; // 판매가
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private Optional<LocalDateTime> sellStaDt; // 판매기간 - 시작
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private Optional<LocalDateTime> sellEndDt; // 판매기간 - 끝
	private Optional<String> deliPrice; // 매입가
	private Optional<String> localPrice; // 정가
	private Optional<String> margin; // 마진율

	// 이미지 설정 화면

	// 상품 가격 관리(MD팀) 화면
	private Optional<String> mdRrp; // RRP
	private Optional<String> mdYear; // 자료연도
	private Optional<String> mdTax; // TAX(자료)
	private Optional<String> mdVatrate; // 부가세율
	private Optional<String> mdDiscountRate; // 할인율
	private Optional<String> mdGoodsVatrate; // 상품마진율

	// 상품 가격 관리(구매팀) 화면
	private Optional<String> buyRrpIncrement; // RRP 인상률
	private Optional<String> buySupplyDiscount; //
	private Optional<String> buyTax; // TAX(구매)
	private Optional<String> mdMargin; // 정기마진율
	private Optional<String> buyExchangeRate; // 적용환율

	// 옵션 화면
	// 설명 화면
	private Optional<String> assortGb;
	private Optional<String> optionGbName;
	private Optional<String> siteGb;
	private Optional<String> asVendorId;
	private Optional<String> localDeliFee;
	private Optional<String> assortColor;
	private Optional<String> mdOfflinePrice;
	private Optional<String> mdOnlinePrice;
	private Optional<String> buyWhere;
	private Optional<String> sizeType;
	private Optional<String> vendorId;
	private Optional<String> optionUseYn;


	// itasrd
	private Optional<List<Description>> description; // html (메모 상세) - long memo, text (메모 간략) - short memo
	@Getter
	@Setter
	public static class Description{
		private Optional<String> seq;
		private Optional<String> ordDetCd;
		private Optional<String> textHtmlGb;
		private Optional<String> memo;
	}

	// ititmm
//	@SerializedName("items")
//	@Expose
	private Optional<List<Items>> items;

	// itvari
//	@SerializedName("attributes")
//	@Expose
	@JsonProperty("attributes")
	private Optional<List<Attributes>> attributes;

	// ititmm
	@Getter
	@Setter
	public static class Items{
		//		private Optional<String> assortId;
		private Optional<String> itemId;
		//		@Expose // object 중 해당 값이 null일 경우, json으로 만들 필드를 자동 생략
		private Optional<String> variationSeq1;
		private Optional<String> variationSeq2;
		private Optional<String> variationSeq3;
		private Optional<String> variationValue1;
		private Optional<String> variationValue2;
		private Optional<String> variationValue3;
		private Optional<String> addPrice;
		private Optional<String> shortYn;
	}
	@Getter
	@Setter
	public static class Attributes {
		public Attributes(){}
		public Attributes(Itvari itvari){
			this.seq = Optional.of(itvari.getSeq());
			this.value = Optional.of(itvari.getOptionNm());
			this.variationGb = Optional.of(itvari.getVariationGb());
		}
		private Optional<String> seq;
		private Optional<String> value;
		private Optional<String> variationGb;
	}

	// image 관련
	private Optional<List<UploadMainImage>> uploadMainImage;
	private Optional<List<UploadAddImage>> uploadAddImage;
	private Optional<List<Long>> deleteImage;
	@Getter
	@Setter
	public static class UploadMainImage{
		private Optional<Long> uid;
		private Optional<String> name;
		private Optional<String> url;
		private Optional<String> imageGb;
		private Optional<String> status;
	}
	@Getter
	@Setter
	public static class UploadAddImage{
		private Optional<Long> uid;
		private Optional<String> name;
		private Optional<String> url;
		private Optional<String> imageGb;
		private Optional<String> status;
	}

//	@Getter
//	@Setter
//	public static class SeqAndValue{
//		@SerializedName("seq")
//		@Expose
//		private Optional<String> seq;
//		@SerializedName("value")
//		@Expose
//		private Optional<String> value;
//	}

//	private Optional<String> optionGb;
//	private Optional<String> optionNm;
//	private Optional<String> seq;
//	private Optional<String> imgYn;
//	private Optional<String> delYn;
//	private Optional<String> variationGb;
//	private Optional<String> memo;
//	private Optional<String> textHtmlGb;
//	private Optional<String> memo2;
//	private Optional<String> attributes;
//	private Optional<String> items;
//	private Optional<String> dispCategory;
}