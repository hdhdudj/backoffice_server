package io.spring.model.purchase.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.idclass.LspchdId;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
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
@NamedEntityGraph(
    name = "Lspchd.purchaseList", attributeNodes = {
    @NamedAttributeNode("lspchm"),@NamedAttributeNode("tbOrderDetail"), @NamedAttributeNode(value="ititmm", subgraph = "ititmm_itvari")
}, subgraphs = {@NamedSubgraph(name="ititmm_itvari", attributeNodes = {@NamedAttributeNode("itvari1"), @NamedAttributeNode("itvari2")})}
)
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
        this.vendorId = lspchd.getVendorId();
    }
    public Lspchd(String purchaseNo, String purchaseSeq, PurchaseInsertRequestData.Items item){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
        this.itemGrade = StringFactory.getStrEleven();

        this.purchaseQty = item.getPurchaseQty();
        this.purchaseUnitAmt = item.getPurchaseUnitAmt();
        this.assortId = (item.getAssortId());
        this.itemId = (item.getItemId());
        this.orderId = (item.getOrderId());
        this.orderSeq = (item.getOrderSeq());
        this.siteGb = (StringFactory.getGbOne()); // 01 ????????????
        this.ownerId = (StringUtils.leftPad(StringFactory.getStrOne(), 6, '0')); // 000001 ????????????
        this.vendorId = item.getVendorId();
    }

    /**
     * ?????????????????? ????????? ???????????? ?????????
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
        this.vendorId = lsdpsd.getItasrt().getVendorId();
    }

	/**
	 * ??????????????????2 ????????? ???????????? ?????????
	 */
	public Lspchd(String purchaseNo, String purchaseSeq, Lsshpd lsshpd, TbOrderDetail tbOrderDetail) {
		this.purchaseNo = purchaseNo;
		this.purchaseSeq = purchaseSeq;
		this.assortId = lsshpd.getAssortId();
		this.itemId = lsshpd.getItemId();
		this.purchaseQty = lsshpd.getShipIndicateQty();
		this.purchaseUnitAmt = lsshpd.getSaleCost();// lsdpsd.getExtraUnitcost();
		this.purchaseItemAmt = lsshpd.getSaleCost() * lsshpd.getShipIndicateQty();// lsdpsd.getExtraCost();
		this.itemGrade = "11";
		this.siteGb = lsshpd.getSiteGb();
		this.ownerId = lsshpd.getOwnerId();
		// this.siteOrderNo = tbOrderDetail.getChannelOrderNo();
		if (tbOrderDetail != null) {
			this.orderId = tbOrderDetail.getOrderId();
			this.orderSeq = tbOrderDetail.getOrderSeq();
		}
		// this.depositNo = lsdpsd.getDepositNo();
		// this.depositSeq = lsdpsd.getDepositSeq();
        this.vendorId = lsshpd.getItasrt().getVendorId();
	}

    /**
     * ?????????????????? ????????? ???????????? ?????????
     */
    public Lspchd(String purchaseNo, String purchaseSeq, Lsshpd lsshpd, String regId){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
        this.assortId = lsshpd.getAssortId();
        this.itemId = lsshpd.getItemId();
        this.purchaseQty = lsshpd.getShipIndicateQty();
        this.purchaseUnitAmt = lsshpd.getLocalPrice();
        this.purchaseItemAmt = this.purchaseQty * this.purchaseUnitAmt;
        this.itemGrade = StringFactory.getStrEleven(); // 11 ????????????
//        this.siteGb = lsdpsd.getSiteGb();
        this.ownerId = lsshpd.getOwnerId();
        this.vendorId = lsshpd.getItasrt().getVendorId();
        super.setRegId(regId);
        super.setUpdId(regId);
    }

    /**
     * ????????????????????? ?????? ??? ?????? ???????????? ????????? ?????????
     */
    public Lspchd(TbOrderDetail tbOrderDetail, Lspchd lspchd) {
        this.assortId = tbOrderDetail.getAssortId();
        this.itemId = tbOrderDetail.getItemId();
		this.purchaseQty = tbOrderDetail.getQty(); // lspchd.getPurchaseQty();
		this.purchaseUnitAmt = lspchd.getPurchaseUnitAmt();
		this.purchaseItemAmt = (this.purchaseUnitAmt * (this.purchaseQty));
        this.itemGrade = StringFactory.getStrEleven(); // 11 ????????????
        this.siteGb = StringFactory.getGbOne(); // 01 (?????????) ????????????
        this.ownerId = lspchd.getOwnerId();
        this.vendorId = lspchd.getVendorId();
//        this.orderId = tbOrderDetail.getOrderId();
//        this.orderSeq = tbOrderDetail.getOrderSeq();
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
//    private String vendorId; : ownerId??? ?????????
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

	// todo:2021-10-14 depositNo ??? depositSeq ??? ????????????????????? ???????????? ?????????.????????? lsdpsd???
	// inputNo,inputSeq?????? ???????????????.
    private String depositNo;
    private String depositSeq;

    private String setShipId;
    private String setShipSeq;
    private String siteOrderNo;
    // 22-01-24 ??????
    private LocalDate compleDt;
    // 22-04-05 ??????
    private String vendorId;

    // ???????????? : lspchb
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Lspchb.class)
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lspchb> lspchb;

    // ???????????? : lspchm
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Lspchm.class)
    @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Lspchm lspchm;

    // ???????????? : ititmm
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Ititmm ititmm;

    // ?????? ?????? lsdpsp
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lsdpsp> lsdpsp;

	// lsdpsd ??? ???????????? ???????????? entity ????????? ????????? ???..????????? ????????????
    // lsdpsd ????????????
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lspchd")
//    @JoinColumns({
//            @JoinColumn(name = "purchaseNo", referencedColumnName = "inputNo", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
//            @JoinColumn(name = "purchaseSeq", referencedColumnName = "inputSeq", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
//    })

//    @JsonIgnore
//    @NotFound(action = NotFoundAction.IGNORE)
//	private List<Lsdpsd> lsdpsd;

    // tbOrderDetail ????????????
	@JoinColumns({
			@JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")),
			@JoinColumn(name = "orderSeq", referencedColumnName = "orderSeq", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none")) })
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private TbOrderDetail tbOrderDetail;
}
