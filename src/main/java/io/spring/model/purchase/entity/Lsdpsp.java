package io.spring.model.purchase.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lsdpsp")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsp {
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
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
