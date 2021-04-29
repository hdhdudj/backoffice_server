package io.spring.model.goods.entity;

import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.idclass.ItitmmId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ititmm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItitmmId.class)
public class Ititmm {

    public Ititmm(String assortId, GoodsInsertRequestData.Items items){
        this.assortId = assortId;

        this.shortYn = items.getShortYn();
        this.addPrice = items.getAddPrice();
    }
    @Id
    private String assortId;
    @Id
    private String itemId;
    private String itemNm;
    private String shortYn;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String addPrice;
    @Column(nullable = true)
    private Long regId;
    @Column(nullable = true)
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;

//    private String orderLmtYn;
//    private String orderLmtCnt;
//    private String minCnt;
//    private String maxCnt;
//    private String dayDeliCnt;
//    private String totDeliCnt;
//    private String setYn;
}
