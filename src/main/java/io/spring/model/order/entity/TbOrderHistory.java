package io.spring.model.order.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import jdk.vm.ci.meta.Local;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_order_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderHistory extends CommonProps {
	public TbOrderHistory(String orderId, String orderSeq, String statusCd, String lastYn, LocalDateTime effStartDt,
                          LocalDateTime effEndDt) {

		this.orderId = orderId;
		this.orderSeq = orderSeq;
		this.statusCd = statusCd;
		this.lastYn = lastYn;
		this.effStartDt = effStartDt;
		this.effEndDt = effEndDt;
	}

    public TbOrderHistory(TbOrderDetail tbOrderDetail){
        this.orderId = tbOrderDetail.getOrderId();
        orderSeq = tbOrderDetail.getOrderSeq();//StringUtils.leftPad(StringFactory.getStrOne(), 3,'0'); // 001 하드코딩
        lastYn = StringUtils.leftPad(StringFactory.getStrOne(), 3,'0'); // 002 하드코딩
        effEndDt = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
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
    private LocalDateTime effStartDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
}
