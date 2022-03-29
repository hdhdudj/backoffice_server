package io.spring.model.deposit.idclass;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class LsdpsdId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;
    public LsdpsdId(String depositNo, String depositSeq){
        this.depositNo = depositNo;
        this.depositSeq = depositSeq;
    }

    private String depositNo;
    private String depositSeq;
}
