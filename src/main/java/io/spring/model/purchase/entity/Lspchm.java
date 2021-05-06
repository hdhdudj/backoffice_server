package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="lspchm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchm {
    private final static Logger logger = LoggerFactory.getLogger(Lspchm.class);
    public Lspchm(PurchaseInsertRequest purchaseInsertRequest){
        this.purchaseNo = purchaseInsertRequest.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequest.getPurchaseDt();
        try{
            this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.purchaseStatus = purchaseInsertRequest.getPurchaseStatus(); // 01 : 발주, 05 : 취소
        this.purchaseRemark = purchaseInsertRequest.getPurchaseRemark();
        this.siteGb = StringFactory.getGbOne(); // "01"
        this.vendorId = StringFactory.getFourStartCd(); // "0001"
        this.siteOrderNo = purchaseInsertRequest.getSiteOrderNo();
        this.siteTrackNo = purchaseInsertRequest.getSiteTrackNo();
        this.localPrice = purchaseInsertRequest.getLocalPrice();
        this.newLocalPrice = this.localPrice;
        this.localDeliFee = purchaseInsertRequest.getLocalDeliFee();
        this.newLocalDeliFee = this.localDeliFee;
        this.localTax = purchaseInsertRequest.getLocalTax();
        this.newLocalTax = this.localTax;
        this.disPrice = purchaseInsertRequest.getDisPrice();
        this.newDisPrice = this.disPrice;
        this.purchaseGb = StringFactory.getGbOne(); // "01" : 일반발주
        this.purchaseVendorId = purchaseInsertRequest.getPurchaseVendorId();
        this.storeCd = purchaseInsertRequest.getStoreCd(); // "00001"
        this.oStoreCd = purchaseInsertRequest.getOStoreCd();
        this.terms = purchaseInsertRequest.getTerms();
        this.delivery = purchaseInsertRequest.getDelivery();
        this.payment = purchaseInsertRequest.getPayment();
        this.carrier = purchaseInsertRequest.getCarrier();

    }
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
    private String siteOrderNo;
    private String siteTrackNo;
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

    // 연관관계 : lspchd
    @OneToMany
    @JsonIgnore
    @JoinColumn(name = "purchaseNo", referencedColumnName = "purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private List<Lspchd> lspchdList;



    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
