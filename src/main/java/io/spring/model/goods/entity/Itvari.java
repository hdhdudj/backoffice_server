package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItvariId;
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

    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
    private String regId;
    private String updId;
}
