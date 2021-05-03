package io.spring.model.purchase.idclass;

<<<<<<< HEAD
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
=======
import java.io.Serializable;

>>>>>>> f7b8e53dc986ee7251cedee8b65bee1f84b3392d
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
