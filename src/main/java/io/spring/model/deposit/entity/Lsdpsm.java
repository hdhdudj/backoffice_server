package io.spring.model.deposit.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.request.DepositInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flywaydb.core.internal.util.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsdpsm")
public class Lsdpsm {
    public Lsdpsm(DepositInsertRequestData depositInsertRequestData){
        this.depositNo = depositInsertRequestData.getDepositNo();
        this.depositDt = depositInsertRequestData.getDepositDt();
        this.storeCd = depositInsertRequestData.getStoreCd();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.depositGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad("1",6,'0');
        this.finishYymm = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
        this.depositVendorId = depositInsertRequestData.getDepositVendorId();
    }

    @Id
    private String depositNo;
    private Date depositDt;
    private String depositGb;
    private String siteGb;
    private String vendorId;
    private Date finishYymm;
    private String depositType;
    private String storeCd;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
    private String depositVendorId;
}
