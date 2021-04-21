package io.spring.model.goods;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.JsonObject;
import lombok.Data;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "itvari")
@Data
@IdClass(ItvariId.class)
public class Itvari {
    public Itvari(){

    }

    /**
     *
     * @param attribute
     * {
     *    "color": [
     *            "빨강",
     *            "파랑",
     *            "노랑"
     *             ]
     * }
     */
    public Itvari(GoodsRequestData goodsRequestData){
        this.optionGb = goodsRequestData.getOptionGb();
        this.optionNm = goodsRequestData.getOptionNm();
        this.seq = goodsRequestData.getSeq();
        this.assortId = goodsRequestData.getAssortId();
        this.imgYn = goodsRequestData.getImgYn();
        this.variationGb = goodsRequestData.getVariationGb();

        this.regId = "123"; // 추후 추가
        this.updId = "123"; // 추후 추가
    }

    @Id
    private long assortId;
    @Id
    private String seq;
    private String optionGb;
    private String imgYn;
    private String optionNm;
    private String variationGb;
    private String delYn;
    private String regId;
    private String updId;

    @Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String regDt;
    @Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String updDt;
}
