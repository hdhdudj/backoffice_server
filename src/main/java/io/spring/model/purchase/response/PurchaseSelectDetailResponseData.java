package io.spring.model.purchase.response;

import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PurchaseSelectDetailResponseData {
    public PurchaseSelectDetailResponseData(Lspchm lspchm){
        this.purchaseDt = lspchm.getPurchaseDt();
        this.purchaseVendorId = lspchm.getPurchaseVendorId();
        this.purchaseRemark = lspchm.getPurchaseRemark();
        this.storeCd = lspchm.getStoreCd();
        this.terms = lspchm.getTerms();
        this.delivery = lspchm.getDelivery();
        this.payment = lspchm.getPayment();
        this.carrier = lspchm.getCarrier();
        this.siteOrderNo = lspchm.getSiteOrderNo();
        this.purchaseStatus = lspchm.getPurchaseStatus();
    }
    private Date purchaseDt;
    private String purchaseVendorId;
    private String purchaseRemark;
    private String storeCd;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    private String siteOrderNo;
    private String purchaseStatus;
    private List<Items> items;

    @Getter
    @Setter
    public static class Items{
        private String assortId;
        private String itemId;
        private String purchaseSeq;
        private Long purchaseQty;
        private Float purchaseUnitAmt;
        private String purchaseStatus;
    }
}
