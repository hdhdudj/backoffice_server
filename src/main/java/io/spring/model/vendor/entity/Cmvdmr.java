package io.spring.model.vendor.entity;


import javax.persistence.*;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;

import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.idclass.IfBrandId;
import io.spring.model.vendor.request.VendorInsertRequest;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Entity
@Table(name="cmvdmr")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cmvdmr extends CommonProps{
    @Id
    private String id;
    private String vdNm;
    private String vendorType;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    private String delYn;

    public Cmvdmr(VendorInsertRequest vendorInsertRequest) {
        this.vdNm = vendorInsertRequest.getVdNm();
        this.vendorType = vendorInsertRequest.getVendorType();
        this.terms = vendorInsertRequest.getTerms();
        this.delivery = vendorInsertRequest.getDelivery();
        this.payment = vendorInsertRequest.getPayment();
        this.carrier = vendorInsertRequest.getCarrier();
        this.delYn = StringFactory.getGbTwo();
    }
}
