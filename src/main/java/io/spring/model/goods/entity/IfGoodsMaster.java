package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.IfGoodsMasterId;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "if_goods_master")
@Getter
@Setter
@IdClass(IfGoodsMasterId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = "uploadStatus")
public class IfGoodsMaster extends CommonProps implements Cloneable {
    @Override
    public IfGoodsMaster clone() {
        try {
            return (IfGoodsMaster) super.clone();
        } catch (CloneNotSupportedException e) {
            // Cloneable을 구현했기 때문에 이 블록이 실행되는 일은 없다.
            return null;
        }
    }

    public IfGoodsMaster(Itasrt itasrt, Itasrn itasrn, Itasrd itasrd){
        // itasrt
        this.channelGb = StringFactory.getGbOne(); // 01 하드코딩
        this.goodsNm = itasrt.getAssortNm();
        this.goodsDisplayFl = itasrt.getAssortState();
        this.sizeType = itasrt.getAssortGb();
        this.goodsSellFl = itasrt.getShortageYn();
        this.cateCd = itasrt.getDispCategoryId();
        this.goodsColor = itasrt.getAssortColor();
        this.commission = itasrt.getMargin();
        this.brandCd = itasrt.getBrandId();
        this.makerNm = itasrt.getManufactureNm();
        this.goodsModelNo = itasrt.getAssortModel();
        this.taxFreeFl = itasrt.getTaxGb();
        this.salesStartYmd = itasrt.getSellStaDt();
        this.salesEndYmd = itasrt.getSellEndDt();
        this.goodsPrice = itasrt.getLocalSale();
        this.fixedPrice = itasrt.getLocalPrice();
        this.costPrice = itasrt.getDeliPrice();
        this.optionName = itasrt.getOptionGbName();
        this.optionFl = itasrt.getOptionUseYn();
        this.mdRrp = itasrt.getMdRrp();
        this.mdTax = itasrt.getMdTax();
        this.mdYear = itasrt.getMdYear();
        this.mdMargin = itasrt.getMdMargin();
        this.mdVatrate = itasrt.getMdVatrate();
        this.mdOfflinePrice = itasrt.getMdOfflinePrice();
        this.mdOnlinePrice = itasrt.getMdOnlinePrice();
        this.mdGoodsVatrate = itasrt.getMdGoodsVatrate();
        this.buyWhere = itasrt.getBuyWhere();
        this.buySupplyDiscount = itasrt.getBuySupplyDiscount();
        this.buyRrpIncrement = itasrt.getBuyRrpIncrement();
        this.buyExchangeRate = itasrt.getBuyExchangeRate();
        this.width = itasrt.getAsWidth();
        this.height = itasrt.getAsHeight();
        this.depth = itasrt.getAsLength();
        this.goodsWeight = itasrt.getWeight();
        // itasrn
        this.goodsSellFl = itasrn.getShortageYn();
        this.goodsPrice = itasrn.getLocalSale();
        // itasrd
    }
    
    // 테스트용
//    public IfGoodsMaster(){
//        this.channelGb = "1";
//        this.goodsNo = "1";
//        this.assortId = "1";
//        this.goodsNm = "1";
//        this.goodsNmDetail = "1";
//        this.goodsDisplayFl = "1";
//        this.goodsSellFl = "1";
//        this.cateCd = "1";
//        this.goodsColor = "1";
//        this.commission = 1f;
//        this.brandCd = "1";
//        this.makerNm = "1";
//        this.originNm = "1";
//        this.goodsModelNo = "1";
//        this.onlyAdultFl = "1";
//        this.taxFreeFl = "1";
//        this.stockFl = "1";
//        this.soldOutFl = "1";
//        this.salesStartYmd = new Date();
//        this.salesEndYmd = new Date();
//        this.goodsPrice = 1f;
//        this.fixedPrice = 1f;
//        this.costPrice = 1f;
//        this.optionMemo = "1";
//        this.shortDescription = "1";
//        this.goodsDescription = "1";
//        this.optionName = "1";
//        this.optionFl = "1";
//        this.mdRrp = 1f;
//        this.mdTax = "1";
//        this.mdYear = "1";
//        this.width = 1f;
//        this.height = 1f;
//        this.depth = 1f;
//        this.goodsWeight = 1f;
//        this.mdMargin = 1f;
//        this.mdVatrate = 1f;
//        this.mdOfflinePrice = 1f;
//        this.mdOnlinePrice = 1f;
//        this.mdGoodsVatrate = 1f;
//        this.buyWhere = "1";
//        this.buySupplyDiscount = 1f;
//        this.buyRrpIncrement = 1f;
//        this.buyExchangeRate = 1f;
//        this.sizeType = "1";
//        this.uploadStatus = "1";
//        this.mainImageData = "1";
//        this.listImageData = "1";
//        this.detailImageData = "1";
//        this.magnifyImageData = "1";
//        this.scmNo = 1l;
//    }
    
    @Id
    private String channelGb;
    @Id
    private String goodsNo;
    private String assortId;
    private String goodsNm;
    private String goodsNmDetail;
    private String goodsDisplayFl;
    private String goodsSellFl;
    private String cateCd;
    private String goodsColor;
    private Float commission;
    private String brandCd;
    private String makerNm;
    private String originNm;
    private String goodsModelNo;
    private String onlyAdultFl;
    private String taxFreeFl;
    private String stockFl;
    private String soldOutFl;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date salesStartYmd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date salesEndYmd;
    private Float goodsPrice;
    private Float fixedPrice;
    private Float costPrice;
    private String optionMemo;
    private String shortDescription;
    private String goodsDescription;
    private String optionName;
    private String optionFl;
    private Float mdRrp;
    private String mdTax;
    private String mdYear;
    private Float width;
    private Float height;
    private Float depth;
    private Float goodsWeight;
    private Float mdMargin;
    private Float mdVatrate;
    private Float mdOfflinePrice;
    private Float mdOnlinePrice;
    private Float mdGoodsVatrate;
    private String buyWhere;
    private Float buySupplyDiscount;
    private Float buyRrpIncrement;
    private Float buyExchangeRate;
    private String sizeType;
    private String uploadStatus = StringFactory.getGbOne(); // 01 하드코딩

    // 이미지 경로
    @JsonIgnore
    private String mainImageData;
    @JsonIgnore
    private String listImageData;
    @JsonIgnore
    private String detailImageData;
    @JsonIgnore
    private String magnifyImageData;

    // 21-10-05 추가
    private Long scmNo;
    // 21-10-12 추가
    private String purchaseNm;
}
