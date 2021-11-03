package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.ItitmmId;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "ititmm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItitmmId.class)
public class Ititmm extends CommonProps implements Serializable {
    public Ititmm(String assortId, GoodsInsertRequestData.Items items){
        this.assortId = assortId;
        this.shortYn = items.getShortYn();
        this.addPrice = items.getAddPrice();
    }

    public Ititmm(GoodsInsertRequestData goodsInsertRequestData){
        this.assortId = goodsInsertRequestData.getAssortId();
        this.itemNm = goodsInsertRequestData.getAssortNm();
        this.shortYn = goodsInsertRequestData.getShortageYn();
    }

    @Override
    public String toString(){
        return "Ititmm : assortId="+this.assortId + ", itemId=" + this.itemId;
    }

    @Id
    private String assortId;
    @Id
    private String itemId;
    private String itemNm;
    private String shortYn;
    private Long minCnt = 0l; // 하드코딩
    private Long maxCnt = 0l; // 하드코딩
    private Long dayDeliCnt;
    private Long totDeliCnt;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String setYn = StringFactory.getGbTwo(); // 02 하드코딩
    private String orderLmtYn;
    private Long orderLmtCnt;
    private Float addPrice = 0f; // 하드코딩

    // itasrt 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;

    // itvari 연관 관계 (일단 단방향) - 색상
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Itvari.class)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "variationSeq1", referencedColumnName="seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Itvari itvari1;

    // itvari 연관 관계 (일단 단방향) - 사이즈
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Itvari.class)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "variationSeq2", referencedColumnName="seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Itvari itvari2;

    // ititmc 연관 관계 (일단 단방향) - 사이즈
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Ititmc.class)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName="itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Ititmc> ititmc;
}
