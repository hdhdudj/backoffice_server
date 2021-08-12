package io.spring.model.deposit.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.deposit.entity.Lsdpsd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositSelectListResponseData {
    public DepositSelectListResponseData(Date depositDt, String assortId, String assortNm, String purchaseVendorId){
        this.depositDt = depositDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.purchaseVendorId = purchaseVendorId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date depositDt;
    private String assortId;
    private String assortNm;
    private String purchaseVendorId;
    private List<Deposit> depositList;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Deposit{
        public Deposit(Lsdpsd lsdpsd) {
            this.depositNo = lsdpsd.getDepositNo();
            this.depositSeq = lsdpsd.getDepositSeq();
            this.depositKey = depositNo + "-" + depositSeq;
            this.assortId = lsdpsd.getAssortId();
            this.itemId = lsdpsd.getItemId();
            this.goodsKey = assortId + "-" + itemId;
            this.extraUnitcost = lsdpsd.getExtraUnitcost();
            this.depositDt = lsdpsd.getLsdpsm().getDepositDt();
        }
        private String depositKey;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date depositDt;
        public String purchaseNo;
        private String purchaseSeq;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String depositNo;
        private String depositSeq;
        private String purchaseVendorId;
        private String vdNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long depositQty;
        private Float extraUnitcost;
    }
}
