package io.spring.model.order.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderMaster {

	@Id
	private String orderId;

	private String firstOrderId;
	private Date orderDate;
	private String orderStatus;
	private String channelGb;
	private long custId;
	private long deliId;
	private float orderAmt;
	private float receiptAmt;
	private String firstOrderGb;
	private String orderGb;
	private String channelOrderNo;
	private String custPcode;
	private String orderMemo;

	private Long regId;
	@CreationTimestamp
	private Date regDt;
	private Long updId;
	@UpdateTimestamp
	private Date updDt;

}
