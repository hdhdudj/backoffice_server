package io.spring.model.deposit.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 입고 - 발주선택창 화면 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseListInDepositModalData {
	public PurchaseListInDepositModalData(LocalDate startDt, LocalDate endDt, String purchaseVendorId,
			String storageId) {
        this.startDt = startDt;
        this.endDt = endDt;
		this.vendorId = purchaseVendorId;
		this.storageId = storageId;
    }
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
	private String vendorId;
	private String storageId;
	// private String purchaseVendorId; : ownerId로 수정 --> vendorId로 수정
    private List<Purchase> purchases;

    /**
     * Lspchm 기준 목록으로 변경된 발주리스트 생성자
     */
    public PurchaseListInDepositModalData(LocalDate startDt, LocalDate endDt, String siteOrderNo, String orderNo, String brandId, String vendorId, String purchaseGb) {

    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Purchase{
        public Purchase(Lspchm lspchm){
            this.purchaseNo = lspchm.getPurchaseNo();
            this.purchaseGb = lspchm.getPurchaseGb();
            this.dealtypeCd = lspchm.getDealtypeCd();
            this.purchaseDt = Utilities.removeTAndTransToStr(lspchm.getPurchaseDt());
			this.vendorId = lspchm.getVendorId();
            this.purchaseStatus = lspchm.getPurchaseStatus();
            this.siteOrderNo = lspchm.getSiteOrderNo();
            this.siteTrackNo = lspchm.getSiteTrackNo();
        }
        private String purchaseNo;
        private String purchaseGb;
        private String dealtypeCd;

//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
        private String purchaseDt;
//        private String purchaseVendorId; : ownerId로 수정
		private String vendorId;
        private String purchaseStatus;
        private String siteOrderNo; // 해외주문번호
        private String siteTrackNo; // 해외트래킹번호
        // 21-12-20 추가
        private List<PurchaseSelectDetailResponseData.Items> items;
    }
}
