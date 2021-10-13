package io.spring.model.purchase.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseSelectListResponseData {
    public PurchaseSelectListResponseData(Map<String, Object> param){
        this.startDt = (LocalDate) param.get(StringFactory.getStrStartDt());
        this.endDt = (LocalDate) param.get(StringFactory.getStrEndDt());
        this.assortId = (String) param.get(StringFactory.getStrAssortId());
        this.purchasStatus = (String) param.get(StringFactory.getStrPurchaseStatus());
        this.purchaseGb = (String)param.get(StringFactory.getStrPurchaseGb());

    }
    public PurchaseSelectListResponseData(Lspchm lspchm){
        this.purchaseNo = lspchm.getPurchaseNo();
        this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
        this.depositStoreId = lspchm.getStoreCd();
        this.ownerId = lspchm.getOwnerId();
        this.purchaseGb = lspchm.getPurchaseGb();
		this.vendorId = lspchm.getVendorId();
//        this.dealtypeCd = lspchm.getDealtypeCd();
    }
    // 발주리스트 화면
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
    private String assortId;
    private String assortNm; // 바깥에서 set
    private String purchasStatus;
    private String purchaseGb;

    // 입고처리 화면
    private String purchaseNo;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String purchaseDt;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String DepositDt;
    private String depositStoreId;
//    private String dealtypeCd; // 01 : 상품발주, 02 : 주문발주, 03 : 입고예정 주문발주

    // 화면 공통요소
//    private String purchaseVendorId; : ownerId로 수정
    private String ownerId;

	private String vendorId;

    private List<Purchase> purchaseList;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Purchase implements SetOptionInterface {
        /**
         * 발주리스트 get 시 작동하는 생성자 
         */
        public Purchase(Lspchm lspchm, Lsdpsp lsdpsp, Itasrt itasrt){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.ownerId = lspchm.getOwnerId();
            this.purchaseGb = lspchm.getPurchaseGb();
            this.dealtypeCd = lspchm.getDealtypeCd();

            this.purchaseSeq = lsdpsp.getPurchaseSeq();
            this.assortId = lsdpsp.getAssortId();
            this.itemId = lsdpsp.getItemId();
            this.depositPlanId = lsdpsp.getDepositPlanId();

            this.itemNm = itasrt.getAssortNm();

            this.depositQty = 0l;

            this.purchaseCost = lsdpsp.getLspchd().getPurchaseUnitAmt();
        }
        public Purchase(Lspchm lspchm, Lspchd lspchd){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.dealtypeCd = lspchm.getDealtypeCd();
            this.purchaseSeq = lspchd.getPurchaseSeq();
            this.purchaseKey = Utilities.addDashInMiddle(purchaseNo, purchaseSeq);
            this.ownerId = lspchm.getOwnerId();
            this.purchaseGb = lspchm.getPurchaseGb();
            this.assortId = lspchd.getAssortId();
            this.itemId = lspchd.getItemId();
            this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
            this.purchaseGb = lspchm.getPurchaseGb();
            this.purchaseCost = lspchd.getPurchaseUnitAmt();
            this.purchaseStatus = lspchm.getPurchaseStatus();
            this.orderId = lspchd.getOrderId();
            this.orderSeq = lspchd.getOrderSeq();
            this.purchaseQty = lspchd.getPurchaseQty();
            this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
            this.siteOrderNo = lspchd.getSiteOrderNo();
        }
        // 발주리스트, 입고처리 화면 공통 요소
        private String purchaseNo; // 발주번호
        private String purchaseSeq; // 발주순번
        private String purchaseKey; // 발주번호-발주순번
        private String assortId; // 품목코드
        private String itemId; // 상품코드
        private String itemNm; // 상품이름
        private String optionNm1; // 색상
        private String optionNm2; // 사이즈

        // 발주리스트 화면 요소
//        @JsonDeserialize(using = LocalDateDeserializer.class)
//        @JsonSerialize(using = LocalDateSerializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private String purchaseDt; // 발주일자
        private String purchaseGb; // 발주구분 - 01 : 일반발주, 02 : 이동요청
        private String purchaseStatus; // 발주상태 - 01 : 발주, 03 : 부분입고, 04 : 완전입고, 05 : 취소
        private String dealtypeCd; // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정
//        private String purchaseVendorId; // 구매처 : ownerId로 수정
        private String ownerId;
        private String orderId; // 주문번호
        private String orderSeq; // 주문순번
        private Float purchaseUnitAmt; // 개당금액
        private Long purchaseQty; // 발주수량
        private Float purchasePrice; // 발주단가
        private String siteOrderNo; // 해외주문번호

        // 입고처리 화면 요소
        private String depositPlanId; // 입고예정번호
        private Long availableQty; // 가능수량
        private Long depositQty; // 입고수량
        private Float purchaseCost; // 발주금액
    }
}
