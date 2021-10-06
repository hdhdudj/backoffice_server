package io.spring.model.order.entity;

import io.spring.model.common.entity.CommonProps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "order_log")
@NoArgsConstructor
public class OrderLog extends CommonProps {
    public OrderLog(TbOrderDetail tbOrderDetail){
        // 임시 코드
        super.setRegId("1");
        super.setUpdId("1");
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.prevStatus = tbOrderDetail.getStatusCd();
        this.currentStatus = tbOrderDetail.getStatusCd();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String orderId;
    private String orderSeq;
    private String prevStatus;
    private String currentStatus;
}
