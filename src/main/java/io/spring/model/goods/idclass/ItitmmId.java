package io.spring.model.goods.idclass;

import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmmId implements Serializable {
    public ItitmmId(String assortId, GoodsInsertRequestData.Items items){
        this.assortId = assortId;
    }
    private String assortId;
    private String itemId;
}
