package io.spring.model.order.idclass;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class TbOrderDetailId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderSeq;
}
