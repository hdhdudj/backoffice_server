package io.spring.model.goods.idclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmmId implements Serializable {
    private static final long serialVersionUID = 1L;
    public ItitmmId(String assortId, String itemId){
        this.assortId = assortId;
        this.itemId = itemId;
    }
    private String assortId;
    private String itemId;
}
