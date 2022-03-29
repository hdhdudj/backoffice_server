package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.ItasrdId;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Table(name = "itasrd")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItasrdId.class)
@BatchSize(size = 5)
public class Itasrd extends CommonProps {

    public Itasrd(GoodsInsertRequestData goodsInsertRequestData, GoodsInsertRequestData.Description description){
        this.assortId = goodsInsertRequestData.getAssortId();
        this.ordDetCd = description.getOrdDetCd();
        this.memo = description.getMemo();
        this.textHtmlGb = description.getTextHtmlGb();
        this.delYn = StringFactory.getGbTwo();
//        this.regId = "123"; // 추후 추가
//        this.updId = "123"; // 추후 추가
    }

    @Id
    private String assortId;
    @Id
    private String seq;

    private String ordDetCd;
    private String memo;
    private String delYn;
    private String textHtmlGb;
    private String memo2;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;
}
