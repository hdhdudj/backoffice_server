package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItitmdId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItitmdId.class)
public class Ititmd {
    public Ititmd(GoodsRequestData goodsRequestData, Ititmm ititmm){
        this.assortId = ititmm.getAssortId();
        this.itemId = ititmm.getItemId();
        this.effStaDt = goodsRequestData.getSellSta();
        this.effEndDt = goodsRequestData.getSellEnd();
        this.shortYn = ititmm.getShortYn();
    }
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    private String effEndDt;
    @Id
    private String effStaDt;
    private String shortYn;

    private String regId;
    private String updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
}
