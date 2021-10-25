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
    public DepositSelectListResponseData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String purchaseVendorId){
        this.startDt = startDt;
        this.endDt = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
		this.vendorId = purchaseVendorId;
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
        private Long depositQty;
        private Float extraUnitcost;
    }
}
