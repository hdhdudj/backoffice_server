package io.spring.model.purchase.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PurchaseSelectDetailResponseData {
    public PurchaseSelectDetailResponseData(Lspchm lspchm){
        this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
        this.purchaseVendorId = lspchm.getPurchaseVendorId();
        this.purchaseRemark = lspchm.getPurchaseRemark();
        this.storageId = lspchm.getStoreCd();
        this.terms = lspchm.getTerms();
        this.delivery = lspchm.getDelivery();
        this.payment = lspchm.getPayment();
        this.carrier = lspchm.getCarrier();
        this.siteOrderNo = lspchm.getSiteOrderNo();
        this.purchaseStatus = lspchm.getPurchaseStatus();
    }
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String purchaseDt;
    private String purchaseVendorId;
    private String purchaseRemark;
    private String storageId;
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
