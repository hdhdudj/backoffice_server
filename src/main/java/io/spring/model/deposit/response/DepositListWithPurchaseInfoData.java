package io.spring.model.deposit.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DepositListWithPurchaseInfoData {
    private String purchaseNo;
    private String purchaseVendorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date purchaseDt;
    private String storageId;
    private List<Deposit> deposits;

    @Getter
    @Setter
    public static class Deposit{
        private String depositNo;
        private String depositSeq;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
        private Date depositDt;
        private String depositVendorId;
        private String depositVendorNm;
        private String assortId;
        private String itemId;
        private String assortNm;
        private String optionNm;
        private Long availableQty; // Lsdpsp.purchasePlanQty - Lsdpsp.purchaseTakeQty
        private Long depositQty;
        private Float extraUnitcost;
    }
}
