package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItasrdId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "itasrd")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItasrdId.class)
public class Itasrd {

    public Itasrd(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();
//        this.ordDetCd = "01";
//        this.memo = goodsRequestData.get
        this.delYn = "02";
//        this.textHtmlGb = goodsRequestData.getTextHtmlGb();
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

    @Column(nullable = true)
    private Long regId;
    @Column(nullable = true)
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
}
