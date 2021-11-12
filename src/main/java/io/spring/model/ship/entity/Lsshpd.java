package io.spring.model.ship.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import io.spring.model.deposit.entity.Lsdpsd;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsshpd")
@IdClass(value = LsshpdId.class)
public class Lsshpd extends CommonProps implements Serializable {
    // 주문 이동지시 저장시 작동하는 생성자
    public Lsshpd(String shipId, String shipSeq, TbOrderDetail tbOrderDetail, Ititmc ititmc, Itasrt itasrt){
        this.shipId = shipId;
        this.shipSeq = shipSeq;
        this.assortId = tbOrderDetail.getAssortId();
        this.itemId = tbOrderDetail.getItemId();
        this.ownerId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.shipIndicateQty = 1l;
        this.shipQty = 0l;
//        this.vendorDealCd = lsdpsp.getDealtypeCd(); 바깥에서 set
        this.vatGb = StringFactory.getGbOne(); // 01 하드코딩
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.channelId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
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
        this.shipGb = StringFactory.getGbThree(); // 01:일반출고 03:주문이동지시 04:상품이동지시
    }
    // 상품이동지시 저장시 작동하는 생성자
    public Lsshpd(String shipId, String shipSeq, Ititmc ititmc, GoodsMoveSaveData.Goods goods, String userId) {
        this.shipId = shipId;
        this.shipSeq = shipSeq;
        this.assortId = goods.getAssortId();
        this.itemId = goods.getItemId();
        this.ownerId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
//        this.shipIndicateQty = 1l; : 바깥에서 set
        this.shipQty = 0l;
        this.vendorDealCd = StringFactory.getGbTwo(); // 01 : 주문, 02 : 상품, 03 : 입고예정 (02 하드코딩)
        this.vatGb = StringFactory.getGbOne(); // 01 하드코딩
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.channelId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
        this.rackNumber = null;
        this.customsTax = 0f;
        this.excAppDt = ititmc.getEffStaDt(); // ititmc의 effEndDt를 lsshpd.excAppDt로
//        this.orderDiscount = tbOrderDetail.getSalePrice();
		this.saleCost = goods.getCost();
        this.localPrice = goods.getCost();
        this.localDeliFee = 0f;
        this.localTax = 0f;
        this.disPrice = 0f;
//        this.oStorageId = tbOrderDetail.getStorageId(); : 바깥에서 set
        this.shipGb = StringFactory.getGbFour(); // 01:일반출고 03:주문이동지시 04:상품이동지시

        super.setRegId(userId);
        super.setUpdId(userId);
    }
    @Id
    private String shipId;
    @Id
    private String shipSeq;
    private String assortId;
    private String itemId;
//    private String shipVendorId : ownerId로 변경됨
    private String ownerId;
    private Long shipIndicateQty;
    private Long shipQty;
    private String vendorDealCd;
    private String vatGb;
    private String orderId;
    private String orderSeq;
    private String shipGb;
    private String siteGb;
//    private String vendorId; : channelId로 변경됨
    private String channelId;
    private String rackNumber;
    private Float customsTax;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime excAppDt;
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

    @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Itasrt itasrt; // itasrt 연관관계
//
//    // 연관관계 : Lspchd
//    @OneToMany(fetch = FetchType.LAZY, targetEntity = Lspchd.class)
//    @JoinColumns({
//            @JoinColumn(name = "depositId", referencedColumnName = "depositId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//            @JoinColumn(name = "depositSeq", referencedColumnName = "depositSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//    })
//    private List<Lspchd> lspchdList;
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    })
    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Lsdpsd> lsdpsdList; // itasrt 연관관계
}
