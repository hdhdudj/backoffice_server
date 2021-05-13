package io.spring.model.deposit.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Entity
@Getter
@Setter
@Table(name="lsdpsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsp implements Serializable {
    public Lsdpsp(PurchaseInsertRequestData purchaseInsertRequestData, PurchaseInsertRequestData.Items items){
        this.depositPlanId = purchaseInsertRequestData.getDepositPlanId();
        try{
            this.smReservationDt = new SimpleDateFormat(StringFactory.getDateFormat()).parse(StringFactory.getDoomDay());
        }
        catch(Exception e){
            log.debug(e.getMessage());
        }
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
    }
    @Id
    private String depositPlanId;
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
    private Long regId;
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;

}
