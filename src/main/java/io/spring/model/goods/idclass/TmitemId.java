package io.spring.model.goods.idclass;

import javax.persistence.Id;
import java.io.Serializable;

public class TmitemId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    private String channelGb;
    private String assortId;
    private String itemId;
    private String effStaDt;
    private String effEndDt;
}
