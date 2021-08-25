package io.spring.model.goods.idclass;

import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmtId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    public ItitmtId(String storageId, String assortId, String itemId, String itemGrade){

    }

    public ItitmtId(PurchaseInsertRequestData purchaseInsertRequestData, PurchaseInsertRequestData.Items items){
        this.storageId = purchaseInsertRequestData.getStorageId();
        this.assortId = items.getAssortId();
        this.itemId = items.getItemId();
        this.itemGrade = items.getItemGrade();
        this.effStaDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = this.effStaDt;
    }

    public ItitmtId(DepositInsertRequestData depositInsertRequestData, DepositInsertRequestData.Item item){
        this.storageId = depositInsertRequestData.getStorageId();
        this.assortId = item.getAssortId();
        this.itemId = item.getItemId();
        this.itemGrade = item.getItemGrade();
        this.effStaDt = depositInsertRequestData.getDepositDt();
        this.effEndDt = this.effStaDt;
    }

    private String storageId;
    private String assortId;
    private String itemId;
    private String itemGrade;
    private LocalDateTime effEndDt;
    private LocalDateTime effStaDt;
}
