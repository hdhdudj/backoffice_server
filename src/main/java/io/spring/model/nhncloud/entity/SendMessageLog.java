package io.spring.model.nhncloud.entity;

import io.spring.enums.MessageType;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "send_message_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendMessageLog extends CommonProps {
    public SendMessageLog(TbOrderDetail tod, TbMember tm, MessageType mt) {
        this.orderId = tod.getOrderId();
        this.statusCd = tod.getStatusCd();
        this.messageGb = mt.getFieldName();
        this.custHp = tm.getCustHp();
        this.custNm = tm.getCustNm();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String orderId;
    private String statusCd;
    private String messageGb;
    private String custNm;
    private String custHp;

}
