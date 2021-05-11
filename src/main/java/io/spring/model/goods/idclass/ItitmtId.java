package io.spring.model.goods.idclass;

import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItitmtId implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(ItitmtId.class);
    //default serial version id, required for serializable classes.
    public ItitmtId(PurchaseInsertRequestData purchaseInsertRequestData, PurchaseInsertRequestData.Items items){
        this.storageId = purchaseInsertRequestData.getStoreCd();
        this.assortId = items.getAssortId();
        this.itemId = items.getItemId();
        this.itemGrade = items.getItemGrade();
        this.effStaDt = purchaseInsertRequestData.getPurchaseDt();
        this.effEndDt = this.effStaDt;
    }

    private static final long serialVersionUID = 1L;
    private String storageId;
    private String assortId;
    private String itemId;
    private String itemGrade;
    private Date effEndDt;
    private Date effStaDt;
}
