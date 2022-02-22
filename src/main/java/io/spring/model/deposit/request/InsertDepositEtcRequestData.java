package io.spring.model.deposit.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertDepositEtcRequestData {
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
	// timezone = "Asia/Seoul")

	@NotNull(message = "depositDt는 필수 값입니다.")
	@Pattern(regexp = "^([0-9]{4})-([0-1][0-9])-([0-3][0-9])\\s([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9])$", message = "depositDt는 형식을 확인하시기바랍니다.")
//	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private String depositDt;
	@NotNull(message = "storageId는 필수 값입니다.")
    private String storageId;

	@NotNull(message = "vendorId는 필수 값입니다.")
	private String vendorId = "AAAAAA"; // AAAAAA trdst 고정
	@NotNull(message = "depositGb는 필수 값입니다.")
	private String depositGb; // 기타입고 11

	private String depositType = "01"; // 입고 01
	@NotNull(message = "items는 필수 값입니다.")
    private List<Item> items;
    // 21-12-08 추가

    private String memo;
	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;


    @Getter
    @Setter
    public static class Item{
		@NotNull(message = "assortId는 필수 값입니다.")
        private String assortId;
		@NotNull(message = "itemId는 필수 값입니다.")
		private String itemId;
		@NotNull(message = "itemGrade는 필수 값입니다.")
        private String itemGrade;
		@NotNull(message = "depositQty는 필수 값입니다.")
        private Long depositQty;
		@NotNull(message = "extraUnitcost는 필수 값입니다.")
        private Float extraUnitcost;
		@NotNull(message = "rackNo는 필수 값입니다.")
		private String rackNo;

    }
}
