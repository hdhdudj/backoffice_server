package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "itasrn")
public class Itasrn extends CommonProps {
    private final static Logger logger = LoggerFactory.getLogger(Itasrn.class);
    public Itasrn(Itasrn itasrn){
        this.historyGb = itasrn.getHistoryGb();
        this.ownerId = itasrn.getOwnerId();
        this.assortId = itasrn.getAssortId();
        this.localSale = itasrn.getLocalSale();
        this.shortageYn = itasrn.getShortageYn();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
    }
    public Itasrn(GoodsInsertRequestData goodsInsertRequestData){
        this.historyGb = StringFactory.getGbOne(); // default 값 (01)
        this.ownerId = Utilities.getStringNo(null,StringFactory.getStrOne(),6); // 000001 하드코딩
        this.assortId = goodsInsertRequestData.getAssortId();
        this.localSale = goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale());
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String historyGb;
    private String ownerId;
    private String assortId;
    @CreationTimestamp
    private Date effStaDt;
//    @Id
//    private String seq;

    private Date effEndDt;

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
