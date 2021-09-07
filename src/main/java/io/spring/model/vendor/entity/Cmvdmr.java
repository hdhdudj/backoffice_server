package io.spring.model.vendor.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name="cmvdmr")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Cmvdmr extends CommonProps {
    @Id
    private String id;
    private String vdNm;
    private String vendorType;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
}
