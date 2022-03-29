package io.spring.model.goods.idclass;

import java.io.Serializable;

import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ItitmmId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;
    public ItitmmId(String assortId, GoodsInsertRequestData.Items items){
        this.assortId = assortId;
    }
    private String assortId;
    private String itemId;
}
