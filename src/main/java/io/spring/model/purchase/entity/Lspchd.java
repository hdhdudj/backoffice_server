package io.spring.model.purchase.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.idclass.LspchdId;
import io.spring.model.ship.entity.Lsshpd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="lspchd")
@IdClass(value = LspchdId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchd extends CommonProps implements Serializable {
    public Lspchd(Lspchd lspchd, String purchaseSeq){
        this.purchaseNo = lspchd.getPurchaseNo();
        this.purchaseSeq = purchaseSeq;
        this.assortId = lspchd.getAssortId();
        this.itemId = lspchd.getItemId();
        this.purchaseQty = lspchd.getPurchaseQty();
        this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
        this.purchaseItemAmt = lspchd.getPurchaseItemAmt();
        this.itemGrade = lspchd.getItemGrade();
        this.vatGb = lspchd.getVatGb();
        this.setGb = lspchd.getSetGb();
        this.mailsendYn = lspchd.getMailsendYn();
        this.memo = lspchd.getMemo();
        this.siteGb = lspchd.getSiteGb();
        this.ownerId = lspchd.getOwnerId();
        this.raNo = lspchd.getRaNo();
        this.itemAmt = lspchd.getItemAmt();
        this.newItemAmt = lspchd.getNewItemAmt();
        this.transAmt = lspchd.getTransAmt();
        this.newTransAmt = lspchd.getNewTransAmt();
        this.taxAmt = lspchd.getTaxAmt();
        this.newTaxAmt = lspchd.getNewTaxAmt();
        this.saleAmt = lspchd.getSaleAmt();
        this.newSaleAmt = lspchd.getNewSaleAmt();
        this.orderId = lspchd.getOrderId();
        this.orderSeq = lspchd.getOrderSeq();
        this.depositNo = lspchd.getDepositNo();
        this.depositSeq = lspchd.getDepositSeq();
        this.setShipId = lspchd.getSetShipId();
        this.setShipSeq = lspchd.getSetShipSeq();
        this.siteOrderNo = lspchd.getSiteOrderNo();
    }
    public Lspchd(String purchaseNo, String purchaseSeq){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
        this.itemGrade = StringFactory.getStrEleven();
    }

    /**
     * 주문이동지시 저장시 실행되는 생성자
     */
    public Lspchd(String purchaseNo, String purchaseSeq, Lsdpsd lsdpsd,
                  TbOrderDetail tbOrderDetail){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
        this.assortId = lsdpsd.getAssortId();
        this.itemId = lsdpsd.getItemId();
        this.purchaseQty = tbOrderDetail.getQty();
        this.purchaseUnitAmt = lsdpsd.getExtraUnitcost();
        this.purchaseItemAmt = lsdpsd.getExtraCost();
        this.itemGrade = lsdpsd.getItemGrade();
        this.siteGb = lsdpsd.getSiteGb();
        this.ownerId = lsdpsd.getOwnerId();
		// this.siteOrderNo = tbOrderDetail.getChannelOrderNo();
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
		// this.depositNo = lsdpsd.getDepositNo();
		// this.depositSeq = lsdpsd.getDepositSeq();
    }
    /**
     * 상품이동지시 저장시 실행되는 생성자
     */
    public Lspchd(String purchaseNo, String purchaseSeq, Lsshpd lsshpd, String regId){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
        this.assortId = lsshpd.getAssortId();
        this.itemId = lsshpd.getItemId();
        this.purchaseQty = lsshpd.getShipIndicateQty();
        this.purchaseUnitAmt = lsshpd.getLocalPrice();
        this.purchaseItemAmt = this.purchaseQty * this.purchaseUnitAmt;
        this.itemGrade = StringFactory.getStrEleven(); // 11 하드코딩
//        this.siteGb = lsdpsd.getSiteGb();
        this.ownerId = lsshpd.getOwnerId();
        super.setRegId(regId);
        super.setUpdId(regId);
    }
    @Id
    private String purchaseNo;
    @Id
    private String purchaseSeq;
    private String assortId;
    private String itemId;
    private Long purchaseQty;
    private Float purchaseUnitAmt;
    private Float purchaseItemAmt;
    private String itemGrade;
    private String vatGb;
    private String setGb;
    private String mailsendYn;
    private String memo;
    private String siteGb;
//    private String vendorId; : ownerId로 변경됨
    private String ownerId;
    private String raNo;
    private Float itemAmt;
    private Float newItemAmt;
    private Float transAmt;
    private Float newTransAmt;
    private Float taxAmt;
    private Float newTaxAmt;
    private Float saleAmt;
    private Float newSaleAmt;
    @Column(name = "orderId")
    private String orderId;
    @Column(name = "orderSeq")
    private String orderSeq;

	// todo:2021-10-14 depositNo 와 depositSeq 는 부분입고떄문에 들어가면 안될듯.반대로 lsdpsd의
	// inputNo,inputSeq에서 관리해야함.
    private String depositNo;
    private String depositSeq;

    private String setShipId;
    private String setShipSeq;
    private String siteOrderNo;

    // 연관관계 : lspchb
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Lspchb.class)
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lspchb> lspchb;

    // 연관관계 : lspchm
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Lspchm.class)
    @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Lspchm lspchm;

    // 연관관계 : ititmm
    @OneToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Ititmm ititmm;

    // 연관 관계 lsdpsp
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lsdpsp> lsdpsp;

    // lsdpsd 연관관계

    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName = "inputNo", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName = "inputSeq", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    })
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "lspchd")
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
	private Lsdpsd lsdpsd;

    // tbOrderDetail 연관관계
	@JoinColumns({
			@JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
			@JoinColumn(name = "orderSeq", referencedColumnName = "orderSeq", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")) })
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	private TbOrderDetail tbOrderDetail;
}
