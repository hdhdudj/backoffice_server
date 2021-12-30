package io.spring.model.purchase.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PurchaseCancelRequestData {

	private String orderId;
	private String orderSeq;
	private String cancelGb;
	private String cancelMsg;

}
