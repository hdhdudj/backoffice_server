package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItitmmId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ititmm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItitmmId.class)
public class Ititmm {

    public Ititmm(GoodsRequestData goodsRequestData, GoodsRequestData.Items items){
        this.assortId = goodsRequestData.getAssortId();

        this.shortYn = items.getShortYn();
        this.addPrice = items.getAddPrice();
    }
    @Id
    private String assortId;
    @Id
    private String itemId;
    private String itemNm;
    private String shortYn;
    private String minCnt;
    private String maxCnt;
    private String dayDeliCnt;
    private String totDeliCnt;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String setYn;
    private String regId;
    private String updId;
    @Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String regDt;
    @Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private String updDt;
    private String orderLmtYn;
    private String orderLmtCnt;
    private String addPrice;
}
