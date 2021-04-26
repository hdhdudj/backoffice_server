package io.spring.model.goods.idclass;

import java.io.Serializable;

public class ItasrdId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    public ItasrdId(String assortId, String seq){
        this.assortId = assortId;
        this.seq = seq;
    }
    private String assortId;
    private String seq;
}
