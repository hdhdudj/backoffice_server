package io.spring.model.goods.entity;

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
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="ititmt")
@IdClass(ItitmtId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmt extends CommonProps {
    public Ititmt(ItitmtId ititmtId){
        this.storageId = ititmtId.getStorageId();
        this.assortId = ititmtId.getAssortId();
        this.itemId = ititmtId.getItemId();
        this.itemGrade = ititmtId.getItemGrade();
        this.effEndDt = ititmtId.getEffEndDt();
        this.effStaDt = ititmtId.getEffStaDt();
    }
    public Ititmt(Date purchaseDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.storageId = storageId;
        this.assortId = deposit.getAssortId();
        this.itemId = deposit.getItemId();
		this.effEndDt = LocalDateTime.now();//LocalDateTime.parse(StringFactory.getDoomDay(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 9999-12-31 하드코딩
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
        this.effEndDt = LocalDateTime.now();//LocalDateTime.parse(StringFactory.getDoomDay(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 9999-12-31 하드코딩
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.tempIndicateQty = 0l;
        this.tempQty = lspchd.getPurchaseQty();
        this.stockAmt = lspchd.getPurchaseUnitAmt();
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
    private String vendorId = StringUtils.leftPad(StringFactory.getStrOne(),6,'0'); // 000001 하드코딩
    private String siteGb = StringFactory.getGbOne(); // 01 하드코딩
}
