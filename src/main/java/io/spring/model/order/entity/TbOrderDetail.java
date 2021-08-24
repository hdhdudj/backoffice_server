package io.spring.model.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.order.idclass.TbOrderDetailId;
import io.spring.model.purchase.entity.Lspchd;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Table(name="tb_order_detail")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TbOrderDetailId.class)
@EqualsAndHashCode(exclude = {"ititmm"}, callSuper = false)
public class TbOrderDetail extends CommonProps
{
    public TbOrderDetail(TbOrderMaster tbOrderMaster, Ititmm ititmm){
        orderId = tbOrderMaster.getOrderId();
        orderSeq = StringFactory.getThreeStartCd(); // 001 하드코딩
        goodsNm = ititmm.getItemNm();
    }
    public TbOrderDetail(TbOrderDetail tbOrderDetail){
        orderId = tbOrderDetail.getOrderId();
        orderSeq = tbOrderDetail.getOrderSeq();
        statusCd = tbOrderDetail.getStatusCd();
        assortGb = tbOrderDetail.getAssortGb();
        assortId = tbOrderDetail.getAssortId();
        itemId = tbOrderDetail.getItemId();
        goodsNm = tbOrderDetail.getGoodsNm();
        optionInfo = tbOrderDetail.getOptionInfo();
        setGb = tbOrderDetail.getSetGb();
        setOrderId = tbOrderDetail.getSetOrderId();
        setOrderSeq = tbOrderDetail.getSetOrderSeq();
        qty = tbOrderDetail.getQty();
        itemAmt = tbOrderDetail.getItemAmt();
        goodsPrice = tbOrderDetail.getGoodsPrice();
        salePrice = tbOrderDetail.getSalePrice();
        goodsDcPrice = tbOrderDetail.getGoodsDcPrice();
        memberDcPrice = tbOrderDetail.getMemberDcPrice();
        couponDcPrice = tbOrderDetail.getCouponDcPrice();
        adminDcPrice = tbOrderDetail.getAdminDcPrice();
        dcSumPrice = tbOrderDetail.getDcSumPrice();
        deliPrice = tbOrderDetail.getDeliPrice();
        deliMethod = tbOrderDetail.getDeliMethod();
        channelOrderNo = tbOrderDetail.getChannelOrderNo();
        channelOrderSeq = tbOrderDetail.getChannelOrderSeq();
        lastGb = tbOrderDetail.getLastGb();
        lastCategoryId = tbOrderDetail.getLastCategoryId();
        storageId = tbOrderDetail.getStorageId();
        ititmm = tbOrderDetail.getItitmm();
        super.setRegDt(tbOrderDetail.getRegDt());
        super.setRegId(tbOrderDetail.getRegId());
        super.setUpdDt(tbOrderDetail.getUpdDt());
        super.setUpdId(tbOrderDetail.getUpdId());
    }
    @Id
    private String orderId;
    @Id
    private String orderSeq;

    private String statusCd;
    private String assortGb; // 01 : 직구, 02 : 수입
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
    private String channelOrderSeq; // order_status의 sno
    private String lastGb;
    private String lastCategoryId;
    private String storageId;

    @JoinColumns(
    {
        @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
        @JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    })
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Ititmm ititmm; // ititmm 연관관계

    @JoinColumn(name="orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private TbOrderMaster tbOrderMaster; // tbOrderMaster 연관관계

    @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Itasrt itasrt; // itasrt 연관관계

//    @JoinColumns({
//        @JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
//        @JoinColumn(name = "orderSeq", referencedColumnName = "orderSeq", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
//    })
    @OneToOne(fetch = FetchType.LAZY,
    mappedBy = "tbOrderDetail")
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Lspchd lspchd; // lspchd 연관관계
}
