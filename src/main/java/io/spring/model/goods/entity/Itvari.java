package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItvariId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
     * @param goodsRequestData
     * {
     *    "color": [
     *         {
     *           "seq": "001",
     *           "value": "빨강"
     *         },        
     *         {
     *           "seq": "002",
     *           "value": "파랑"
     *         },
     *         {
     *           "seq": "003",
     *           "value": "노랑"
     *         }
     *             ]
     * }
     */
    public Itvari(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();
        this.delYn = "02";
//        this.optionGb = goodsRequestData.getOptionGb();
//        this.imgYn = goodsRequestData.getImgYn();
//        this.optionNm = goodsRequestData.getOptionNm();
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
    @Column(nullable = true)
    private Long regId;
    @Column(nullable = true)
    private Long updId;
}
