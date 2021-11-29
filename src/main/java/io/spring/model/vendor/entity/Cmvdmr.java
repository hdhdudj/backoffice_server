package io.spring.model.vendor.entity;


import javax.persistence.*;

import io.spring.model.common.entity.CommonProps;

import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.idclass.IfBrandId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Entity
@Table(name="cmvdmr")
@Getter
@Setter
public class Cmvdmr extends CommonProps{
    @Id
    private String id;
    private String vdNm;
    private String vendorType;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
}
