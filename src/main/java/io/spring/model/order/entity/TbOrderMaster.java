package io.spring.model.order.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderMaster extends CommonProps {
    public TbOrderMaster(String orderId){
        this.orderId = orderId;
    }
    @Id
    private String orderId;
    private String firstOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;
    private String orderStatus;
    private String channelGb;
    private Long custId;
    private Long deliId;
    private Float orderAmt;
    private Float receiptAmt;
    private String firstOrderGb;
    private String orderGb;
    private String channelOrderNo;
    private String custPcode;
    private String orderMemo;
    
    // 21-09-28 추가된 컬럼
    private String payGb; // 결제방법

	// 21-10-07 추가된 컬럼
	private Float totalGoodsPrice;
	private Float totalDeliveryCharge;
	private Float totalGoodsDcPrice;
	private Float totalMemberDcPrice;
	private Float totalMemberOverlapDcPrice;
	private Float totalCouponGoodsDcPrice;
	private Float totalCouponOrderDcPrice;
	private Float totalCouponDeliveryDcPrice;
	private Float totalMileage;
	private Float totalGoodsMileage;
	private Float totalMemberMileage;
	private Float totalCouponGoodsMileage;
	private Float totalCouponOrderMileage;

    // 21-12-17 추가된 컬럼
    private String orderName;
    private String orderAddr1;
    private String orderAddr2;
    // 21-12-21 추가된 컬럼
    private String orderZipcode;
    private String orderZonecode;
    private String orderTel; // 주문자 전화
    private String orderHp; // 주문자 폰

    @JoinColumn(name = "custId", referencedColumnName = "custId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
//    @NotFound(action = NotFoundAction.IGNORE)
    private TbMember tbMember; // tbMember 연관관계

    @JoinColumn(name = "deliId", referencedColumnName = "deliId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
//    @NotFound(action = NotFoundAction.IGNORE)
    private TbMemberAddress tbMemberAddress; // tbMemberAddress 연관관계
}
