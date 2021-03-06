package io.spring.model.deposit.response;

import java.util.List;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 입고 - 입고내역 : 입고 내역 DTO
 */
@Getter
@Setter
public class DepositSelectDetailResponseData {
    public DepositSelectDetailResponseData(Lsdpsm lsdpsm){
        this.depositNo = lsdpsm.getDepositNo();
        this.depositDt = Utilities.removeTAndTransToStr(lsdpsm.getDepositDt());
//        this.storeCd = lsdpsm.getStoreCd();
		this.vendorId = lsdpsm.getVendorId();
    }
    private String depositNo;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String depositDt;
	private String vendorId;

    // 21-12-08 추가
    private String memo;
//    private String storeCd;
//    private String depositStatus;
//    private String depositVendorId;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Item implements SetOptionInterface {
        public Item(Lsdpsd lsdpsd){
            // lsdpsd
            this.depositNo = lsdpsd.getDepositNo();
            this.depositSeq = lsdpsd.getDepositSeq();
            this.depositDt = Utilities.removeTAndTransToStr(lsdpsd.getLsdpsm().getDepositDt());
            this.depositKey = Utilities.addDashInMiddle(depositNo,depositSeq);
            this.purchaseNo = lsdpsd.getInputNo();
            this.purchaseSeq = lsdpsd.getInputSeq();
            this.purchaseKey = Utilities.addDashInMiddle(purchaseNo,purchaseSeq);
            this.assortId = lsdpsd.getAssortId();
            this.dealtypeCd = lsdpsd.getLspchd().getLspchm().getDealtypeCd();
//            this.itemGrade = lsdpsd.getItemGrade();
            this.itemId = lsdpsd.getItemId();
            this.extraUnitcost = lsdpsd.getExtraUnitcost();
            this.purchaseGb = lsdpsd.getLspchd().getLspchm().getPurchaseGb();
            this.memo = lsdpsd.getMemo();

			this.optionNm1 = lsdpsd.getItitmm().getItvari1() == null ? ""
					: lsdpsd.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = lsdpsd.getItitmm().getItvari2() == null ? ""
					: lsdpsd.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = lsdpsd.getItitmm().getItvari3() == null ? ""
					: lsdpsd.getItitmm().getItvari3().getOptionNm();

        }
        private String depositNo;
        private String depositSeq;
        private String depositKey;
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private String depositDt;
        public String purchaseNo;
        private String purchaseSeq;
        private String purchaseKey;
        private String dealtypeCd;
        private String assortId;
        private String itemId;
        private String purchaseGb;
        private String itemNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;

//        private String itemGrade;
        private Long depositQty;
        private Float extraUnitcost;
        private String depositStatus;
        // 21-12-08 추가
        private String memo;

		// 22-02-08
		private String rackNo;
    }
}
