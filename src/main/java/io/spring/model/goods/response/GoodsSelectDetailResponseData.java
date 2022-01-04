package io.spring.model.goods.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.infrastructure.util.PropertyUtil;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Itvari;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@PropertySource("classpath:application.properties")
public class GoodsSelectDetailResponseData {
    public GoodsSelectDetailResponseData(){

    }
    public GoodsSelectDetailResponseData(Itasrt itasrt){
        this.assortId = itasrt.getAssortId();
        this.assortNm = itasrt.getAssortNm();
        this.assortGb = itasrt.getAssortGb();
        this.assortColor = itasrt.getAssortColor();
        this.brandId = itasrt.getBrandId();
        this.origin = itasrt.getOrigin();
        this.manufactureNm = itasrt.getManufactureNm();
        this.assortModel = itasrt.getAssortModel();
        this.optionGbName = itasrt.getOptionGbName();
        this.taxGb = itasrt.getTaxGb();
        this.assortState = itasrt.getAssortState();
        this.shortageYn = itasrt.getShortageYn();
        this.localPrice = itasrt.getLocalPrice() + "";
        this.localSale = itasrt.getLocalSale() + "";
        this.localDeliFee = itasrt.getLocalDeliFee() + "";
        this.margin = itasrt.getMargin() + "";
        this.mdRrp = itasrt.getMdRrp() + "";
        this.mdYear = itasrt.getMdYear();
        this.mdVatrate = itasrt.getMdVatrate() + "";
        this.mdDiscountRate = itasrt.getMdDiscountRate() + "";
        this.mdGoodsVatrate = itasrt.getMdGoodsVatrate() + "";
        this.mdMargin = itasrt.getMdMargin() + "";
        this.buyWhere = itasrt.getBuyWhere();
        this.buySupplyDiscount = itasrt.getBuySupplyDiscount() + "";
        this.buyExchangeRate = itasrt.getBuyExchangeRate() + "";
        this.buyRrpIncrement = itasrt.getBuyRrpIncrement() + "";
        

        this.sellStaDt= Utilities.removeTAndTransToStr(itasrt.getSellStaDt());
        this.sellEndDt=Utilities.removeTAndTransToStr(itasrt.getSellEndDt());
        
        
        this.asWidth=itasrt.getAsWidth() + "";
        this.asLength=itasrt.getAsLength() + "";
        this.asHeight=itasrt.getAsHeight() + "";
        this.weight=itasrt.getWeight() + "";
        this.deliPrice = itasrt.getDeliPrice() + "";
        
		this.buyTax = itasrt.getBuyTax();
		this.mdTax = itasrt.getMdTax();
		this.vendorId = itasrt.getVendorId();

//		this.brandNm = (itasrt.getIfBrand() != null ? itasrt.getIfBrand().getBrandNm() : ""); 바깥에서 set
		this.vendorNm = (itasrt.getVendorId() != null && !itasrt.getVendorId().trim().equals("")? itasrt.getCmvdmr().getVdNm() : null);

		this.optionUseYn = itasrt.getOptionUseYn();
		
		this.dispCategoryId = itasrt.getDispCategoryId();	

		// this.brandNm = itasr

//        this.regDt = itasrt.getRegDt();
//        this.updDt = itasrt.getUpdDt();
    }

    
	// itasrt, itvari, itasrd 怨듯넻
    private String assortId;
//    @CreationTimestamp
//    private Date regDt;
    private String regId;
    private String updId;
//    @UpdateTimestamp
//    private Date updDt;

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
//    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private String sellStaDt; // 판매기간 - 시작
//    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private String sellEndDt; // 판매기간 - 끝
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
    private String vendorNm; // 구매처명
    private String vendorId; // 구매처 코드
    private String buyRrpIncrement; // RRP 인상률
    private String buySupplyDiscount; //
    private String buyTax; // TAX(구매)
    private String mdMargin; // 정기마진율
    private String buyExchangeRate; // 적용환율


    private String optionGbName;
    private String assortGb;
    private String siteGb;
    private String asVendorId;
    private String localDeliFee;
    private String assortColor;

    private String mdOfflinePrice;
    private String mdOnlinePrice;
    private String buyWhere;
    private String sizeType;
    private String optionUseYn;

    private String brandNm;

	private LinkedList<String> categoryValue;

    // itasrd
	private List<GoodsSelectDetailResponseData.Description> description; // html (硫붾え �긽�꽭) - long memo, text (硫붾え 媛꾨왂)
																			// - short memo
    @Getter
    @Setter
    public static class Description{
        private String seq;
        private String ordDetCd;
        private String textHtmlGb;
        private String memo;
    }

    // ititmm
//    @SerializedName("items")
//    @Expose
    private List<GoodsSelectDetailResponseData.Items> items;

    // itvari
//    @SerializedName("attributes")
//    @Expose
    private List<GoodsSelectDetailResponseData.Attributes> attributes;

    // ititmm
    @Getter
    @Setter
    public static class Items{
        //		private String assortId;
        private String itemId;
		private String seq1;
		private String seq2;
		private String seq3;
		private String value1;
		private String value2;
		private String value3;
        private String addPrice;
		private String shortageYn;
		private String status1;
		private String status2;
		private String status3;
    }

	// image 愿��젴
    private List<UploadMainImage> uploadMainImage;
    private List<UploadAddImage> uploadAddImage;
    private List<String> deleteImage;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UploadMainImage{
        public UploadMainImage(Itaimg itaimg){

			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");

            this.uid = itaimg.getImageSeq() + "";
            this.name = itaimg.getImageName();
            this.imageGb = itaimg.getImageGb();
            this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();

        }
        private String uid;
        private String name;
        private String url;
        private String imageGb;
        private String status;
    }
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UploadAddImage{
        public UploadAddImage(Itaimg itaimg){
			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");

            this.uid = itaimg.getImageSeq() + "";
            this.name = itaimg.getImageName();
            this.imageGb = itaimg.getImageGb();
            this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();
        }
        private String uid;
        private String name;
        private String url;
        private String imageGb;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Attributes{
        public Attributes(Itvari itvari){
            this.seq = itvari.getSeq();
            this.value = itvari.getOptionNm();
            this.variationGb = itvari.getVariationGb();
        }
        private String seq;
        private String value;
        private String variationGb;
    }
}
