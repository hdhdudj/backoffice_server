package io.spring.model.purchase.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchb {
    @Id
    private Long seq;
    private String purchaseNo;
    private String purchaseSeq;
    private Date effEndDt;
    @CreationTimestamp
    private Date effStaDt;
    private String purchaseStatus;
    private String cancelGb;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
