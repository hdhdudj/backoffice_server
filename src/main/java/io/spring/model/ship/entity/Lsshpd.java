package io.spring.model.ship.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.move.request.GoodsMoveSaveData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.idclass.LsshpdId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsshpd")
@IdClass(value = LsshpdId.class)
public class Lsshpd extends CommonProps {
    // 주문 이동지시 저장시 작동하는 생성자
    public Lsshpd(String shipId, String shipSeq, TbOrderDetail tbOrderDetail, Ititmc ititmc, Itasrt itasrt){
        this.shipId = shipId;
        this.shipSeq = shipSeq;
        this.assortId = tbOrderDetail.getAssortId();
        this.itemId = tbOrderDetail.getItemId();
        this.shipVendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.shipIndicateQty = 1l;
        this.shipQty = 0l;
//        this.vendorDealCd = lsdpsp.getDealtypeCd(); 바깥에서 set
        this.vatGb = StringFactory.getGbOne(); // 01 하드코딩
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.shipGb = StringFactory.getGbTwo(); // 01 출고 02 이동
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.rackNumber = null;
        this.customsTax = 0f;
        this.excAppDt = ititmc.getEffEndDt();
        this.orderDiscount = tbOrderDetail.getSalePrice();
        this.saleCost = ititmc.getStockAmt();
        this.localPrice = ititmc.getStockAmt();
        this.localDeliFee = 0f;
        this.localTax = 0f;
        this.disPrice = 0f;
        this.oStorageId = tbOrderDetail.getStorageId();
    }
    // 상품이동지시 저장시 작동하는 생성자
    public Lsshpd(String shipId, String shipSeq, GoodsMoveSaveData goodsMoveSaveData, GoodsMoveSaveData.Goods goods) {
        this.shipId = shipId;
        this.shipSeq = shipSeq;
        this.assortId = goods.getAssortId();
        this.itemId = goods.getItemId();
        this.shipVendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.shipIndicateQty = 1l;
        this.shipQty = 0l;
        this.vendorDealCd = StringFactory.getGbTwo(); // 01 : 주문, 02 : 상품, 03 : 입고예정 (02 하드코딩)
        this.vatGb = StringFactory.getGbOne(); // 01 하드코딩
        this.shipGb = StringFactory.getGbTwo(); // 01 출고 02 이동
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.rackNumber = null;
        this.customsTax = 0f;
//        this.excAppDt = ititmc.getEffStaDt();
//        this.orderDiscount = tbOrderDetail.getSalePrice();
//        this.saleCost = ititmc.getStockAmt();
//        this.localPrice = ititmc.getStockAmt();
        this.localDeliFee = 0f;
        this.localTax = 0f;
        this.disPrice = 0f;
//        this.oStorageId = tbOrderDetail.getStorageId();
    }
    @Id
    private String shipId;
    @Id
    private String shipSeq;
    private String assortId;
    private String itemId;
    private String shipVendorId;
    private Long shipIndicateQty;
    private Long shipQty;
    private String vendorDealCd;
    private String vatGb;
    private String orderId;
    private String orderSeq;
    private String shipGb;
    private String siteGb;
    private String vendorId;
    private String rackNumber;
    private Float customsTax;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date excAppDt;
    private Float orderDiscount;
    private Float saleCost;
    private Float localPrice;
    private Float localDeliFee;
    private Float localTax;
    private Float disPrice;
    private String oStorageId;

    // 연관관계 : lsshpm
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Lsshpm.class)
    @JoinColumn(name = "shipId", referencedColumnName="shipId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Lsshpm lsshpm;

    // 연관관계 : tbOrderDetail
    @OneToOne(fetch = FetchType.LAZY, targetEntity = TbOrderDetail.class)
    @JoinColumns({
        @JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
        @JoinColumn(name = "orderSeq", referencedColumnName = "orderSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    })
    private TbOrderDetail tbOrderDetail;
}
