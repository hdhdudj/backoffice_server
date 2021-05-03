package io.spring.model.purchase.request;

<<<<<<< HEAD
public class PurchaseInsertRequest {
=======
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseInsertRequest {
    /**
     * 21-05-03 Pecan
     * 발주 insert request dto
      */
    // 여러 테이블에서 쓰는 변수
    private String purchaseNo; // lspchm, lspchs
    private String purchaseStatus; // lspchm, lspchs, lspchb
    private Date effEndDt; // lspchm, lspchs
    private String siteGb; // lspchm, ititmt
    private String vendorId; // lspchm, ititmt
    private String assortId; // lspchd, lsdpsp, ititmt
    private String itemId; // lspchd, lsdpsp
    private String itemGrade; // lspchd, ititmt

    // lspchm
    private Date purchaseDt;
    private String purchaseRemark;
    private String siteOrderno;
    private String siteTrackno;
    private Long localPrice;
    private Long newLocalPrice;
    private Long localDeliFee;
    private Long newLocalDeliFee;
    private Long localTax;
    private Long newLocalTax;
    private Long disPrice;
    private Long newDisPrice;
    private String purchaseGb;
    private String purchaseVendorId;
    private String storeCd;
    private String oStoreCd;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;

    // lspchs
    private Date effStaDt;

    // lspchd
    private String purchaseSeq;
    private Long purchaseQty;
    private Float purchaseUnitamt;
    private Float purchaseItemamt;
    private String vatGb;
    private String setGb;
    private String mailsendYn;

    // lspchb
    private String cancelGb;

    // lsdpsp
    private String depositPlanId;
    private Date smReservationDt;
    private Long purchasePlanQty;
    private Long purchaseTakeQty;
    private String planStatus;
    private String orderId;
    private String orderSeq;
//    private String planChgReason;
    private String claimItemYn;

    // ititmt
    private String storageId;
    private String stockGb;
    private Long tempIndicateQty;
    private Long tempQty;
    private Float stockAmt;
>>>>>>> f7b8e53dc986ee7251cedee8b65bee1f84b3392d
}
