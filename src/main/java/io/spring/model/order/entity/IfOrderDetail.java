package io.spring.model.order.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.order.idclass.IfOrderDetailId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "if_order_detail")
@IdClass(value = IfOrderDetailId.class)
public class IfOrderDetail {
    @Id
    private String ifNo;
    @Id
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
    private Float goodsModelNo;
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
}
