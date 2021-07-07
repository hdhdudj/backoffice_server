package io.spring.model.order.entity;

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

}
