package io.spring.model.goods.idclass;

import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

public class ItitmtId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;
    private String storageId;
    private String assortId;
    private String itemId;
    private String itemGrade;
    private Date effEndDt;
    @CreationTimestamp
    private Date effStaDt;
}
