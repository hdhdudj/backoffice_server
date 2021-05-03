package io.spring.model.goods.idclass;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
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
