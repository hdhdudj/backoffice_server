package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Itasrt extends CommonProps {

	public Itasrt(GoodsInsertRequestData goodsInsertRequestData){
		this.assortId = goodsInsertRequestData.getAssortId();
		this.assortNm = goodsInsertRequestData.getAssortNm();
		this.assortModel = goodsInsertRequestData.getAssortModel();
		this.margin = goodsInsertRequestData.getMargin();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.assortGb = goodsInsertRequestData.getAssortGb();
		this.assortState = goodsInsertRequestData.getAssortState();
		this.asWidth = goodsInsertRequestData.getAsWidth();
		this.asLength = goodsInsertRequestData.getAsLength();
		this.asHeight = goodsInsertRequestData.getAsHeight();
		this.weight = goodsInsertRequestData.getWeight();
		this.origin = goodsInsertRequestData.getOrigin();
		this.shortageYn = goodsInsertRequestData.getShortageYn();
		this.brandId = goodsInsertRequestData.getBrandId();
		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId();
		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
		this.asVendorId = goodsInsertRequestData.getAsVendorId();
		this.manufactureNm = goodsInsertRequestData.getManufactureNm();
		this.deliPrice = goodsInsertRequestData.getDeliPrice();
		this.localPrice = goodsInsertRequestData.getLocalPrice();
		this.localSale = goodsInsertRequestData.getLocalSale();
	//	this.localDeliFee = goodsInsertRequestData.getLocalDeliFee();
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
//		this.sizeType = goodsInsertRequestData.getSizeType();
		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate();
		this.optionGbName = goodsInsertRequestData.getOptionGbName();
		
	}

	@Id
	private String assortId;

	private String assortNm;
	private String assortModel;
	private Float margin;
	private String taxGb;
	private String assortGb;
	private String assortState;
	private Float asWidth;
	private Float asLength;
	private Float asHeight;
	private Float weight;
	private String origin;
	private String shortageYn; // itasrn에도
	private String brandId;
	private String dispCategoryId;
	private String siteGb;
	private String asVendorId;
	private String manufactureNm;
	private Float deliPrice;
	private Float localPrice;
	private Float localDeliFee;
	private Float localSale; // itasrn에도 들어감
	private String assortColor;
	private Date sellStaDt;
	private Date sellEndDt;
	private Float mdRrp;
	private String mdTax;
	private String mdYear;
	private Float mdMargin;
	private Float mdVatrate;
	private Float mdOfflinePrice;
	private Float mdOnlinePrice;
	private Float mdGoodsVatrate;
	private String buyWhere;
	private String buyTax;
	private Float buySupplyDiscount;
	private Float buyRrpIncrement;
	private Float buyExchangeRate;
//	private String sizeType;
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Itcatg itcatg; // itcatg 연관관계

	@JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Itaimg> itaimg; // itaimg 연관관계

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
