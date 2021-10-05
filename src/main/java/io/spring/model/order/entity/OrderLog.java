package io.spring.model.order.entity;

import io.spring.model.common.entity.CommonProps;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "order_log")
public class OrderLog extends CommonProps {
    @Id
    private Long seq;
    private String orderId;
    private String orderSeq;
    private String prevStatus;
    private String currentStatus;
}
