package io.spring.model.purchase.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchb extends CommonProps {
    private final static Logger logger = LoggerFactory.getLogger(Lspchb.class);
    public Lspchb(PurchaseInsertRequestData purchaseInsertRequestData){
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        this.purchaseNo = purchaseInsertRequestData.getPurchaseNo();
        this.purchaseSeq = purchaseInsertRequestData.getPurchaseSeq();
        this.purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();
        this.cancelGb = StringFactory.getNinetyNine();
    }
    public Lspchb(Lspchb lspchb){
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseNo = lspchb.getPurchaseNo();
        this.purchaseSeq = lspchb.getPurchaseSeq();
        this.purchaseStatus = lspchb.getPurchaseStatus();
        this.cancelGb = StringFactory.getNinetyNine();
    }
    public Lspchb(Lspchd lspchd){
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseNo = lspchd.getPurchaseNo();
        this.purchaseSeq = lspchd.getPurchaseSeq();
        this.purchaseStatus = StringFactory.getGbOne(); // 01 하드코딩
        this.cancelGb = StringFactory.getNinetyNine();
        this.purchaseQty = lspchd.getPurchaseQty();
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
    private Long purchaseQty;
}
