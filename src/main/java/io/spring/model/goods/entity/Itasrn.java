package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "itasrn")
public class Itasrn {
    private final static Logger logger = LoggerFactory.getLogger(Itasrn.class);
    public Itasrn(Itasrn itasrn){
        this.historyGb = itasrn.getHistoryGb();
        this.vendorId = itasrn.getVendorId();
        this.assortId = itasrn.getAssortId();
        this.localSale = itasrn.getLocalSale();
        this.shortageYn = itasrn.getShortageYn();
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
    }
    public Itasrn(GoodsRequestData goodsRequestData){
        this.historyGb = "01"; // default 값
        this.vendorId = "000001";
        
        this.assortId = goodsRequestData.getAssortId();
        this.localSale = goodsRequestData.getLocalSale();
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String historyGb;
    private String vendorId;
    private String assortId;
    @CreationTimestamp
    private Date effStaDt;
//    @Id
//    private String seq;

    private Date effEndDt;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
    @Column(nullable = true)
    private Long updId;
    @Column(nullable = true)
    private Long regId;

    @Column(nullable = true)
    private Float localSale;
    private String shortageYn;
//    private String addDeliGb;
//    private String bonusReserve;
//    private String callDisLimit;
//    private String cardFee;
//    private String couponYn;
//    private String delayRewardYn;
//    private String deliCharge;
//    private String deliInterval;
//    private String deliMth;
//    private String deliPrice;
//    private String disGb;
//    private String disRate;
//    private String dispCategoryId;
//    private String divideMth;
//    private String drtDeliMargin;
//    private String excluChargeYn;
//    private String freeGiftYn;
//    private String handlingCharge;
//    private String handlingChargeYn;
//    private String hsCode;
//    private String leadTime;
//    private String localDeliFee;
//    private String localPrice;
//    private String localTaxRt;
//    private String margin;
//    private String marginCd;
//    private String payMthCd;
//    private String payType;
//    private String plFromDt;
//    private String plGbn;
//    private String plToDt;
//    private String preorderYn;
//    private String reserveGive;
//    private String salePrice;
//    private String standardPrice;
//    private String taxDeliYn;
//    private String taxGb;
//    private String templateId;
//    private String vendorTrGb;
//    private String weight;
}
