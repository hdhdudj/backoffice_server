package io.spring.model.order.idclass;

import java.io.Serializable;

<<<<<<< HEAD
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

=======
public class TbOrderDetailId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderSeq;
>>>>>>> 59a8621c1562d6aa02e547bbfab4aa92d84a3e8b
}
