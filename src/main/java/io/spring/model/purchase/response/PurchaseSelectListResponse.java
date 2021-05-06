package io.spring.model.purchase.response;

import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseSelectListResponse {
    public PurchaseSelectListResponse(List<Purchase> purchases){
        this.purchaseList = purchases;
    }
    private List<Purchase> purchaseList;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Purchase{
        public Purchase(Lspchm lspchm){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.purchaseVendorId = lspchm.getPurchaseVendorId();
        }
        private String purchaseNo;
        private String purchaseVendorId;
        private String assortId;
        private String itemId;
        private String assortNm;
        private String optionNm;
        private String purchaseQty;
        private String purchaseUnitAmt;
        private String siteOrderNo;
    }
}
