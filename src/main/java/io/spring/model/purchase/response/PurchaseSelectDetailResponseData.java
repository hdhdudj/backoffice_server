package io.spring.model.purchase.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.purchase.entity.Lspchd;
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
        this.purchaseId = lspchm.getPurchaseNo();
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
    private String purchaseId;
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Items{
        public Items(Lspchd lspchd, Itasrt itasrt){
            this.assortId = lspchd.getAssortId();
            this.itemId = lspchd.getItemId();
            this.purchaseId = lspchd.getPurchaseNo();
            this.purchaseQty = lspchd.getPurchaseQty();
            this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
            this.purchaseStatus = lspchd.getLspchm().getPurchaseStatus();
            this.purchaseSeq = lspchd.getPurchaseSeq();
            this.assortNm = itasrt.getAssortNm();
            this.mdRrp = itasrt.getMdRrp();
            this.buySupplyDiscount = itasrt.getBuySupplyDiscount();
            this.deliMethod = itasrt.getDeliMth();
        }
//        public Items(Lspchd lspchd, TbOrderDetail tbOrderDetail, Itasrt itasrt){
//            this.purchaseId = lspchd.getPurchaseNo();
//            this.assortId = lspchd.getAssortId();
//            this.purchaseSeq = lspchd.getPurchaseSeq();
//            this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
//            this.purchaseStatus = lspchd.getLspchm().getPurchaseStatus();
//            this.itemId = lspchd.getItemId();
//            this.orderId = tbOrderDetail.getOrderId();
//            this.orderSeq = tbOrderDetail.getOrderSeq();
//            this.deliMethod = tbOrderDetail.getDeliMethod();
//            this.assortNm = itasrt.getAssortNm();
//            this.mdRrp = itasrt.getMdRrp();
//            this.buySupplyDiscount = itasrt.getBuySupplyDiscount();
//            // 옵션은 밖에서
//        }
        private String purchaseId;
        private String orderId;
        private String orderSeq;
        private String assortNm;
        private String assortId;
        private String optionNm1;
        private String optionNm2;
        private String deliMethod;
        private Float mdRrp;
        private Float buySupplyDiscount;
        private String itemId;
        private String purchaseSeq;
        private Long purchaseQty;
        private Float purchaseUnitAmt;
        private String purchaseStatus;
    }
}
