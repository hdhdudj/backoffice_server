package io.spring.model.goods.entity;

import io.spring.model.goods.idclass.ItitmtId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity
@Getter
@Setter
@IdClass(ItitmtId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmt {
    @Id
    private String storageId;
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    private String itemGrade;
    @Id
    private Date effEndDt;
    @Id
    @CreationTimestamp
    private Date effStaDt;
    private String stockGb;
    private Long tempIndicateQty;
    private Long tempQty;
    private Float stockAmt;
    private String vendorId;
    private String siteGb;
    @CreationTimestamp
    private Long regId;
    private String regDt;
    private Long updId;
    @UpdateTimestamp
    private String updDt;
}
