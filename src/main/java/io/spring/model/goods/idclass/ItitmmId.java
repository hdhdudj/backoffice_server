package io.spring.model.goods.idclass;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmmId implements Serializable {
    public ItitmmId(String assortId, GoodsRequestData.Items items){
        this.assortId = assortId;
    }
    private String assortId;
    private String itemId;
}
