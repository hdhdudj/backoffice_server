package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.idclass.ItitmdId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItitmdId.class)
public class Ititmd {
    public Ititmd(GoodsRequestData goodsRequestData){

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
    private String regDt;
    private String updId;
    private String updDt;
}
