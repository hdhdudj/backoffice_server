package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItasrnId;
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
@IdClass(ItasrnId.class)
public class Itasrn {
    private final static Logger logger = LoggerFactory.getLogger(Itasrn.class);
    public Itasrn(GoodsRequestData goodsRequestData){
        this.historyGb = "01"; // default 값
        this.vendorId = "000001";
        
        this.assortId = goodsRequestData.getAssortId();
        this.localSale = goodsRequestData.getLocalSale();
        // 밑의 것들은 추후에...
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.effStaDt = new Date();// 오늘날짜
    }

    @Id
    private String historyGb;
    @Id
    private String vendorId;
    @Id
    private String assortId;
    @Id
    private Date effEndDt;
    @Id
    private Date effStaDt;
    @Column(nullable = true)
    private float localSale;
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

    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
    @Column(nullable = true)
    private Long updId;
    @Column(nullable = true)
    private Long regId;
}
