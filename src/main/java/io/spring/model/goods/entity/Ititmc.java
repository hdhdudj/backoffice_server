package io.spring.model.goods.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.Cmstgm;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.goods.idclass.ItitmcId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
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

	public Ititmc(String storageId, LocalDateTime effStaDt, String assortId, String itemId, String itemGrade,
			Float localPrice, Long qty) {
		this.storageId = storageId;
		this.assortId = assortId;
		this.itemId = itemId;
		this.effEndDt = effStaDt;
		this.effStaDt = effStaDt;
		this.stockGb = itemGrade; // 01 하드코딩
		this.stockAmt = localPrice;
		this.qty = qty;
		this.shipIndicateQty = 0l;
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

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Cmstgm.class)
	@JoinColumn(name = "storageId", referencedColumnName = "storageId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	private Cmstgm cmstgm;

	@JoinColumns({
			@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
			@JoinColumn(name = "itemId", referencedColumnName = "itemId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")) })
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Ititmm ititmm; // ititmc 연관관계

}
