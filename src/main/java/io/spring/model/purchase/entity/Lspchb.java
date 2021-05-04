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

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchb {
    private final static Logger logger = LoggerFactory.getLogger(Lspchb.class);
    public Lspchb(PurchaseInsertRequest purchaseInsertRequest){
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.purchaseNo = purchaseInsertRequest.getPurchaseNo();
        this.purchaseSeq = purchaseInsertRequest.getPurchaseSeq();
        this.purchaseStatus = purchaseInsertRequest.getPurchaseStatus();
        this.cancelGb = StringFactory.getNinetyNine();
    }
    public Lspchb(Lspchb lspchb){
        try
        {
            this.effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        this.purchaseNo = lspchb.getPurchaseNo();
        this.purchaseSeq = lspchb.getPurchaseSeq();
        this.purchaseStatus = lspchb.getPurchaseStatus();
        this.cancelGb = StringFactory.getNinetyNine();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String purchaseNo;
    private String purchaseSeq;
    private Date effEndDt;
    @CreationTimestamp
    private Date effStaDt;
    private String purchaseStatus;
    private String cancelGb;
    private Long regId;
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;
}
