package io.spring.model.goods.entity;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.ItitmmId;
import io.spring.model.goods.request.GoodsInsertRequestData;
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
public class Ititmm extends CommonProps {

    public Ititmm(String assortId, GoodsInsertRequestData.Items items){
        this.assortId = assortId;
        this.shortYn = items.getShortYn();
        this.addPrice = items.getAddPrice();
    }
    @Id
    private String assortId;
    @Id
    private String itemId;
    private String itemNm;
    private String shortYn;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String addPrice;

    // itasrt 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;

    // itvari 연관 관계 (일단 단방향) - 색상
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Itvari.class)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "variationSeq1", referencedColumnName="seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Itvari itvari1;

    // itvari 연관 관계 (일단 단방향) - 사이즈
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Itvari.class)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "variationSeq2", referencedColumnName="seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Itvari itvari2;

//    private String orderLmtYn;
//    private String orderLmtCnt;
//    private String minCnt;
//    private String maxCnt;
//    private String dayDeliCnt;
//    private String totDeliCnt;
//    private String setYn;
}
