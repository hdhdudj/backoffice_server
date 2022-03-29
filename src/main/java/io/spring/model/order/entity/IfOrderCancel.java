package io.spring.model.order.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "if_order_cancel")
public class IfOrderCancel extends CommonProps {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String seq;

	private String ifNo;
	private String ifNoSeq;
	private String channelOrderNo;
	private String channelOrderSeq;
	private String channelOrderStatus;
	private String channelGoodsType;
	private String channelGoodsNo;
	private String channelOptionsNo;
	private String channelParentGoodsNo;
	private String channelGoodsNm;
	private String channelOptionInfo;
	private Long goodsCnt;
	private Float goodsPrice;
	private Float goodsDcPrice;
	private Float memberDcPrice;
	private Float couponDcPrice;
	private Float adminDcPrice;
	private Float etcDcPrice;
	private String deliveryMethodGb;
	private Float deliPrice;
	private String orderId;
	private String orderSeq;
	private String channelGb = StringFactory.getGbOne(); // 01 하드코딩

	// 21-09-28 추가된 컬럼
	private String goodsModelNo;
	private Float divisionUseMileage;
	private Float divisionGoodsDeliveryUseDeposit;
	private Float divisionGoodsDeliveryUseMileage;
	private Float divisionCouponOrderDcPrice;
	private Float divisionUseDeposit;
	private Float divisionCouponOrderMileage;
	private Float addGoodsPrice;
	private String optionTextInfo;
	private String listImageData;
	private Float optionPrice;
	private Float optionTextPrice;
	private Float fixedPrice;
	private String deliveryInfo;
	private Float costPrice;
	private Float memberOverlapDcPrice;
	private Long scmNo;

	// 21-10-06 추가된 컬럼
	private String parentChannelOrderSeq;

	// 21-10-13 추가된 컬럼
	private String claimHandleMode;
	private String claimHandleReason;
	private String claimHandleDetailReason;

	private String ifStatus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date ifDt;
	private String ifMsg;
	private String ifCancelGb;

}
