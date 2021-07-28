package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
     * 주문이동지시 저장시 실행되는 생성자
     */
    public Lspchm(String purchaseNo, Itasrt itasrt, Lspchm lspchm, Lsdpsd lsdpsd, TbOrderMaster tbOrderMaster) {
        this.purchaseNo = purchaseNo;
        this.purchaseDt = new Date();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.purchaseStatus = StringFactory.getGbOne(); // 01 : 발주, 05 : 취소
        this.purchaseRemark = Long.toString(lspchm.getRegId());
        this.siteGb = StringFactory.getGbOne(); // "01" 하드코딩
        this.vendorId = StringFactory.getFourStartCd(); // "0001" 하드코딩
        this.siteOrderNo = null;
        this.siteTrackNo = null;
//        this.localPrice = lspchd의 같은 항목의 sum(item_amt)
//        this.newLocalPrice = lspchd의 같은 항목의 sum(item_amt)
        this.localDeliFee = null;
        this.newLocalDeliFee = null;
        this.localTax = null;
        this.newLocalTax = null;
        this.disPrice = null;
        this.newDisPrice = null;
        this.purchaseGb = StringFactory.getGbTwo(); // 02 하드코딩 : 이동지시
        this.purchaseVendorId = itasrt.getVendorId();
        this.storeCd = itasrt.getStorageId();
        this.oStoreCd = null;
        this.terms = lspchm.getTerms();
        this.delivery = lspchm.getDelivery();
        this.payment = lspchm.getPayment();
        this.carrier = lspchm.getCarrier();

//        this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();
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
