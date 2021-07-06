package io.spring.model.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.order.idclass.TbOrderDetailId;
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
    private String channelOrderSeq; // order_status의 sno
    private String lastGb;
    private String lastCategoryId;
    private String storageId;

    @JoinColumn(name="goodsNm", referencedColumnName = "itemNm", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Ititmm ititmm; // ititmm 연관관계

//    @Override
//    public boolean equals(Object obj){
//        return this.orderId.equals(((TbOrderDetail)obj).getOrderId()) && this.orderSeq.equals(((TbOrderDetail)obj).getOrderSeq()) && this.statusCd.equals(((TbOrderDetail)obj).getStatusCd()) && this.assortGb.equals(((TbOrderDetail)obj).getAssortGb()) && this.assortId.equals(((TbOrderDetail)obj).getAssortId()) && this.itemId.equals(((TbOrderDetail)obj).getItemId()) && this.goodsNm.equals(((TbOrderDetail)obj).getGoodsNm()) && this.optionInfo.equals(((TbOrderDetail)obj).getOptionInfo()) && this.setGb.equals(((TbOrderDetail)obj).getSetGb()) && this.setOrderId.equals(((TbOrderDetail)obj).getSetOrderId()) && this.setOrderSeq.equals(((TbOrderDetail)obj).getSetOrderSeq()) && this.qty == ((TbOrderDetail)obj).getQty() && this.itemAmt == ((TbOrderDetail)obj).getItemAmt() && this.goodsPrice == ((TbOrderDetail)obj).getGoodsPrice() && this.salePrice == ((TbOrderDetail)obj).getSalePrice() && this.goodsDcPrice == ((TbOrderDetail)obj).getGoodsDcPrice() && this.memberDcPrice == ((TbOrderDetail)obj).getMemberDcPrice() && this.couponDcPrice == ((TbOrderDetail)obj).getCouponDcPrice() && this.adminDcPrice == ((TbOrderDetail)obj).getAdminDcPrice() && this.dcSumPrice == ((TbOrderDetail)obj).getDcSumPrice() && this.deliPrice == ((TbOrderDetail)obj).getDeliPrice() && this.deliMethod.equals(((TbOrderDetail)obj).getDeliMethod()) && this.channelOrderNo.equals(((TbOrderDetail)obj).getChannelOrderNo()) && this.channelOrderSeq.equals(((TbOrderDetail)obj).getChannelOrderSeq()) && this.lastGb.equals(((TbOrderDetail)obj).getLastGb()) && this.lastCategoryId.equals(((TbOrderDetail)obj).getLastCategoryId()) && this.storageId.equals(((TbOrderDetail)obj).getStorageId());
//    }
}
