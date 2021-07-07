package io.spring.model.order.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.spring.model.order.idclass.TbOrderDetailId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Getter
@Setter
@Table(name = "tb_order_detail")
@IdClass(TbOrderDetailId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderDetail {
	private final static Logger logger = LoggerFactory.getLogger(TbOrderDetail.class);

	@Id
	private String orderId;
	@Id
	private String orderSeq;

	private String statusCd;
	private String assortGb;
	private String assortId;
	private String itemId;
	private String goodsNm;
	private String optionInfo;
	private String setGb;
	private String setOrderId;
	private String setOrderSeq;
	private Long qty;
	private Float itemAmt;
	private Float goodsPrice;
	private Float salePrice;
	private Float goodsDcPrice;
	private Float memberDcPrice;
	private Float couponDcPrice;
	private Float adminDcPrice;
	private Float dcSumPrice;
	private Float deliPrice;
	private String deliMethod;
	private String channelOrderNo;
	private String channelOrderSeq;

	private String storageId;
	private String lastGb;
	private String lastCategoryId;
	
	


	private Long regId;
	@CreationTimestamp
	private Date regDt;
	private Long updId;
	@UpdateTimestamp
	private Date updDt;

}
