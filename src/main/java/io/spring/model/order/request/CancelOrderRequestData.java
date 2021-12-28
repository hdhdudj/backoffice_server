package io.spring.model.order.request;

import java.util.List;

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

	private String orderId;
	private String userId;
	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		private String orderId;
		private String orderSeq;
		private String cancelGb;
		private String cancelMsg;
		private Long cancelQty;
	}

}
