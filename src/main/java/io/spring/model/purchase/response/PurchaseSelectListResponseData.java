package io.spring.model.purchase.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseSelectListResponseData {
    public PurchaseSelectListResponseData(Map<String, Object> param){
        this.startDt = (Date) param.get(StringFactory.getStrStartDt());
        this.endDt = (Date) param.get(StringFactory.getStrEndDt());
        this.assortId = (String) param.get(StringFactory.getStrAssortId());
        this.purchasStatus = (String) param.get(StringFactory.getStrPurchaseStatus());
        this.purchaseGb = (String)param.get(StringFactory.getStrPurchaseGb());
    }
    // 발주리스트 화면
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDt;
    private String assortId;
    private String assortNm; // 바깥에서 set
    private String purchasStatus;
    private String purchaseGb;

    // 입고처리 화면
    private String purchaseNo;
    private Date purchaseDt;
    private Date DepositDt;
    private String depositStoreId;

    // 화면 공통요소
    private String purchaseVendorId;

    private List<Purchase> purchaseList;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Purchase{
        public Purchase(Lspchm lspchm){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.purchaseVendorId = lspchm.getPurchaseVendorId();
        }
        // 발주리스트, 입고처리 화면 공통 요소
        private String purchaseNo; // 발주번호
        private String purchaseSeq; // 발주순번
        private String assortId; // 품목코드
        private String itemId; // 상품코드
        private String itemNm; // 상품이름
        private String optionNm1; // 색상
        private String optionNm2; // 사이즈

        // 발주리스트 화면 요소
        private Date purchaseDt; // 발주일자
        private String purchaseGb; // 발주구분
        private String purchaseStatus; // 발주상태
        private String purchaseVendorId; // 구매처
        private String orderId; // 주문번호
        private String orderSeq; // 주문순번
        private Float purchaseUnitAmt; // 개당금액
        private Long purchaseQty; // 발주수량
        private Float purchasePrice; // 발주단가
        private String siteOrderNo; // 해외주문번호

        // 입고처리 화면 요소
        private String depositPlanId;
        private Long depositQty; // 입고수량
        private Float purchaseCost; // 발주금액
    }
}
