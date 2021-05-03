package io.spring.model.purchase.entity;

import io.spring.model.purchase.idclass.LspchdId;
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
@Table(name="lspchd")
@IdClass(LspchdId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchd {
    @Id
    private String purchaseNo;
    @Id
    private String purchaseSeq;
    private String assortId;
    private String itemId;
    private Long purchaseQty;
    private Float purchaseUnitamt;
    private Float purchaseItemamt;
    private String itemGrade;
    private String vatGb;
    private String setGb;
    private String mailsendYn;
    private String memo;
    private String siteGb;
    private String vendorId;
    private String raNo;
    private Float itemAmt;
    private Float newItemAmt;
    private Float transAmt;
    private Float newTransAmt;
    private Float taxAmt;
    private Float newTaxAmt;
    private Float saleAmt;
    private Float newSaleAmt;
    private String orderId;
    private String orderSeq;
    private String depositNo;
    private String depositSeq;
    private String setShipId;
    private String setShipSeq;
    private String siteOrderno;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
