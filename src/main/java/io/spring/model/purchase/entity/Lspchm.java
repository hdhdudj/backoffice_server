package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Entity
@Table(name="lspchm")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchm extends CommonProps {
    public Lspchm(Lspchm purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
//        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus(); // 01 : 발주, 03 : 부분입고, 04 : 완전입고, 05 : 취소
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
    public Lspchm(PurchaseInsertRequestData purchaseInsertRequestData){
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
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

    /**
     * 주문이동지시, 상품이동지시 저장시 실행되는 생성자
     */
    public Lspchm(String purchaseNo) {
        this.purchaseNo = purchaseNo;
        this.purchaseDt = LocalDateTime.now();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseStatus = StringFactory.getGbFour(); // 01 : 발주, 04 : 이동지시?, 05 : 취소 (04 하드코딩)
//        this.purchaseRemark : 바깥 set
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
        this.vendorId = StringFactory.getFourStartCd(); // "0001" 하드코딩
//        this.siteOrderNo : 바깥 set
//        this.siteTrackNo : 바깥 set (?)
//        this.localPrice : ?
        this.newLocalPrice = this.localPrice;
//        this.localDeliFee : ?
        this.newLocalDeliFee = this.localDeliFee;
//        this.localTax : ?
//        this.disPrice : ?
        this.newDisPrice = this.disPrice;
        this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청 (02 하드코딩)
//        this.purchaseVendorId : ?
//        this.storeCd : 바깥 set
//        this.oStoreCd : 바깥 set (itasrt의 창고id)
//        this.terms : ?
//        this.delivery : ?
//        this.payment : ?
//        this.carrier : ?

//        this.dealtypeCd = StringFactory.getGbOne(); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (01 하드코딩) 바깥에서 set
    }

    @Id
    private String purchaseNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime purchaseDt;
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
