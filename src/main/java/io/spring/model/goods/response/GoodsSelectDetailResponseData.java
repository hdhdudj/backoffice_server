package io.spring.model.goods.response;

import java.util.LinkedList;
import java.util.List;

import io.spring.infrastructure.util.PropertyUtil;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Itvari;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@PropertySource("classpath:application.properties")
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
        this.localPrice = itasrt.getLocalPrice() == null? null : itasrt.getLocalPrice() + "";
        this.localSale = itasrt.getLocalSale() == null? null : itasrt.getLocalSale()+ "";
        this.localDeliFee = itasrt.getLocalDeliFee() == null? null : itasrt.getLocalDeliFee()+ "";
        this.margin = itasrt.getMargin() == null? null : itasrt.getMargin()+ "";
        this.mdRrp = itasrt.getMdRrp() == null? null : itasrt.getMdRrp()+ "";
        this.mdYear = itasrt.getMdYear();
        this.mdVatrate = itasrt.getMdVatrate() == null? null : itasrt.getMdVatrate()+ "";
        this.mdDiscountRate = itasrt.getMdDiscountRate() == null? null : itasrt.getMdDiscountRate()+ "";
        this.mdGoodsVatrate = itasrt.getMdGoodsVatrate() == null? null : itasrt.getMdGoodsVatrate()+ "";
        this.mdMargin = itasrt.getMdMargin() == null? null : itasrt.getMdMargin()+ "";
        this.buyWhere = itasrt.getBuyWhere();
        this.buySupplyDiscount = itasrt.getBuySupplyDiscount() == null? null : itasrt.getBuySupplyDiscount()+ "";
        this.buyExchangeRate = itasrt.getBuyExchangeRate() == null? null : itasrt.getBuyExchangeRate()+ "";
        this.buyRrpIncrement = itasrt.getBuyRrpIncrement() == null? null : itasrt.getBuyRrpIncrement()+ "";
        

        this.sellStaDt= Utilities.removeTAndTransToStr(itasrt.getSellStaDt());
        this.sellEndDt=Utilities.removeTAndTransToStr(itasrt.getSellEndDt());

        this.deliPrice = itasrt.getDeliPrice() == null ? null : Float.toString(itasrt.getDeliPrice());
        this.asWidth = itasrt.getAsWidth() == null ? null : Float.toString(itasrt.getAsWidth());
        this.asLength = itasrt.getAsLength() == null ? null : Float.toString(itasrt.getAsLength());
        this.asHeight = itasrt.getAsHeight() == null ? null : Float.toString(itasrt.getAsHeight());
        this.weight = itasrt.getWeight() == null ? null : Float.toString(itasrt.getWeight());

		this.buyTax = itasrt.getBuyTax();
		this.mdTax = itasrt.getMdTax();
		this.vendorId = itasrt.getVendorId();
        System.out.println(itasrt.getVendorId());
//		this.brandNm = (itasrt.getIfBrand() != null ? itasrt.getIfBrand().getBrandNm() : ""); ???????????? set

		this.optionUseYn = itasrt.getOptionUseYn();
		
		this.dispCategoryId = itasrt.getDispCategoryId();	

		// this.brandNm = itasr

//        this.regDt = itasrt.getRegDt();
//        this.updDt = itasrt.getUpdDt();
    }

    
	// itasrt, itvari, itasrd ?????????
    private String assortId;
//    @CreationTimestamp
//    private Date regDt;
    private String regId;
    private String updId;
//    @UpdateTimestamp
//    private Date updDt;

    // ?????????????????? ??????
    private String assortNm; // ?????????
    private String assortModel; // ????????????
    private String taxGb; // ??????/??????
    private String assortState; // ???????????? : ?????????(01), ????????????(02), ??????(03), ??????(04)
    private String shortageYn; // ???????????? : ?????????(01), ??????(02)
    private String asLength; // ??????
    private String asHeight; // ??????
    private String asWidth; // ??????
    private String weight; // ??????
    private String origin; // ?????????
    private String brandId; // ????????? ??????
    private String dispCategoryId; // erp ???????????? ??????
    private String manufactureNm; // ????????????
    private String localSale; // ?????????
//    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private String sellStaDt; // ???????????? - ??????
//    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private String sellEndDt; // ???????????? - ???
    private String deliPrice; // ?????????
    private String localPrice; // ??????
    private String margin; // ?????????

    // ????????? ?????? ??????

    // ?????? ?????? ??????(MD???) ??????
    private String mdRrp; // RRP
    private String mdYear; // ????????????
    private String mdTax; // TAX(??????)
    private String mdVatrate; // ????????????
    private String mdDiscountRate; // ?????????
    private String mdGoodsVatrate; // ???????????????

    // ?????? ?????? ??????(?????????) ??????
    private String vendorNm; // ????????????
    private String vendorId; // ????????? ??????
    private String buyRrpIncrement; // RRP ?????????
    private String buySupplyDiscount; //
    private String buyTax; // TAX(??????)
    private String mdMargin; // ???????????????
    private String buyExchangeRate; // ????????????


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
	private List<GoodsSelectDetailResponseData.Description> description; // html (????????? ????????????) - long memo, text (????????? ?????????)
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

	// image ????????????
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
