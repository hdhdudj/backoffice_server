package io.spring.model.goods.entity;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name="ititmt")
@IdClass(ItitmtId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmt extends CommonProps {
    public Ititmt(ItitmtId ititmtId){
        this.storageId = ititmtId.getStorageId();
        this.assortId = ititmtId.getAssortId();
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.itemId = ititmtId.getItemId();
        this.itemGrade = ititmtId.getItemGrade();
        this.effEndDt = ititmtId.getEffEndDt();
        this.effStaDt = ititmtId.getEffStaDt();
		this.tempIndicateQty = 0L;
		this.tempQty = 0L;
    }
    public Ititmt(LocalDateTime purchaseDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.storageId = storageId;
        this.assortId = deposit.getAssortId();
        this.itemId = deposit.getItemId();
		this.effEndDt = purchaseDt;//LocalDateTime.now();//LocalDateTime.parse(StringFactory.getDoomDay(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 9999-12-31 하드코딩
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.tempIndicateQty = deposit.getDepositQty();
        this.stockAmt = deposit.getPurchaseCost();
    }

    public Ititmt(Lspchm lspchm, Lspchd lspchd, String regId) {
        this.storageId = lspchm.getStoreCd();
        this.assortId = lspchd.getAssortId();
        this.itemId = lspchd.getItemId();
        this.itemGrade = StringFactory.getStrEleven(); // 11 하드코딩
        this.effEndDt = lspchm.getPurchaseDt();//LocalDateTime.now();//LocalDateTime.parse(StringFactory.getDoomDay(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 9999-12-31 하드코딩
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.tempIndicateQty = 0l;
        this.tempQty = lspchd.getPurchaseQty();
        this.stockAmt = lspchd.getPurchaseUnitAmt();
		this.vendorId = lspchm.getVendorId();
        super.setRegId(regId);
        super.setUpdId(regId);
    }
    @Id
    private String storageId;
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    private String itemGrade = StringFactory.getStrEleven(); // 11 하드코딩
    @Id
    private LocalDateTime effEndDt;
    @Id
    private LocalDateTime effStaDt;
    private String stockGb;
    private Long tempIndicateQty;
    private Long tempQty;
    private Float stockAmt;
	private String vendorId; // 000001 하드코딩
    private String ownerId;
	private String siteGb; // 01 하드코딩
}
