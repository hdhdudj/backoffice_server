package io.spring.model.deposit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Setter
@Table(name="lsdpsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsp extends CommonProps implements Serializable {
    public Lsdpsp(String depositPlanId, Lspchd lspchd){

		// todo:purchaseGb 를 입력하는 부분이 추가되야함. -> 바깥에서 set으로 추가됨.

        this.depositPlanId = depositPlanId;
        this.smReservationDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchasePlanQty = lspchd.getPurchaseQty();
        this.purchaseTakeQty = 0l;
        this.assortId = lspchd.getAssortId();
        this.itemId = lspchd.getItemId();
        this.planStatus = StringFactory.getGbOne(); // 01 하드코딩
        this.purchaseNo = lspchd.getPurchaseNo();
        this.purchaseSeq = lspchd.getPurchaseSeq();
        this.claimItemYn = StringFactory.getGbTwo(); // 02 하드코딩
//		this.purchaseGb =
		this.dealtypeCd = "03";// 03 하드코딩 입고예정주문
		this.planStatus = StringFactory.getGbOne();
    }
    public Lsdpsp(PurchaseInsertRequestData purchaseInsertRequestData, PurchaseInsertRequestData.Items items){
        this.depositPlanId = purchaseInsertRequestData.getDepositPlanId();
        this.smReservationDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchasePlanQty = items.getPurchaseQty();
        this.purchaseTakeQty = 0L;
        this.assortId = items.getAssortId();
        this.itemId = items.getItemId();
        this.planStatus = purchaseInsertRequestData.getPlanStatus();
        this.orderId = purchaseInsertRequestData.getOrderId();
        this.orderSeq = purchaseInsertRequestData.getOrderSeq();
        this.purchaseNo = purchaseInsertRequestData.getPurchaseId();
        this.purchaseSeq = items.getPurchaseSeq();
        this.claimItemYn = StringFactory.getGbTwo(); // 02
		this.purchaseGb = purchaseInsertRequestData.getPurchaseGb();
		this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();
		this.planStatus = StringFactory.getGbOne();
		this.setRegId(purchaseInsertRequestData.getUserId());
		this.setUpdId(purchaseInsertRequestData.getUserId());
    }

    public Lsdpsp(String depositPlanId, Lsdpsp lsdpsp) {
        this.depositPlanId = depositPlanId;
        this.smReservationDt = lsdpsp.getSmReservationDt();
        this.assortId = lsdpsp.getAssortId();
        this.itemId = lsdpsp.getItemId();
        this.planStatus = lsdpsp.getPlanStatus();
        this.orderId = lsdpsp.getOrderId();
        this.orderSeq = lsdpsp.getOrderSeq();
        this.purchaseNo = lsdpsp.getPurchaseNo();
        this.purchaseSeq = lsdpsp.getPurchaseSeq();
        this.planChgReason = lsdpsp.getPlanChgReason();
        this.claimItemYn = lsdpsp.getClaimItemYn();
        this.purchaseGb = lsdpsp.getPurchaseGb();
        this.dealtypeCd = lsdpsp.getDealtypeCd();
		this.planStatus = StringFactory.getGbOne();
		this.lspchd = lsdpsp.getLspchd();
    }

    /**
     * 상품이동지시 저장 -> 발주 data 생성시
     */
    public Lsdpsp(String depositPlanId, Lspchd lspchd, String regId) {
		this.smReservationDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.depositPlanId = depositPlanId;
        this.purchasePlanQty = lspchd.getPurchaseQty();
        this.purchaseTakeQty = 0l;
        this.assortId = lspchd.getAssortId();
        this.itemId = lspchd.getItemId();
        this.planStatus = StringFactory.getGbOne(); // 01 : 입고전, 03 : 부분입고, 04 : 입고완료, 05 : 취소. 01 하드코딩
        this.purchaseNo = lspchd.getPurchaseNo();
        this.purchaseSeq = lspchd.getPurchaseSeq();
        this.purchaseGb = StringFactory.getGbTwo(); // 01 : 일반발주, 02 : 이동요청. 02 하드코딩
        this.dealtypeCd = StringFactory.getGbTwo(); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주. 02 하드코딩
		this.claimItemYn = StringFactory.getGbTwo(); // 02
        super.setRegId(regId);
        super.setUpdId(regId);
    }

    @Id
    private String depositPlanId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date smReservationDt;
    private Long purchasePlanQty;
    private Long purchaseTakeQty;
    private String assortId;
    private String itemId;
    private String planStatus; // 01:입고전 03:부분입고 04:입고완료 05:취소
    private String orderId;
    private String orderSeq;
    private String purchaseNo;
    private String purchaseSeq;
    private String planChgReason;
    private String claimItemYn;
	private String purchaseGb;
	private String dealtypeCd;

    // 연관관계 : lspchd
    @ManyToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName = "purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName = "purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Lspchd lspchd;

    // 연관 관계 tbOrderDetail
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "orderId", referencedColumnName="orderId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "orderSeq", referencedColumnName="orderSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private TbOrderDetail tbOrderDetail;

    // 연관 관계 itasrt
    @ManyToOne
    @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;


//    // 연관 관계 : lsdpsd
//    @OneToMany
//    @JoinColumns({
//            @JoinColumn(name = "purchaseNo", referencedColumnName="inputNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//            @JoinColumn(name = "purchaseSeq", referencedColumnName="inputSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
//    })
//    private List<Lsdpsd> lsdpsd;
}
