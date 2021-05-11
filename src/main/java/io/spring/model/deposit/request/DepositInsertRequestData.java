package io.spring.model.deposit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DepositInsertRequestData {
    private String depositNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date depositDt;
    private String storeCd;
    private String depositStatus;
    private String depositVendorId;
    private List<Item> items;

    @Getter
    @Setter
    public static class Item{
        private String depositSeq;
        private String assortId;
        private String itemGrade;
        private String itemId;
        private Long depositQty;
        private Float extraUnitcost;
        private String depositStatus;
        public String purchaseNo;
        private String purchaseSeq;
    }
}
