package io.spring.model.order.entity;

<<<<<<< HEAD
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_order_history")
@NoArgsConstructor
public class TbOrderHistory {

	public TbOrderHistory(String orderId, String orderSeq, String statusCd, String lastYn, Date effStartDt,
			Date effEndDt) {

		this.orderId = orderId;
		this.orderSeq = orderSeq;
		this.statusCd = statusCd;
		this.lastYn = lastYn;
		this.effStartDt = effStartDt;
		this.effEndDt = effEndDt;

	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	private String orderId;
	private String orderSeq;
	private String statusCd;
	private String lastYn;
	private Date effStartDt;
	private Date effEndDt;

	private Long regId = 1L;
	@CreationTimestamp
	private Date regDt;
	private Long updId = 1L;
	@UpdateTimestamp
	private Date updDt;

=======
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
>>>>>>> 59a8621c1562d6aa02e547bbfab4aa92d84a3e8b
}
