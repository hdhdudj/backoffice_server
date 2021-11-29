package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.ItvariId;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Table(name = "itvari")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItvariId.class)
public class Itvari extends CommonProps {

    /**
     *
     * @param goodsInsertRequestData
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
    public Itvari(GoodsInsertRequestData goodsInsertRequestData){
        this.assortId = goodsInsertRequestData.getAssortId();
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
    private String imgYn = StringFactory.getGbTwo(); // 02 하드코딩
    private String optionNm;
    private String variationGb;
    private String delYn;

    // itasrt 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;

//    // ititmm 연관 관계
//    @OneToMany(fetch = FetchType.LAZY, targetEntity = Ititmm.class)
//    @JoinColumns({
//            @JoinColumn(name = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//            @JoinColumn(name = "seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//    })
////    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//    private List<Ititmm> ititmm1;
//
//    // ititmm 연관 관계
//    @OneToMany(fetch = FetchType.LAZY, targetEntity = Ititmm.class)
//    @JoinColumns({
//            @JoinColumn(name = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//            @JoinColumn(name = "seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//    })
////    @JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//    private List<Ititmm> ititmm2;
}
