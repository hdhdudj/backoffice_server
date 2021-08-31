package io.spring.model.deposit.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
        this.purchaseVendorId = lsdpsm.getVendorId();
    }
    private String depositNo;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private String depositDt;
    private String purchaseVendorId;
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
//            this.itemGrade = lsdpsd.getItemGrade();
            this.itemId = lsdpsd.getItemId();
            this.extraUnitcost = lsdpsd.getExtraUnitcost();
            this.purchaseGb = lsdpsd.getLspchd().getLspchm().getPurchaseGb();
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
        private String assortId;
        private String itemId;
        private String purchaseGb;
        private String itemNm;
        private String optionNm1;
        private String optionNm2;

//        private String itemGrade;
        private Long depositQty;
        private Float extraUnitcost;
        private String depositStatus;
    }
}
