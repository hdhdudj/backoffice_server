package io.spring.model.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="tb_order_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderHistory extends CommonProps {
    public TbOrderHistory(TbOrderDetail tbOrderDetail){
        this.orderId = tbOrderDetail.getOrderId();
        orderSeq = tbOrderDetail.getOrderSeq();//StringUtils.leftPad(StringFactory.getStrOne(), 3,'0'); // 001 하드코딩
        lastYn = StringUtils.leftPad(StringFactory.getStrTwo(), 3,'0'); // 002 하드코딩
        effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String orderId;
    private String orderSeq;
    private String statusCd;
    private String lastYn;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStartDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
}
