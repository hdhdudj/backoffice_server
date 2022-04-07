package io.spring.model.deposit.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.request.InsertDepositEtcRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.ship.request.InsertShipEtcRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        this.vendorId = depositInsertRequestData.getVendorId();//StringUtils.leftPad("1",6,'0'); // 발주등록(주문) 화면의 '구매처'
		this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
		// this.ownerId =
		// StringUtils.leftPad("1",6,'0');//depositInsertRequestData.getOwnerId();
    }

	public Lsdpsm(String depositNo, InsertDepositEtcRequestData p) {
		this.depositNo = depositNo;
		this.depositDt = LocalDateTime.parse(p.getDepositDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.storeCd = p.getStorageId();
		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
		this.depositGb = p.getDepositGb(); // 01 하드코딩
		this.vendorId = p.getVendorId();// StringUtils.leftPad("1",6,'0'); // 발주등록(주문) 화면의 '구매처'
		this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.depositType = p.getDepositType();
		this.setRegId(p.getUserId());
		this.setUpdId(p.getUserId());
		// this.ownerId =
		// StringUtils.leftPad("1",6,'0');//depositInsertRequestData.getOwnerId();
	}

	public Lsdpsm(String depositNo, InsertShipEtcRequestData p) {
		// 기타출고
		this.depositNo = depositNo;
		this.depositDt = LocalDateTime.parse(p.getShipDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		;
		this.storeCd = p.getStorageId();
		this.siteGb = StringFactory.getGbOne();
		this.depositGb = p.getDepositGb();
		this.vendorId = p.getVendorId();// StringUtils.leftPad("1",6,'0'); // 발주등록(주문) 화면의 '구매처'
		this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.depositType = p.getDepositType();
		this.deliFee = p.getDeliFee() == null? 0f : p.getDeliFee();
		this.setRegId(p.getUserId());
		this.setUpdId(p.getUserId());
		// this.ownerId =
		// StringUtils.leftPad("1",6,'0');//depositInsertRequestData.getOwnerId();
	}

    public Lsdpsm(String depositNo, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
		this.vendorId = depositListWithPurchaseInfoData.getVendorId();
        this.depositNo = depositNo;
		this.depositDt = LocalDateTime.parse(depositListWithPurchaseInfoData.getDepositDt(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));// new Date();
        this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
        this.depositGb = StringFactory.getGbOne(); // 01 하드코딩
		this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDay(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 9999-12-31 하드코딩
        this.depositType = StringFactory.getGbOne(); // 01 하드코딩
        this.storeCd = depositListWithPurchaseInfoData.getStorageId();
		// this.ownerId = depositListWithPurchaseInfoData.getVendorId();
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
    private String ownerId;
	// 22-04-07 추가
	private Float deliFee;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId", referencedColumnName="id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Cmvdmr cmvdmr;
}
