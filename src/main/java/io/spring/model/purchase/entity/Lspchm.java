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
@Table(name="lspchm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchm {
    @Id
    private String purchaseNo;
    @CreationTimestamp
    private Date purchaseDt;
    private Date effEndDt;
    private String purchaseStatus;
    private String purchaseRemark;
    private String siteGb;
    private String vendorId;
    private String dealtypeCd;
    private String siteOrderno;
    private String siteTrackno;
    private String purchaseCustNm;
    private Long localPrice;
    private Long newLocalPrice;
    private Long localDeliFee;
    private Long newLocalDeliFee;
    private Long localTax;
    private Long newLocalTax;
    private Long disPrice;
    private Long newDisPrice;
    private String cardId;
    private String purchaseGb;
    private String purchaseVendorId;
    private String affilVdId;
    private String storeCd;
    private String oStoreCd;
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
