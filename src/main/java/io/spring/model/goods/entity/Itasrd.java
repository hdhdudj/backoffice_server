package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItvariId;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "itasrd")
@Data
@IdClass(ItvariId.class)
public class Itasrd {

    public Itasrd(){

    }

    public Itasrd(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();

        this.regId = "123"; // 추후 추가
        this.updId = "123"; // 추후 추가
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
    @Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String regDt;
    @Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updDt;
}
