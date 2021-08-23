package io.spring.model.goods.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.spring.infrastructure.util.PropertyUtil;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.PropertySource;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@PropertySource("classpath:application.properties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsSelectDetailResponseData {
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
        this.localPrice = itasrt.getLocalPrice();
        this.localSale = itasrt.getLocalSale();
        this.localDeliFee = itasrt.getLocalDeliFee();
        this.margin = itasrt.getMargin();
        this.mdRrp = itasrt.getMdRrp();
        this.mdYear = itasrt.getMdYear();
        this.mdVatrate = itasrt.getMdVatrate();
        this.mdDiscountRate = itasrt.getMdDiscountRate();
        this.mdGoodsVatrate = itasrt.getMdGoodsVatrate();
        this.buyWhere = itasrt.getBuyWhere();
        this.buySupplyDiscount = itasrt.getBuySupplyDiscount();
        this.buyExchangeRate = itasrt.getBuyExchangeRate();
        this.buyRrpIncrement = itasrt.getBuyRrpIncrement();
        

        this.sellStaDt=itasrt.getSellStaDt();
        this.sellEndDt=itasrt.getSellEndDt();
        
        
        this.asWidth=itasrt.getAsWidth();
        this.asLength=itasrt.getAsLength();
        this.asHeight=itasrt.getAsHeight();
        this.weight=itasrt.getWeight();
        this.deliPrice = itasrt.getDeliPrice();
        
		this.buyTax = itasrt.getBuyTax();
		this.mdTax = itasrt.getMdTax();
		this.vendorId = itasrt.getVendorId();

//		this.brandNm = (itasrt.getIfBrand() != null ? itasrt.getIfBrand().getBrandNm() : ""); 바깥에서 set
		this.vendorNm = (itasrt.getCmvdmr() != null ? itasrt.getCmvdmr().getVdNm() : "");

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
	private String vendorId;
	private String optionUseYn;

	private String brandNm;
	private String vendorNm;

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
    @SerializedName("items")
    @Expose
    private List<GoodsSelectDetailResponseData.Items> items;

    // itvari
    @SerializedName("attributes")
    @Expose
    private List<GoodsSelectDetailResponseData.Attributes> attributes;

    // ititmm
    @Getter
    @Setter
    public static class Items{
        //		private String assortId;
        private String itemId;
		private String seq1;
		private String seq2;
		private String value1;
		private String value2;
        private Float addPrice;
		private String shortageYn;
		private String status1;
		private String status2;
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

            this.uid = itaimg.getImageSeq();
            this.name = itaimg.getImageName();
            this.imageGb = itaimg.getImageGb();
            this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();

        }
        private Long uid;
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

            this.uid = itaimg.getImageSeq();
            this.name = itaimg.getImageName();
            this.imageGb = itaimg.getImageGb();
            this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();
        }
        private Long uid;
        private String name;
        private String url;
        private String imageGb;
        private String status;
    }

    @Getter
    @Setter
    public static class Attributes {
        private String seq;
        private String value;
        private String variationGb;
    }
}
