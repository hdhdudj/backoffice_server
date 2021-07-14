package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="lspchb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchb extends CommonProps {
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
        this.cancelGb = StringFactory.getNinetyNine(); // 99 하드코딩
    }
    public Lspchb(Lspchd lspchd){
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.purchaseNo = lspchd.getPurchaseNo();
        this.purchaseSeq = lspchd.getPurchaseSeq();
        this.purchaseStatus = StringFactory.getGbOne(); // 01 하드코딩
        this.cancelGb = StringFactory.getNinetyNine(); // 99 하드코딩
        this.purchaseQty = lspchd.getPurchaseQty();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String purchaseNo;
    private String purchaseSeq;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStaDt;
    private String purchaseStatus;
    private String cancelGb;
    private Long purchaseQty;
}
