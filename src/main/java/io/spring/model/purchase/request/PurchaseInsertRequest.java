package io.spring.model.purchase.request;

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
    // lspchd
    private String purchaseNo; // lspchs
    private Date purchaseDt;
    private Date effEndDt; // lspchs
    private String purchaseStatus; // lspchs, lspchb
    private String purchaseRemark;
    private String siteGb; // ititmt
    private String vendorId; // ititmt
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
    private String assortId; // lsdpsp, ititmt
    private String itemId; // lsdpsp
    private Long purchaseQty;
    private Float purchaseUnitamt;
    private Float purchaseItemamt;
    private String itemGrade; // ititmt
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
}
