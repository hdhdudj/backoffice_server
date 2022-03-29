package io.spring.model.goods.idclass;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TmmapiId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    public TmmapiId(String channelGb, String assortId){
        this.channelGb = channelGb;
        this.assortId = assortId;
    }

    private String channelGb;
    private String assortId;
}
