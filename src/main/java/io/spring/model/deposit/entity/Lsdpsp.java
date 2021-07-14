package io.spring.model.deposit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
=======

>>>>>>> 4f298fa536f30ff74a848e9e7678e6c47a295810
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Slf4j
@Entity
@Getter
@Setter
@Table(name="lsdpsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsp extends CommonProps implements Serializable {
    public Lsdpsp(String depositPlanId, Lspchd lspchd){

		// todo:purchaseGb 를 입력하는 부분이 추가되야함.

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
		// this.purchaseGb =
		this.dealtypeCd = "03";// 03 하드코딩 입고예정주문
    }
    public Lsdpsp(PurchaseInsertRequestData purchaseInsertRequestData, PurchaseInsertRequestData.Items items){
        this.depositPlanId = purchaseInsertRequestData.getDepositPlanId();
        this.smReservationDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchasePlanQty = items.getPurchaseQty();
        this.purchaseTakeQty = 0L;
        this.assortId = items.getAssortId();
        this.itemId = items.getItemId();
        this.planStatus = purchaseInsertRequestData.getPlanStatus();
//        this.orderId = purchaseInsertRequest.getOrderId();
//        this.orderSeq = purchaseInsertRequest.getOrderSeq();
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseSeq = items.getPurchaseSeq();
        this.claimItemYn = StringFactory.getGbTwo(); // 02
		this.purchaseGb = purchaseInsertRequestData.getPurchaseGb();
		this.dealtypeCd = purchaseInsertRequestData.getDealtypeCd();

    }
    @Id
    private String depositPlanId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date smReservationDt;
    private Long purchasePlanQty;
    private Long purchaseTakeQty;
    private String assortId;
    private String itemId;
    private String planStatus;
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
}
