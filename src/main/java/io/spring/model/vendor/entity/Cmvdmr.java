package io.spring.model.vendor.entity;

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
@Table(name="cmvdmr")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Cmvdmr {
    @Id
    private String vendorId;
    private String vdNm;
    private String vdEnm;
    private String vendorType;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
