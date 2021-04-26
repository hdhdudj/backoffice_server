package io.spring.model.goods.idclass;

import io.spring.model.goods.GoodsRequestData;

import java.io.Serializable;

public class ItasrnId implements Serializable {
    private static final long serialVersionUID = 1L;

    public ItasrnId(GoodsRequestData goodsRequestData){
        this.historyGb = "01";
        this.vendorId = "000001";
        this.assortId = goodsRequestData.getAssortId();
        this.effEndDt = goodsRequestData.getSellEnd();
        this.effStaDt = goodsRequestData.getSellSta();
    }

    private String historyGb;
    private String vendorId;
    private String assortId;
    private String effEndDt;
    private String effStaDt;
}
