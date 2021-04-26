package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItasrnId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public Itasrn(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();
        this.localSale = goodsRequestData.getLocalSale();
        // 밑의 것들은 추후에
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        this.effEndDt = sDate.format(new Date());
        this.effStaDt = sDate.format(new Date());
        this.historyGb = "";

        this.vendorId = "";
    }
    @Id
    private String historyGb;
    @Id
    private String vendorId;
    @Id
    private String assortId;
    @Id
    private String effEndDt;
    @Id
    private String effStaDt;
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

    @Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String regDt;
    @Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private String updDt;
    private String updId;
    private String regId;
}
