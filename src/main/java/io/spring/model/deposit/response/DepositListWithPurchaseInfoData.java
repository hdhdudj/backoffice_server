package io.spring.model.deposit.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositListWithPurchaseInfoData {
    public DepositListWithPurchaseInfoData(Lspchm lspchm, List<DepositListWithPurchaseInfoData.Deposit> deposits){
        this.purchaseNo = lspchm.getPurchaseNo();
        this.purchaseVendorId = lspchm.getVendorId();
        this.purchaseDt = lspchm.getPurchaseDt();
        this.storageId = lspchm.getStoreCd();
        this.deposits = deposits;
    }
    private String purchaseNo;
    private String purchaseVendorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date purchaseDt;
    private String storageId;
    private List<Deposit> deposits;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Deposit{
        public Deposit(Itasrt itasrt, Lsdpsp lsdpsp) {
            this.depositPlanId = lsdpsp.getDepositPlanId();
            this.purchaseNo = lsdpsp.getPurchaseNo();
            this.purchaseSeq = lsdpsp.getPurchaseSeq();
            this.assortId = lsdpsp.getAssortId();
            this.itemId = lsdpsp.getItemId();
            this.assortNm = itasrt.getAssortNm();
            this.optionNm1 = itasrt.getItvariList().get(0).getOptionNm();
            this.optionNm2 = itasrt.getItvariList().size() <= 1? null:itasrt.getItvariList().get(1).getOptionNm();
            this.availableQty = lsdpsp.getPurchasePlanQty() - lsdpsp.getPurchaseTakeQty();
            this.depositQty = 0l;
            this.extraUnitcost = lsdpsp.getLspchd().getPurchaseUnitAmt(); // 확인 필요
        }

        private String depositPlanId;
        private String purchaseNo;
        private String purchaseSeq;
        private String assortId;
        private String itemId;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long availableQty; // Lsdpsp.purchasePlanQty - Lsdpsp.purchaseTakeQty
        private Long depositQty;
        private Float extraUnitcost;
    }
}