package io.spring.model.deposit.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertDepositEtcRequestData {
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
	// timezone = "Asia/Seoul")
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime depositDt;
    private String storageId;
	private String vendorId; // AAAAAA trdst 고정
	private String depositGb; // 기타입고 11
	private String depositType = "01"; // 입고 01
    private List<Item> items;
    // 21-12-08 추가
    private String memo;
	private String userId;


    @Getter
    @Setter
    public static class Item{
        private String assortId;
		private String itemId;
        private String itemGrade;
        private Long depositQty;
        private Float extraUnitcost;
		private String rackNo;

    }
}
