package io.spring.model.order.response;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.util.Utilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class CancelOrderListResponse {

	private List<Item> items;

	@Getter
	@Setter
	public static class Item {

		public Item(HashMap<String, Object> p) {

			this.seq = p.get("seq").toString();

			this.ifDt = java.sql.Timestamp.valueOf((LocalDateTime) p.get("ifDt"));

			this.channelGb = p.get("channelGb").toString();
			this.orderId = p.get("orderId").toString();
			this.orderSeq = p.get("orderSeq").toString();
			this.orderName = p.get("orderName").toString();
			this.channelOrderNo = p.get("channelOrderNo").toString();
			this.channelOrderSeq = p.get("channelOrderSeq").toString();
			this.assortId = p.get("assortId") == null ? null : p.get("assortId").toString();
			this.itemId = p.get("itemId") == null ? null : p.get("itemId").toString();
			this.goodsNm = p.get("goodsNm") == null ? null : p.get("goodsNm").toString();
			this.optionNm1 = p.get("optionNm1") == null ? null : p.get("optionNm1").toString();
			this.optionNm2 = p.get("optionNm2") == null ? null : p.get("optionNm2").toString();
			this.optionNm3 = p.get("optionNm3") == null ? null : p.get("optionNm3").toString();
			this.ifStatus = p.get("ifStatus").toString();
			this.ifMsg = p.get("ifMsg") == null ? null : p.get("ifMsg").toString();
			this.statusCd = p.get("statusCd").toString();
			this.statusName = Utilities.convertEnumToFieldName(TrdstOrderStatus.values(), this.statusCd);
		}

		private String seq; // 순번

//	    @JsonSerialize(using = LocalDateTimeSerializer.class)
		// CustomLocalDateTimeDeSerializer

//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
		// @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
		private Date ifDt; // 연동일자
		private String channelGb; // 채널구분
	private String orderId; // 주문번호
	private String orderSeq; // 주문순번
	private String assortId; // 품목코드
	private String orderName; // 주문자명
	private String channelOrderNo;
	private String channelOrderSeq;
	private String itemId; // 상품코드
	private String goodsNm; // 상품명
	private String optionInfo; // 제휴옵션정보
	private String optionNm1; // 옵션1
	private String optionNm2; // 옵션2
	private String optionNm3; // 옵션3
	private String ifStatus; // 처리상태
	private String ifMsg; // 연동메세지
	private String statusCd;
	private String statusName;

}

}
