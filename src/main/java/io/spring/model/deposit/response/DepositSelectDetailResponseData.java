package io.spring.model.deposit.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DepositSelectDetailResponseData {
    public DepositSelectDetailResponseData(Lsdpsm lsdpsm){
        this.depositNo = lsdpsm.getDepositNo();
        this.depositDt = lsdpsm.getDepositDt();
//        this.storeCd = lsdpsm.getStoreCd();
        this.purchaseVendorId = lsdpsm.getVendorId();
    }
    private String depositNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date depositDt;
    private String purchaseVendorId;
//    private String storeCd;
//    private String depositStatus;
//    private String depositVendorId;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Item{
        public Item(Lsdpsd lsdpsd){
            // lsdpsd
            this.depositNo = lsdpsd.getDepositNo();
            this.depositSeq = lsdpsd.getDepositSeq();
            this.assortId = lsdpsd.getAssortId();
//            this.itemGrade = lsdpsd.getItemGrade();
            this.itemId = lsdpsd.getItemId();
            this.extraUnitcost = lsdpsd.getExtraUnitcost();
        }
        private String depositNo;
        private String depositSeq;
        public String purchaseNo;
        private String purchaseSeq;
        private String assortId;
        private String itemId;
        private String itemNm;
        private String optionNm1;
        private String optionNm2;

//        private String itemGrade;
        private Long depositQty;
        private Float extraUnitcost;
        private String depositStatus;
    }
}
