package io.spring.model.ship.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertShipEtcRequestData {
	@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private LocalDateTime shipDt;
	private String storageId;
	private String vendorId = "AAAAAA"; // AAAAAA trdst 고정
	private String depositGb; // 기타출고 21
	private String depositType = "02"; // 출고 02
	private List<Item> items;
	// 21-12-08 추가
	private String memo;
	private String userId;

	@Getter
	@Setter
	public static class Item {
		private String assortId;
		private String itemId;
		private String itemGrade;
		private Long shipQty;
		private Float extraUnitcost;
		private String rackNo;
		@JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
		private LocalDateTime effStaDt;

	}
}
