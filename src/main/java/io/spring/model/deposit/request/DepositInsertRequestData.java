package io.spring.model.deposit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DepositInsertRequestData {
    private String depositNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime depositDt;
    private String storageId;
    private String depositStatus;
//    private String depositVendorId; : ownerId로 변경됨
    private String ownerId;
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
