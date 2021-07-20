package io.spring.model.deposit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flywaydb.core.internal.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsdpsm")
public class Lsdpsm extends CommonProps {
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

    public Lsdpsm(String depositNo, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        this.depositNo = depositNo;
        this.depositDt = new Date();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.depositGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = depositListWithPurchaseInfoData.getPurchaseVendorId();
        this.finishYymm = Utilities.getStringToDate(StringFactory.getDoomDay()); // 9999-12-31 하드코딩
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
        this.storeCd = depositListWithPurchaseInfoData.getStorageId();
//        this.depositVendorId = deposit.
    }

    @Id
    private String depositNo;
    private Date depositDt;
    private String depositGb;
    private String siteGb;
    private String vendorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date finishYymm;
    private String depositType;
    private String storeCd;
    private String depositVendorId;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depositVendorId", referencedColumnName="vendorId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Cmvdmr cmvdmr;

}
