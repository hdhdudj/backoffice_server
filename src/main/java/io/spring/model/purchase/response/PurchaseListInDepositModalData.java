package io.spring.model.purchase.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 입고 - 발주선택창 화면 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseListInDepositModalData {
    public PurchaseListInDepositModalData(Date startDt, Date endDt, String purchaseVendorId){
        this.startDt = startDt;
        this.endDt = endDt;
        this.purchaseVendorId = purchaseVendorId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDt;
    private String purchaseVendorId;
    private List<Purchase> purchases;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Purchase{
        public Purchase(Lspchm lspchm){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.purchaseGb = lspchm.getPurchaseGb();
            this.purchaseDt = lspchm.getPurchaseDt();
            this.purchaseVendorId = lspchm.getPurchaseVendorId();
            this.purchaseStatus = lspchm.getPurchaseStatus();
            this.siteOrderNo = lspchm.getSiteOrderNo();
            this.siteTrackNo = lspchm.getSiteTrackNo();
        }
        private String purchaseNo;
        private String purchaseGb;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
        private Date purchaseDt;
        private String purchaseVendorId;
        private String purchaseStatus;
        private String siteOrderNo; // 해외주문번호
        private String siteTrackNo; // 해외트래킹번호
    }
}
