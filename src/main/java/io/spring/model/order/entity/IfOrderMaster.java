package io.spring.model.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "if_order_master")
public class IfOrderMaster {

    @Id
    private String ifNo;
    private String channelGb = StringFactory.getGbOne(); // 01 하드코딩
    private String channelOrderNo;
    private String ifStatus; // 01 신규 02 수정
    private String channelOrderStatus;
    private String memNo;
    private String orderName;
    private String orderTel;
    private String orderEmail;
    private String orderZipcode;
    private String orderAddr1;
    private String orderAddr2;
    private String receiverName;
    private String receiverTel;
    private String receiverZipcode;
    private String receiverAddr1;
    private String receiverAddr2;
    private String channelInfo;
    private String customerId;
    private String payGb;
    private Float payAmt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date payDt;
    private String orderId;
    private String orderMemo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date orderDate;

    // 21-09-28 추가된 컬럼
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

    // 21-12-21
    private String receiverHp; // 수취자 폰
    private String orderHp; // 주문자 폰

    @Override
    public String toString(){
        return "ifNo : " + ifNo + ", channelOrderNo : " + channelOrderNo + ", ifStatus : " + ifStatus;
    }

    // ifOrderDetail 연관 관계
    @JoinColumn(name="ifNo", referencedColumnName = "ifNo", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<IfOrderDetail> ifOrderDetail;
}
