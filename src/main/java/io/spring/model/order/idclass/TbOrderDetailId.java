package io.spring.model.order.idclass;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderDetailId implements Serializable {

	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	public TbOrderDetailId(String orderId, String orderSeq) {
		this.orderId = orderId;
		this.orderSeq = orderSeq;
	}

	private String orderId;
	private String orderSeq;

}
