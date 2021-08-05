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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseSelectListResponseData {
    public PurchaseSelectListResponseData(List<Purchase> purchases){
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
        private String purchaseNo; // 발주번호
        private String purchaseVendorId; // 구매처
        private String purchaseSeq; // 발주순번
        private Date purchaseDt; // 발주일자
        private String purchaseGb; // 발주구분
        private String purchaseStatus; // 발주상태
        private String orderId; // 주문번호
        private String orderSeq; // 주문순번
        private String assortId; // 품목코드
        private String itemId; // 상품코드
        private String assortNm; // 상품이름
        private Float purchaseUnitAmt; //
        private String optionNm1; // 색상
        private String optionNm2; // 사이즈
        private Long purchaseQty; // 발주수량
        private Float purchasePrice; // 발주단가
        private Float purchaseCost; // 발주금액
        private Long depositQty; // 입고수량
        private String siteOrderNo;
    }
}
