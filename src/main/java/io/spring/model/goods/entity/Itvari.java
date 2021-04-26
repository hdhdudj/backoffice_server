package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItvariId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "itvari")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItvariId.class)
public class Itvari {

    /**
     *
     * @param attributes
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

//        this.regId = "123"; // 추후 추가
//        this.updId = "123"; // 추후 추가
    }

    @Id
    private String assortId;
    @Id
    private String seq;
    private String optionGb;
    private String imgYn;
    private String optionNm;
    private String variationGb;
    private String delYn;

    @Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String regDt;
    @Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
    private String updDt;
    private String regId;
    private String updId;
}
