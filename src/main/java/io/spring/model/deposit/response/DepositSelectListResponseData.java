package io.spring.model.deposit.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.deposit.entity.Lsdpsd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 입고 - 입고리스트 : 입고 리스트 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositSelectListResponseData {
    public DepositSelectListResponseData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String purchaseVendorId, String memo){
        this.startDt = startDt;
        this.endDt = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
		this.vendorId = purchaseVendorId;
        this.memo = memo;
    }
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
    private String assortId;
    private String assortNm;
	private String vendorId;
    private List<Deposit> depositList;
    // 21-12-08 추가
    private String memo;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Deposit implements SetOptionInterface {
        public Deposit(Lsdpsd lsdpsd) {
            this.depositNo = lsdpsd.getDepositNo();
            this.depositSeq = lsdpsd.getDepositSeq();
            this.depositKey = Utilities.addDashInMiddle(this.depositNo, this.depositSeq);
            this.assortId = lsdpsd.getAssortId();
            this.itemId = lsdpsd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
            this.extraUnitcost = lsdpsd.getExtraUnitcost();
            this.depositDt = Utilities.removeTAndTransToStr(lsdpsd.getLsdpsm().getDepositDt());
            this.purchaseDt = lsdpsd.getLspchd() == null? null : Utilities.removeTAndTransToStr(lsdpsd.getLspchd().getLspchm().getPurchaseDt());
            if(lsdpsd.getLspchd() != null){
                this.orderId = lsdpsd.getOrderId();
                this.orderSeq = lsdpsd.getOrderSeq();
                this.orderkey = Utilities.addDashInMiddle(orderId, orderSeq);
            }

			this.optionNm1 = lsdpsd.getItitmm().getItvari1() == null ? ""
					: lsdpsd.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = lsdpsd.getItitmm().getItvari2() == null ? ""
					: lsdpsd.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = lsdpsd.getItitmm().getItvari3() == null ? ""
					: lsdpsd.getItitmm().getItvari3().getOptionNm();

        }
        private String depositKey;
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss", timezone = "Asia/Seoul")
        private String depositDt;
        public String purchaseNo;
        private String purchaseSeq;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String depositNo;
        private String depositSeq;
		private String vendorId;
        private String vdNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long depositQty;
        private Float extraUnitcost;
        // 21-11-11 추가
        private Float weight;
        // 21-11-29 추가
        private String purchaseDt;
        // 21-11-30 추가
        private String orderId;
        private String orderSeq;
        private String orderkey;
    }
}
