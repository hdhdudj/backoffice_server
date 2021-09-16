package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.goods.idclass.ItitmcId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="ititmc")
@IdClass(ItitmcId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ititmc extends CommonProps {
    public Ititmc(DepositInsertRequestData depositInsertRequestData, DepositInsertRequestData.Item item){
        this.storageId = depositInsertRequestData.getStorageId();
        this.assortId = item.getAssortId();
        this.itemId = item.getItemId();
        this.effEndDt = depositInsertRequestData.getDepositDt();
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
    }
    public Ititmc(String storageId, LocalDateTime depositDt, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.storageId = storageId;
        this.assortId = deposit.getAssortId();
        this.itemId = deposit.getItemId();
        this.effEndDt = depositDt;
        this.effStaDt = this.effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.stockAmt = deposit.getPurchaseCost();
    }
    public Ititmc(String storageId, String assortId, String itemId, Float localPrice, Long qty){
        this.storageId = storageId;
        this.assortId = assortId;
        this.itemId = itemId;
        this.effEndDt = LocalDateTime.now();
        this.effStaDt = effEndDt;
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
        this.stockAmt = localPrice;
        this.qty = qty;
        this.shipIndicateQty =0l;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effStaDt;
    private String stockGb;
    private Long shipIndicateQty;
    private Long qty;
    private Float stockAmt;
    private String vendorId;
    private String ownerId;
    private String siteGb;

    // 연관관계 : itasrt
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Itasrt.class)
    @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;
}
