package io.spring.model.purchase.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 발주등록 request DTO
 */
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseInsertRequestData {

    // 입고예정에 의한 주문발주시 이용
    public PurchaseInsertRequestData(TbOrderMaster tbOrderMaster, TbOrderDetail tbOrderDetail, Itasrt itasrt, Ititmc ititmc, Ititmt ititmt, String purchaseGb) {
        // lspchm
        this.purchaseStatus = StringFactory.getGbOne(); // 01 : 발주, 05 : 취소
        this.effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT()); // 9999-12-31 23:59:59 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringFactory.getFourStartCd(); // "0001" 하드코딩
        this.siteOrderNo = null; // 해외주문번호
        this.siteTrackNo = null;
//        this.localPrice = tbOrderDetail.get

        this.assortId = itasrt.getAssortId();
        this.itemId = ititmt.getItemId();
        this.itemGrade = ititmt.getItemGrade();

        this.purchaseDt = LocalDateTime.now();
    }

    /**
     * 21-05-03 Pecan
     * 발주 insert request dto
     * (무슨 화면인지 애매함.. 확인 필요)
      */
    // 여러 테이블에서 쓰는 변수
    private String purchaseNo; // lspchm, lspchs
    private String purchaseStatus; // lspchm, lspchs, lspchb
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime effEndDt; // lspchm, lspchs
    private String siteGb; // lspchm, ititmt
    private String vendorId; // lspchm, ititmt
    private String assortId; // lspchd, lsdpsp, ititmt
    private String itemId; // lspchd, lsdpsp
    private String itemGrade; // lspchd, ititmt

	private String dealtypeCd;

    // lspchm
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime purchaseDt ;
    private String purchaseRemark;
    private String siteOrderNo;
    private String siteTrackNo;
    private Float localPrice;
    private Float newLocalPrice;
    private Float localDeliFee;
    private Float newLocalDeliFee;
    private Float localTax;
    private Float disPrice;
    private Float newDisPrice;
    private String purchaseGb;
//    private String purchaseVendorId; : ownerId로 변경됨
    private String ownerId;
    private String storageId;
    @JsonProperty("oStorageId")
    private String oStorageId;
    private String terms;
    private String delivery;
    private String payment;
    private String carrier;
    // 21-12-14 추가
    private String piNo;
    private String memo;
    // 22-01-07 추가
    private String deliFee;

	private List<Items> items;

    // lspchs
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime effStaDt;

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
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime smReservationDt;
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

    // id 관련
	@NotNull(message = "userId는 필수 값입니다.")
    private String userId;

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

//        private String purchaseStatus;
		private String orderId;
		private String orderSeq;
        // 22-01-24 추가
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate compleDt;
        // 22-04-05 추가
        private String vendorId;
    }

}
