package io.spring.model.goods.entity;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.ItitmtId;
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
    private Long tempIndicateQty;
    private Long tempQty;
    private Float stockAmt;
    private String vendorId;
    private String siteGb;
}
