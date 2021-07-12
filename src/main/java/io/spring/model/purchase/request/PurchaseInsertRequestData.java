package io.spring.model.purchase.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseInsertRequestData {

    // 입고예정에 의한 주문발주시 이용
    public PurchaseInsertRequestData(Itasrt itasrt, Ititmc ititmc, Ititmt ititmt) {

    }

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date purchaseDt ;
    private String purchaseRemark;
    private String siteOrderNo;
    private String siteTrackNo;
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


	private List<Items> items;

	// private List<Items> items;

    // lspchs
    private Date effStaDt;

    // lspchd
    private String purchaseSeq;
    private Long purchaseQty;
    private Float purchaseUnitAmt;
    private Float purchaseItemAmt;
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
//    private String storageId;
    private String stockGb;
    private Long tempIndicateQty;
    private Long tempQty;
    private Float stockAmt;


    @Getter
    @Setter
	@ToString
    public static class Items{
        private String assortId;
        private String itemId;
        private String itemGrade = "11";
        private String purchaseSeq;
        private Long purchaseQty;
        private Float purchaseUnitAmt;
        private String purchaseStatus;
		private String orderId;
		private String orderSeq;
    }

}
