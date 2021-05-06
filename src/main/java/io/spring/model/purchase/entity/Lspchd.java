package io.spring.model.purchase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.purchase.idclass.LspchdId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="lspchd")
@IdClass(LspchdId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lspchd {
    public Lspchd(String purchaseNo, String purchaseSeq){
        this.purchaseNo = purchaseNo;
        this.purchaseSeq = purchaseSeq;
    }
    @Id
    private String purchaseNo;
    @Id
    private String purchaseSeq;
    private String assortId;
    private String itemId;
    private Long purchaseQty;
    private Float purchaseUnitAmt;
    private Float purchaseItemAmt;
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
    private String siteOrderNo;

    // 연관관계 : lspchb
    @OneToMany(fetch = FetchType.LAZY, targetEntity = Lspchb.class)
    @JoinColumns({
            @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "purchaseSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lspchb> lspchb;

    // 연관관계 : lspchm
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Lspchm.class)
    @JoinColumn(name = "purchaseNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Lspchm lspchm;

    // 연관관계 : ititmm
    @OneToOne
    @JsonIgnore
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Ititmm ititmm;

    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
}
