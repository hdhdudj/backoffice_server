package io.spring.model.order.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CancelOrderRequestData {

	@NotNull(message = "userId는 필수 값입니다.")
	private String userId;
	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		private String channelGb; // 취소채널 01 고도몰
		private String orderId;
		private String orderSeq;
		private String cancelGb;
		private String cancelMsg;
		private String seq;

	}

}
