package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.goods.idclass.ItitmcId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="ititmc")
@IdClass(ItitmcId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmc extends CommonProps {
    public Ititmc(DepositInsertRequestData depositInsertRequestData, DepositInsertRequestData.Item item){
        this.storageId = depositInsertRequestData.getStoreCd();
        this.assortId = item.getAssortId();
        this.itemId = item.getItemId();
        this.effEndDt = depositInsertRequestData.getDepositDt();
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
    }
    public Ititmc(String storageId, Date purchaseDt, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.storageId = storageId;
        this.assortId = deposit.getAssortId();
        this.itemId = deposit.getItemId();
        this.effEndDt = purchaseDt;
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
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
    private Date effEndDt;
    @Id
    private Date effStaDt;
    private String stockGb;
    private Long shipIndicateQty;
    private Long qty;
    private Float stockAmt;
    private String vendorId;
    private String siteGb;


}
