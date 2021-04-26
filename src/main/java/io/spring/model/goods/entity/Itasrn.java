package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItasrnId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "itasrn")
@IdClass(ItasrnId.class)
public class Itasrn {
    public Itasrn(GoodsRequestData goodsRequestData){
        this.historyGb = "01"; // default 값
        this.vendorId = "000001";
        
        this.assortId = goodsRequestData.getAssortId();
        this.localSale = goodsRequestData.getLocalSale();
        // 밑의 것들은 추후에...
//        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        this.effEndDt = new Date();
        this.effStaDt = new Date();//sDate.format(new Date());
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
    private String addDeliGb;
    private String bonusReserve;
    private String callDisLimit;
    private String cardFee;
    private String couponYn;
    private String delayRewardYn;
    private String deliCharge;
    private String deliInterval;
    private String deliMth;
    private String deliPrice;
    private String disGb;
    private String disRate;
    private String dispCategoryId;
    private String divideMth;
    private String drtDeliMargin;
    private String excluChargeYn;
    private String freeGiftYn;
    private String handlingCharge;
    private String handlingChargeYn;
    private String hsCode;
    private String leadTime;
    private String localDeliFee;
    private String localPrice;
    private String localSale;
    private String localTaxRt;
    private String margin;
    private String marginCd;
    private String payMthCd;
    private String payType;
    private String plFromDt;
    private String plGbn;
    private String plToDt;
    private String preorderYn;
    private String reserveGive;
    private String salePrice;
    private String shortageYn;
    private String standardPrice;
    private String taxDeliYn;
    private String taxGb;
    private String templateId;
    private String vendorTrGb;
    private String weight;

    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
    private String updId;
    private String regId;
}
