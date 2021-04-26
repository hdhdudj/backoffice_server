package io.spring.model.goods.idclass;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmdId implements Serializable {
    private static final long serialVersionUID = 1L;
    public ItitmdId(GoodsRequestData goodsRequestData, String itemId){
        this.assortId = goodsRequestData.getAssortId();
        this.itemId = itemId;
        this.effEndDt = goodsRequestData.getSellEnd();
        this.effStaDt = goodsRequestData.getSellSta();
    }
    private String assortId;
    private String itemId;
    private Date effEndDt;
    private Date effStaDt;
}
