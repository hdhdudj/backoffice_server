package io.spring.model.goods;

import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private long assortId;
    @Id
    private int seq;

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
