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
    private String vendorId;
    private List<Item> items;
    // 21-12-08 추가
    private String memo;

    @Getter
    @Setter
    public static class Item{
        private String purchaseNo;
        private String purchaseSeq;
        private String depositSeq;
        private String assortId;
        private String itemGrade;
        private String itemId;
        private Long depositQty;
        private Float extraUnitcost;
        private String depositStatus;
    }
}
