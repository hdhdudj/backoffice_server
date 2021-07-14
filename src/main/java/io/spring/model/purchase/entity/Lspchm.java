package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name="lspchm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchm extends CommonProps {
    public Lspchm(PurchaseInsertRequestData purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequestData.getPurchaseDt();
        try{
            this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        }
        catch (Exception e){
            log.debug(e.getMessage());
        }
        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus(); // 01 : 발주, 05 : 취소
        this.purchaseRemark = purchaseInsertRequestData.getPurchaseRemark();
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
        this.vendorId = StringFactory.getFourStartCd(); // "0001" 하드코딩
        this.siteOrderNo = purchaseInsertRequestData.getSiteOrderNo();
        this.siteTrackNo = purchaseInsertRequestData.getSiteTrackNo();
        this.localPrice = purchaseInsertRequestData.getLocalPrice();
        this.newLocalPrice = this.localPrice;
        this.localDeliFee = purchaseInsertRequestData.getLocalDeliFee();
        this.newLocalDeliFee = this.localDeliFee;
        this.localTax = purchaseInsertRequestData.getLocalTax();
        this.newLocalTax = this.localTax;
        this.disPrice = purchaseInsertRequestData.getDisPrice();
        this.newDisPrice = this.disPrice;
		// this.purchaseGb = StringFactory.getGbOne(); // "01" : 일반발주
		this.purchaseGb = purchaseInsertRequestData.getPurchaseGb();
        this.purchaseVendorId = purchaseInsertRequestData.getPurchaseVendorId();
        this.storeCd = purchaseInsertRequestData.getStoreCd(); // "00001"
        this.oStoreCd = purchaseInsertRequestData.getOStoreCd();
        this.terms = purchaseInsertRequestData.getTerms();
        this.delivery = purchaseInsertRequestData.getDelivery();
        this.payment = purchaseInsertRequestData.getPayment();
        this.carrier = purchaseInsertRequestData.getCarrier();

		this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();

    }
    @Id
    private String purchaseNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date purchaseDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    private String purchaseStatus;
    private String purchaseRemark;
    private String siteGb;
    private String vendorId;
    private String dealtypeCd;
    private String siteOrderNo;
    private String siteTrackNo;
    private String purchaseCustNm;
    private Float localPrice;
    private Float newLocalPrice;
    private Float localDeliFee;
    private Float newLocalDeliFee;
    private Float localTax;
    private Float newLocalTax;
    private Float disPrice;
    private Float newDisPrice;
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
}
