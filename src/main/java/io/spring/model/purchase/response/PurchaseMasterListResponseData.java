package io.spring.model.purchase.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.purchase.entity.Lspchm;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 발주리스트(마스터 기준으로 변경된 후) DTO
 */
@Getter
@Setter
public class PurchaseMasterListResponseData {
    public PurchaseMasterListResponseData(){}
    public PurchaseMasterListResponseData(LocalDate startDt, LocalDate endDt, String siteOrderNo, String channelOrderNo, String brandId, String vendorId, String purchaseGb){
        this.startDt = startDt.toString();
        this.endDt = endDt.toString();
        this.siteOrderNo = siteOrderNo;
        this.channelOrderNo = channelOrderNo;
        this.brandId = brandId;
        this.vendorId = vendorId;
        this.purchaseGb = purchaseGb;
    }
    private String startDt;
    private String endDt;
    private String siteOrderNo;
    private String channelOrderNo;
    private String brandId;
    private String vendorId;
    private String purchaseGb;
    private List<Purchase> purchases;

    @Getter
    @Setter
    public static class Purchase{
        public Purchase(){}
        public Purchase(Lspchm lspchm){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.siteOrderNo = lspchm.getSiteOrderNo();
            this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
            this.purchaseGb = lspchm.getPurchaseGb();
            this.purchaseStatus = lspchm.getPurchaseStatus();
        }
        private String purchaseNo;
        private String siteOrderNo;
        private String piNo;
        private String purchaseDt;
        private String purchaseGb;
        private String purchaseStatus;
    }
}
