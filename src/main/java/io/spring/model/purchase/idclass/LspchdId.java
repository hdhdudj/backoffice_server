package io.spring.model.purchase.idclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LspchdId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    public LspchdId(String purchaseNo, String purchaseSeq){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
    }
    private String purchaseNo;
    private String purchaseSeq;
}
