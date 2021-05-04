package io.spring.model.purchase.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lsdpsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsp {
    private final static Logger logger = LoggerFactory.getLogger(Lsdpsp.class);
    public Lsdpsp(PurchaseInsertRequest purchaseInsertRequest, PurchaseInsertRequest.Items items){
        this.depositPlanId = purchaseInsertRequest.getDepositPlanId();
        try{
            this.smReservationDt = new SimpleDateFormat(StringFactory.getDateFormat()).parse(StringFactory.getDoomDay());
        }
        catch(Exception e){
            logger.debug(e.getMessage());
        }
        this.purchasePlanQty = purchaseInsertRequest.getPurchasePlanQty();
        this.purchaseTakeQty = purchaseInsertRequest.getPurchaseTakeQty();
        this.assortId = items.getAssortId();
        this.itemId = items.getItemId();
        this.planStatus = purchaseInsertRequest.getPlanStatus();
//        this.orderId = purchaseInsertRequest.getOrderId();
//        this.orderSeq = purchaseInsertRequest.getOrderSeq();
        this.purchaseNo = purchaseInsertRequest.getPurchaseNo();
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
