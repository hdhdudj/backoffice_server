package io.spring.model.purchase.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * 발주내역(발주사후) get DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@PropertySource("classpath:application.properties")
public class PurchaseSelectDetailResponseData {
    public PurchaseSelectDetailResponseData(Lspchm lspchm){
        this.purchaseId = lspchm.getPurchaseNo();
        this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
        this.vendorId = lspchm.getVendorId();
        this.purchaseRemark = lspchm.getPurchaseRemark();
        this.storageId = lspchm.getStoreCd();
        this.terms = lspchm.getTerms();
        this.delivery = lspchm.getDelivery();
        this.payment = lspchm.getPayment();
        this.carrier = lspchm.getCarrier();
        this.siteOrderNo = lspchm.getSiteOrderNo();
        this.purchaseStatus = lspchm.getPurchaseStatus();
        this.dealtypeCd = lspchm.getDealtypeCd();
        this.piNo = lspchm.getPiNo();
        this.memo = lspchm.getMemo();
    }
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String purchaseId;
    private String purchaseDt;
//    private String purchaseVendorId; : ownerId로 수정
    private String vendorId;
    private String purchaseRemark;
    private String storageId;
    private String terms;
    private String dealtypeCd;
    private String delivery;
    private String payment;
    private String carrier;
    private String siteOrderNo;
    private String purchaseStatus;
    private List<Items> items;
    // 21-12-07 추가
    private String piNo;
    private String memo;

    @Getter
    @Setter
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Items implements SetOptionInterface {
        public Items(){

        }
        public Items(Lspchd lspchd, Ititmm ititmm, Itasrt itasrt, Itaimg itaimg){
            this.assortId = lspchd.getAssortId();
            this.itemId = lspchd.getItemId();
            this.itemKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
            this.purchaseId = lspchd.getPurchaseNo();
            this.purchaseQty = lspchd.getPurchaseQty();
            this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
            this.purchaseStatus = lspchd.getLspchm().getPurchaseStatus();
            this.purchaseSeq = lspchd.getPurchaseSeq();
            this.assortNm = itasrt.getAssortNm();
            this.mdRrp = itasrt.getMdRrp();
            this.buySupplyDiscount = itasrt.getBuySupplyDiscount();
            this.deliMethod = itasrt.getDeliMth();

            this.modelNo = itasrt.getAssortModel() + (ititmm.getModelNo() == null || ititmm.getModelNo().trim().equals("")? "" : " "+ititmm.getModelNo());
            this.origin = itasrt.getOrigin();
            this.custCategory = itasrt.getCustCategory();
            this.material = ititmm.getMaterial();
            this.imagePath = itaimg == null? null : this.imgServerUrl + itaimg.getImagePath() + itaimg.getImageName();
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
        private String itemId;
        private String itemKey;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String deliMethod;
        private Float mdRrp;
        private Float buySupplyDiscount;
        private String purchaseSeq;
        private Long purchaseQty;
        private Float purchaseUnitAmt;
        private String purchaseStatus;

        // 21-12-03 추가
        private String imagePath;
        private String modelNo;
        private String origin;
        private String custCategory;
        private String material;
        @Value("${ftp.prefix_url}")
        private String imgServerUrl;

        // 21-12-06 추가
        private String custNm;
        private String channelOrderNo;
    }
}
