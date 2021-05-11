package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.goods.idclass.ItitmcId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class Ititmc {
    public Ititmc(DepositInsertRequestData depositInsertRequestData, DepositInsertRequestData.Item item){
        this.storageId = depositInsertRequestData.getStoreCd();
        this.assortId = item.getAssortId();
        this.itemId = item.getItemId();
        this.itemGrade = item.getItemGrade();
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        this.effStaDt = new Date();
        this.stockGb = StringFactory.getGbOne(); // 01 하드코딩
    }
    @Id
    private String storageId;
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    private String itemGrade;
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
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
