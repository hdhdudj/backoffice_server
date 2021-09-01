package io.spring.model.deposit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.idclass.LsdpsdId;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.purchase.entity.Lspchd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

//import org.flywaydb.core.internal.util.StringUtils;

@Entity
@Getter
@Setter
@Table(name="lsdpsd")
@IdClass(LsdpsdId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lsdpsd extends CommonProps implements Serializable {
    // 발주 생성시 실행되는 생성자
    public Lsdpsd(String depositNo, DepositInsertRequestData.Item item){
        this.depositNo = depositNo;
        this.depositSeq = item.getDepositSeq();
        this.assortId = item.getAssortId();
        this.itemGrade = item.getItemGrade();
        this.itemId = item.getItemId();
        this.extraClsCd = StringFactory.getGbOne(); // 초기값 일단 하드코딩 '01'
        this.salePrice = 0f;
        this.depositQty = item.getDepositQty();
        this.extraUnitcost = item.getExtraUnitcost();
        this.deliPrice = this.depositQty * this.extraUnitcost;
        this.extraCost = this.deliPrice;
        this.extraQty = this.depositQty;
        this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDayT()); // 9999-12-31 하드코딩
        this.depositType = StringFactory.getGbOne(); // 초기값 일단 하드코딩 '01' 입고
        this.siteGb = StringFactory.getGbOne(); // 초기값 일단 하드코딩 '01'
        this.vendorId = StringUtils.leftPad("1", 6, '0');
        this.inputNo = item.getPurchaseNo();
        this.inputSeq = item.getPurchaseSeq();
    }
    // 입고 체크 후 저장시 실행되는 생성자
    public Lsdpsd(Lsdpsm lsdpsm, String depositSeq, DepositListWithPurchaseInfoData.Deposit deposit) {
        this.depositNo = lsdpsm.getDepositNo();
        this.depositSeq = depositSeq;
        this.assortId = deposit.getAssortId();
        this.itemId = deposit.getItemId();
        this.itemGrade = StringFactory.getStrEleven(); // 11 하드코딩
        this.extraClsCd = StringFactory.getGbOne(); // 01 하드코딩
        this.depositQty = deposit.getDepositQty();
        this.extraUnitcost = deposit.getPurchaseCost();
        this.deliPrice = extraUnitcost * depositQty; // 단가 * 개수
        this.extraCost = extraUnitcost * depositQty; // 단가 * 개수
        this.extraQty = deposit.getDepositQty();
        this.finishYymm = LocalDateTime.parse(StringFactory.getDoomDayT()); // 9999-12-31 하드코딩
        this.depositType = StringFactory.getGbOne(); // 초기값 일단 하드코딩 '01' 입고
        this.siteGb = StringFactory.getGbOne(); // 초기값 일단 하드코딩 '01'
        this.vendorId = StringUtils.leftPad("1", 6, '0'); // 000001 하드코딩
        this.inputNo = deposit.getPurchaseNo();
        this.inputSeq = deposit.getPurchaseSeq();
    }
    @Id
    private String depositNo;
    @Id
    private String depositSeq;
    private String assortId;
    private String itemId;
    private String itemGrade;
    private String extraClsCd;
    private Long depositQty;
    private Float deliPrice;
    private Float salePrice = 0f;
    private Float extraUnitcost;
    private Float extraCost;
    private Long extraQty;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime finishYymm;
    private String depositType;
    private String siteGb;
    private String vendorId;
    private String sStorageCd;
    private String minDepositNo;
    private String minDepositSeq;
    private String inputNo;
    private String inputSeq;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime excAppDt;


    // 연관 관계 lsdpsm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depositNo", referencedColumnName="depositNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Lsdpsm lsdpsm;

    // 연관 관계 lspchd
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "inputNo", referencedColumnName="purchaseNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "inputSeq", referencedColumnName="purchaseSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private Lspchd lspchd;

    // 연관 관계 lsdpds
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "depositNo", referencedColumnName="depositNo", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "depositSeq", referencedColumnName="depositSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    private List<Lsdpds> lsdpds;

    // 연관 관계 itasrt
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Itasrt itasrt;

    // 연관 관계 ititmm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "assortId", referencedColumnName="assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
            @JoinColumn(name = "itemId", referencedColumnName="itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
    })
    @NotFound(action = NotFoundAction.IGNORE)
    private Ititmm ititmm;
}
