package io.spring.model.kakaobizmessage.entity;

import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "send_message_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendMessageLog extends CommonProps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String orderId;
    private String statusCd;
    private String messageGb;
    private String custNm;
    private String custHp;
}
