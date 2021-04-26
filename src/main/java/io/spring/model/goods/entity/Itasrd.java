package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItasrdId;
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
@Table(name = "itasrd")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItasrdId.class)
public class Itasrd {

    public Itasrd(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();

        this.long
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

    private String regId;
    private String updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
}
