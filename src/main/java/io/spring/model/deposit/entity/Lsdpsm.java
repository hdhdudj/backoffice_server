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
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="lsdpsm")
public class Lsdpsm extends CommonProps {
    public Lsdpsm(DepositInsertRequestData depositInsertRequestData){
        this.depositNo = depositInsertRequestData.getDepositNo();
        this.depositDt = depositInsertRequestData.getDepositDt();
        this.storeCd = depositInsertRequestData.getStorageId();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.depositGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = StringUtils.leftPad("1",6,'0');
        this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDayT());
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
        this.depositVendorId = depositInsertRequestData.getDepositVendorId();
    }

    public Lsdpsm(String depositNo, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        this.vendorId = depositListWithPurchaseInfoData.getPurchaseVendorId();
        this.depositNo = depositNo;
        this.depositDt = Utilities.dateToLocalDateTime(Utilities.getStringToDate(depositListWithPurchaseInfoData.getDepositDt()));//new Date();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.depositGb = StringFactory.getGbOne(); // 01 하드코딩
        this.vendorId = depositListWithPurchaseInfoData.getPurchaseVendorId();
        this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDayT()); // 9999-12-31 하드코딩
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
        this.storeCd = depositListWithPurchaseInfoData.getStorageId();
        this.setRegId(depositListWithPurchaseInfoData.getRegId());
//        this.depositVendorId = deposit.
    }

    @Id
    private String depositNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime depositDt;
    private String depositGb;
    private String siteGb;
    private String vendorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime finishYymm;
    private String depositType;
    private String storeCd;
    private String depositVendorId;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId", referencedColumnName="vendorId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Cmvdmr cmvdmr;

}
