package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 *  ITASRT table의 Entity
 *  ITASRT : 상품 정보 table
 */

@Entity
@Table(name = "itasrt")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itasrt {

	public Itasrt(GoodsInsertRequestData goodsInsertRequestData){
		this.assortId = goodsInsertRequestData.getAssortId();
		this.assortNm = goodsInsertRequestData.getAssortNm();
		this.assortModel = goodsInsertRequestData.getAssortModel();
		this.margin = goodsInsertRequestData.getMargin();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.assortGb = goodsInsertRequestData.getAssortGb();
		this.assortState = goodsInsertRequestData.getAssortState();
//		this.asWidth = goodsRequestData.getAsWidth();
//		this.asLength = goodsRequestData.getAsLength();
//		this.asHeight = goodsRequestData.getAsHeight();
//		this.weight = goodsRequestData.getWeight();
		this.origin = goodsInsertRequestData.getOrigin();
		this.shortageYn = goodsInsertRequestData.getShortageYn();
		this.brandId = goodsInsertRequestData.getBrandId();
		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId();
		this.siteGb = "01";
		this.asVendorId = goodsInsertRequestData.getAsVendorId();
		this.manufactureNm = goodsInsertRequestData.getManufactureNm();
//		this.deliPrice = goodsRequestData.getDeliPrice();
//		this.localPrice = goodsRequestData.getLocalPrice();
//		this.localSale = goodsRequestData.getLocalSale();
		this.localDeliFee = goodsInsertRequestData.getLocalDeliFee();
		this.assortColor = goodsInsertRequestData.getAssortColor();
		this.sellStaDt = goodsInsertRequestData.getSellStaDt();
		this.sellEndDt = goodsInsertRequestData.getSellEndDt();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.mdRrp = goodsInsertRequestData.getMdRrp();
		this.mdTax = goodsInsertRequestData.getMdTax();
		this.mdYear = goodsInsertRequestData.getMdYear();
		this.mdMargin = goodsInsertRequestData.getMdMargin();
		this.mdMargin = goodsInsertRequestData.getMdMargin();
		this.mdVatrate = goodsInsertRequestData.getMdVatrate();
		this.mdOfflinePrice = goodsInsertRequestData.getMdOfflinePrice();
		this.mdOnlinePrice = goodsInsertRequestData.getMdOnlinePrice();
		this.mdGoodsVatrate = goodsInsertRequestData.getMdGoodsVatrate();
		this.buyWhere = goodsInsertRequestData.getBuyWhere();
		this.buyTax = goodsInsertRequestData.getBuyTax();
		this.buySupplyDiscount = goodsInsertRequestData.getBuySupplyDiscount();
		this.buyRrpIncrement = goodsInsertRequestData.getBuyRrpIncrement();
		this.buyExchangeRate = goodsInsertRequestData.getBuyExchangeRate();
		this.sizeType = goodsInsertRequestData.getSizeType();
		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate();
	}

	@Id
	private String assortId;

	private Long regId;
	private Long updId;
	@CreationTimestamp
	private Date regDt;
	@UpdateTimestamp
	private Date updDt;

	private String assortNm;
	private String assortModel;
	private Float margin;
	private String taxGb;
	private String assortGb;
	private String assortState;
	@Column(nullable = true)
	private Float asWidth;
	@Column(nullable = true)
	private Float asLength;
	@Column(nullable = true)
	private Float asHeight;
	@Column(nullable = true)
	private Float weight;
	private String origin;
	private String shortageYn; // itasrn에도
	private String brandId;
	private String dispCategoryId;
	private String siteGb;
	private String asVendorId;
	private String manufactureNm;
	@Column(nullable = true)
	private Float deliPrice;
	@Column(nullable = true)
	private Float localPrice;
	private Float localDeliFee;
	@Column(nullable = true)
	private Float localSale; // itasrn에도 들어감
	private String assortColor;
	private Date sellStaDt;
	private Date sellEndDt;
	@Column(nullable = true)
	private Float mdRrp;
	private String mdTax;
	private String mdYear;
	@Column(nullable = true)
	private Float mdMargin;
	@Column(nullable = true)
	private Float mdVatrate;
	@Column(nullable = true)
	private Float mdOfflinePrice;
	@Column(nullable = true)
	private Float mdOnlinePrice;
	@Column(nullable = true)
	private Float mdGoodsVatrate;
	private String buyWhere;
	private String buyTax;
	@Column(nullable = true)
	private Float buySupplyDiscount;
	@Column(nullable = true)
	private Float buyRrpIncrement;
	@Column(nullable = true)
	private Float buyExchangeRate;
	private String sizeType;
	@Column(nullable = true)
	private Float mdDiscountRate;
	private String optionGbName;
	private String optionUseYn;

	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY) // itvari 연관관계
	private List<Itvari> itvariList;

	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY) // ititmm 연관관계
	private List<Ititmm> ititmmList;

	@JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@OneToMany(fetch = FetchType.LAZY) // itasrd 연관관계
	@JsonIgnore
	private List<Itasrd> itasrdList;

	@JoinColumn(name="brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Itbrnd itbrnd; // itbrnd 연관관계

	@JoinColumn(name="dispCategoryId", referencedColumnName = "categoryId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne
	@JsonIgnore
	private Itcatg itcatg; // itcatg 연관관계
	
	

//	@OneToOne
//	@JoinColumn(name = "brand_id", referencedColumnName = "brand_id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	@JsonIgnore
//	@ManyToOne(fetch = FetchType.LAZY) // 연관관계
//	private Itbrnd itbrnd;
//	@ManyToOne(fetch = FetchType.LAZY) // 연관관계
//	@JoinColumn(name = "disp_category_id", referencedColumnName = "category_id", insertable=false, updatable=false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	private Itcatg itcatg;

//	private String brandNm;
//	private String categoryNm;
//	private String assortDnm;
//	private String assortEnm;
//	private String payMthCd;
//	private String couponYn;
//	private String packYn;
//	private String deliConfirmYn;
//	private String deliMth;
//	private String addDeliGb;
//	private String deliCharge;
//	private String deliInterval;
//	private String marginGb;
//	private String marginApp;
//	private String adultGb;
//	private String unionApply;
//	private String cardFreeGb;
//	private String divideMth;
//	private String reasonCd;
//	private String vendorTrGb;
//	private String siteUrl;
//	private String luxuryYn;
//	private String weight;
//	private String imgCnt;
//	private String cardLimitYn;
//	private String searchYn;
//	private String sizeId;
//	private String quality;
//	private String leadTime;
//	private String deliSure;
//	private String reserveYn;
//	private String resStaDt;
//	private String resEndDt;
//	private String claimSureYn;
//	private String defaultYn;
//	private String recommYn;
//	private String recommCnt;
//	private String recommQty;
//	private String btMarkYn;
//	private String sendbackRejectYn;
//	private String setGb;
//	private String userId;
//	private String categoryId;
//	private String hsCode;
//	private String unit;
//	private String vendorId;
//	private String alAssortId;
//	private String drtSalesGb;
//	private String templateId;
//	private String drtSalesRatio;
//	private String speTaxYn;
//	private String preItemYn;
//	private String srhExpYn;
//	private String nonsaleYn;
//	private String deliNrgGb;
//	private String delayRewardYn;
//	private String callDisLimit;
//	private String initLocalPrice;
//	private String estiPrice;
//	private String invoiceNm;
//	private String localDeliFee;
//	private String cardFreeYn;
//	private String itemAbbNm;
//	private String disGb;
//	private String disRate;
//	private String reserveGive;
//	private String bonusReserve;
//	private String cashbagPoint;
//	private String plGbn;
//	private String plFromDt;
//	private String plToDt;
//	private String storageId;
//	private String optionGb;
//	private String shopSaleGb;
//	private String sendbackPayGb;
//	private String sendbackChangeYn;
//	private String directPathNrgCd;
//	private String marginCd;
//	private String directPathGb;
//	private String resShipStaDt;
//	private String resShipEndDt;
//	private String imgType;
//	private String onlinedispYn;
//	private String payType;
//	private String freeGiftYn;
//	private String currencyUnit;
//	private String disStartDt;
//	private String disEndDt;
//	private String workGb;
//	private String cardFee;
//	private String assortGrade;
//	private String mdTax;
//	private String mdYear;
//	private String mdMargin;
}
