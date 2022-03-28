package io.spring.model.goods.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.spring.model.goods.request.GoodsInsertRequestData2;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  ITASRT table의 Entity
 *  ITASRT : 상품 정보 table
 */

@DynamicUpdate
@Entity
@Table(name = "itasrt")
@Getter
@Setter
//public class Itasrt extends CommonProps implements PersistentAttributeInterceptable, Serializable {
public class Itasrt extends CommonProps implements Serializable {
	public Itasrt(){

	}

	public Itasrt(GoodsInsertRequestData goodsInsertRequestData){
		this.assortId = goodsInsertRequestData.getAssortId();
		this.assortNm = goodsInsertRequestData.getAssortNm();
		this.assortModel = goodsInsertRequestData.getAssortModel();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.assortGb = goodsInsertRequestData.getAssortGb();
		this.assortState = goodsInsertRequestData.getAssortState();
		this.margin = goodsInsertRequestData.getMargin() == null || goodsInsertRequestData.getMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMargin());
		this.asWidth = goodsInsertRequestData.getAsWidth() == null || goodsInsertRequestData.getAsWidth().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsWidth());
		this.asLength = goodsInsertRequestData.getAsLength() == null || goodsInsertRequestData.getAsLength().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsLength());
		this.asHeight = goodsInsertRequestData.getAsHeight() == null || goodsInsertRequestData.getAsHeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsHeight());
		this.weight = goodsInsertRequestData.getWeight() == null || goodsInsertRequestData.getWeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getWeight());
		this.origin = goodsInsertRequestData.getOrigin();
		this.shortageYn = goodsInsertRequestData.getShortageYn();
		this.brandId = goodsInsertRequestData.getBrandId();
		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId();
		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
		this.ownerId = goodsInsertRequestData.getAsVendorId();
		this.manufactureNm = goodsInsertRequestData.getManufactureNm();
		this.deliPrice = goodsInsertRequestData.getDeliPrice() == null || goodsInsertRequestData.getDeliPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getDeliPrice());
		this.localPrice = goodsInsertRequestData.getLocalPrice() == null || goodsInsertRequestData.getLocalPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalPrice());
		this.localSale = goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale());
		this.mdRrp = goodsInsertRequestData.getMdRrp() == null || goodsInsertRequestData.getMdRrp().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdRrp());
		this.mdMargin = goodsInsertRequestData.getMdMargin() == null || goodsInsertRequestData.getMdMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdMargin());
		this.mdVatrate = goodsInsertRequestData.getMdVatrate() == null || goodsInsertRequestData.getMdVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdVatrate());
		this.mdOfflinePrice = goodsInsertRequestData.getMdOfflinePrice() == null || goodsInsertRequestData.getMdOfflinePrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOfflinePrice());
		this.mdOnlinePrice = goodsInsertRequestData.getMdOnlinePrice() == null || goodsInsertRequestData.getMdOnlinePrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOnlinePrice());
		this.mdGoodsVatrate = goodsInsertRequestData.getMdGoodsVatrate() == null || goodsInsertRequestData.getMdGoodsVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate());
		this.buySupplyDiscount = goodsInsertRequestData.getBuySupplyDiscount() == null || goodsInsertRequestData.getBuySupplyDiscount().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuySupplyDiscount());
		this.buyRrpIncrement = goodsInsertRequestData.getBuyRrpIncrement() == null || goodsInsertRequestData.getBuyRrpIncrement().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyRrpIncrement());
		this.buyExchangeRate = goodsInsertRequestData.getBuyExchangeRate() == null || goodsInsertRequestData.getBuyExchangeRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyExchangeRate());
		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate() == null || goodsInsertRequestData.getMdDiscountRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdDiscountRate());
		this.assortColor = goodsInsertRequestData.getAssortColor();
		this.sellStaDt = goodsInsertRequestData.getSellStaDt();//.toLocalDateTime();
		this.sellEndDt = goodsInsertRequestData.getSellEndDt();//.toLocalDateTime();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.mdTax = goodsInsertRequestData.getMdTax();
		this.mdYear = goodsInsertRequestData.getMdYear();
		this.buyWhere = goodsInsertRequestData.getBuyWhere();
		this.buyTax = goodsInsertRequestData.getBuyTax();
		this.optionGbName = goodsInsertRequestData.getOptionGbName();
		this.vendorId = goodsInsertRequestData.getVendorId();

		this.optionUseYn = goodsInsertRequestData.getOptionUseYn();
	}

	public Itasrt(GoodsInsertRequestData2 goodsInsertRequestData){
		this.assortId = goodsInsertRequestData.getAssortId().get();
		this.assortId = goodsInsertRequestData.getAssortId() == null? this.assortId : goodsInsertRequestData.getAssortId().get();
		this.assortNm = goodsInsertRequestData.getAssortNm() == null? this.assortNm : goodsInsertRequestData.getAssortNm().get();
		this.assortModel = goodsInsertRequestData.getAssortModel() == null? this.assortModel : goodsInsertRequestData.getAssortModel().get();
		this.taxGb = goodsInsertRequestData.getTaxGb() == null? this.taxGb : goodsInsertRequestData.getTaxGb().get();
		this.assortGb = goodsInsertRequestData.getAssortGb() == null? this.assortGb : goodsInsertRequestData.getAssortGb().get();
		this.assortState = goodsInsertRequestData.getAssortState() == null? this.assortState : goodsInsertRequestData.getAssortState().get();
		this.origin = goodsInsertRequestData.getOrigin() == null? this.origin : goodsInsertRequestData.getOrigin().get();
		this.shortageYn = goodsInsertRequestData.getShortageYn() == null? this.shortageYn : goodsInsertRequestData.getShortageYn().get();
		this.userId = goodsInsertRequestData.getUserId() == null? this.userId : goodsInsertRequestData.getUserId().get();
		this.brandId = goodsInsertRequestData.getBrandId() == null? this.brandId : goodsInsertRequestData.getBrandId().get();
		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId() == null? this.dispCategoryId : goodsInsertRequestData.getDispCategoryId().get();
		this.vendorId = goodsInsertRequestData.getVendorId() == null? this.vendorId : goodsInsertRequestData.getVendorId().get();
		this.siteGb = goodsInsertRequestData.getSiteGb() == null? this.siteGb : goodsInsertRequestData.getSiteGb().get();
		this.manufactureNm = goodsInsertRequestData.getManufactureNm() == null? this.manufactureNm : goodsInsertRequestData.getManufactureNm().get();
		this.assortColor = goodsInsertRequestData.getAssortColor() == null? this.assortColor : goodsInsertRequestData.getAssortColor().get();
		this.sellStaDt = goodsInsertRequestData.getSellStaDt() == null? this.sellStaDt : goodsInsertRequestData.getSellStaDt().get();
		this.sellEndDt = goodsInsertRequestData.getSellEndDt() == null? this.sellEndDt : goodsInsertRequestData.getSellEndDt().get();
		this.mdTax = goodsInsertRequestData.getMdTax() == null? this.mdTax : goodsInsertRequestData.getMdTax().get();
		this.mdYear = goodsInsertRequestData.getMdYear() == null? this.mdYear : goodsInsertRequestData.getMdYear().get();
		this.buyWhere = goodsInsertRequestData.getBuyWhere() == null? this.buyWhere : goodsInsertRequestData.getBuyWhere().get();
		this.buyTax = goodsInsertRequestData.getBuyTax() == null? this.buyTax : goodsInsertRequestData.getBuyTax().get();
		this.optionGbName = goodsInsertRequestData.getOptionGbName() == null? this.optionGbName : goodsInsertRequestData.getOptionGbName().get();
		this.optionUseYn = goodsInsertRequestData.getOptionUseYn() == null? this.optionUseYn : goodsInsertRequestData.getOptionUseYn().get();

		this.margin = goodsInsertRequestData.getMargin() == null? this.margin : goodsInsertRequestData.getMargin().get() == null || goodsInsertRequestData.getMargin().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMargin().get());
		this.asWidth = goodsInsertRequestData.getAsWidth() == null? this.asWidth : goodsInsertRequestData.getAsWidth().get() == null || goodsInsertRequestData.getAsWidth().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsWidth().get());
		this.asLength = goodsInsertRequestData.getAsLength() == null? this.asLength : goodsInsertRequestData.getAsLength().get() == null || goodsInsertRequestData.getAsLength().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsLength().get());
		this.asHeight = goodsInsertRequestData.getAsHeight() == null? this.asHeight : goodsInsertRequestData.getAsHeight().get() == null || goodsInsertRequestData.getAsHeight().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsHeight().get());
		this.weight = goodsInsertRequestData.getWeight() == null? this.weight : goodsInsertRequestData.getWeight().get() == null || goodsInsertRequestData.getWeight().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getWeight().get());
		this.deliPrice = goodsInsertRequestData.getDeliPrice() == null? this.deliPrice : goodsInsertRequestData.getDeliPrice().get() == null || goodsInsertRequestData.getDeliPrice().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getDeliPrice().get());
		this.localPrice = goodsInsertRequestData.getLocalPrice() == null? this.localPrice : goodsInsertRequestData.getLocalPrice().get() == null || goodsInsertRequestData.getLocalPrice().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalPrice().get());
		this.localSale = goodsInsertRequestData.getLocalSale() == null? this.localSale : goodsInsertRequestData.getLocalSale().get() == null || goodsInsertRequestData.getLocalSale().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale().get());
		this.localDeliFee = goodsInsertRequestData.getLocalDeliFee() == null? this.localDeliFee : goodsInsertRequestData.getLocalDeliFee().get() == null || goodsInsertRequestData.getLocalDeliFee().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalDeliFee().get());
		this.sellStaDt = goodsInsertRequestData.getSellStaDt() == null? this.sellStaDt : goodsInsertRequestData.getSellStaDt().get() == null? null : goodsInsertRequestData.getSellStaDt().get();
		this.sellEndDt = goodsInsertRequestData.getSellEndDt() == null? this.sellEndDt : goodsInsertRequestData.getSellEndDt().get() == null? null : goodsInsertRequestData.getSellEndDt().get();
		this.mdRrp = goodsInsertRequestData.getMdRrp() == null? this.mdRrp : goodsInsertRequestData.getMdRrp().get() == null || goodsInsertRequestData.getMdRrp().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdRrp().get());
		this.mdMargin = goodsInsertRequestData.getMdMargin() == null? this.mdMargin : goodsInsertRequestData.getMdMargin().get() == null || goodsInsertRequestData.getMdMargin().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdMargin().get());
		this.mdVatrate = goodsInsertRequestData.getMdVatrate() == null? this.mdVatrate : goodsInsertRequestData.getMdVatrate().get() == null || goodsInsertRequestData.getMdVatrate().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdVatrate().get());
		this.mdOfflinePrice = goodsInsertRequestData.getMdOfflinePrice() == null? this.mdOfflinePrice : goodsInsertRequestData.getMdOfflinePrice().get() == null || goodsInsertRequestData.getMdOfflinePrice().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOfflinePrice().get());
		this.mdOnlinePrice = goodsInsertRequestData.getMdOnlinePrice() == null? this.mdOnlinePrice : goodsInsertRequestData.getMdOnlinePrice().get() == null || goodsInsertRequestData.getMdOnlinePrice().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOnlinePrice().get());
		this.mdGoodsVatrate = goodsInsertRequestData.getMdGoodsVatrate() == null? this.mdGoodsVatrate : goodsInsertRequestData.getMdGoodsVatrate().get() == null || goodsInsertRequestData.getMdGoodsVatrate().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate().get());
		this.buySupplyDiscount = goodsInsertRequestData.getBuySupplyDiscount() == null? this.buySupplyDiscount : goodsInsertRequestData.getBuySupplyDiscount().get() == null || goodsInsertRequestData.getBuySupplyDiscount().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuySupplyDiscount().get());
		this.buyRrpIncrement = goodsInsertRequestData.getBuyRrpIncrement() == null? this.buyRrpIncrement : goodsInsertRequestData.getBuyRrpIncrement().get() == null || goodsInsertRequestData.getBuyRrpIncrement().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyRrpIncrement().get());
		this.buyExchangeRate = goodsInsertRequestData.getBuyExchangeRate() == null? this.buyExchangeRate : goodsInsertRequestData.getBuyExchangeRate().get() == null || goodsInsertRequestData.getBuyExchangeRate().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyExchangeRate().get());
		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate() == null? this.mdDiscountRate : goodsInsertRequestData.getMdDiscountRate().get() == null || goodsInsertRequestData.getMdDiscountRate().get().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdDiscountRate().get());

//		this.assortNm = goodsInsertRequestData.getAssortNm() == null? this.assortNm : goodsInsertRequestData.getAssortNm().get();
//		this.assortModel = goodsInsertRequestData.getAssortModel().get();
//		this.taxGb = goodsInsertRequestData.getTaxGb().get();
//		this.assortGb = goodsInsertRequestData.getAssortGb().get();
//		this.assortState = goodsInsertRequestData.getAssortState().get();
//		this.margin = goodsInsertRequestData.getMargin() == null || goodsInsertRequestData.getMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMargin());
//		this.asWidth = goodsInsertRequestData.getAsWidth() == null || goodsInsertRequestData.getAsWidth().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsWidth());
//		this.asLength = goodsInsertRequestData.getAsLength() == null || goodsInsertRequestData.getAsLength().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsLength());
//		this.asHeight = goodsInsertRequestData.getAsHeight() == null || goodsInsertRequestData.getAsHeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getAsHeight());
//		this.weight = goodsInsertRequestData.getWeight() == null || goodsInsertRequestData.getWeight().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getWeight());
//		this.origin = goodsInsertRequestData.getOrigin().get();
//		this.shortageYn = goodsInsertRequestData.getShortageYn().get();
//		this.brandId = goodsInsertRequestData.getBrandId().get();
//		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId().get();
//		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
//		this.ownerId = goodsInsertRequestData.getAsVendorId().get();
//		this.manufactureNm = goodsInsertRequestData.getManufactureNm().get();
//		this.deliPrice = goodsInsertRequestData.getDeliPrice() == null || goodsInsertRequestData.getDeliPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getDeliPrice());
//		this.localPrice = goodsInsertRequestData.getLocalPrice() == null || goodsInsertRequestData.getLocalPrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalPrice());
//		this.localSale = goodsInsertRequestData.getLocalSale() == null || goodsInsertRequestData.getLocalSale().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getLocalSale());
//		this.mdRrp = goodsInsertRequestData.getMdRrp() == null || goodsInsertRequestData.getMdRrp().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdRrp());
//		this.mdMargin = goodsInsertRequestData.getMdMargin() == null || goodsInsertRequestData.getMdMargin().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdMargin());
//		this.mdVatrate = goodsInsertRequestData.getMdVatrate() == null || goodsInsertRequestData.getMdVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdVatrate());
//		this.mdOfflinePrice = goodsInsertRequestData.getMdOfflinePrice() == null || goodsInsertRequestData.getMdOfflinePrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOfflinePrice());
//		this.mdOnlinePrice = goodsInsertRequestData.getMdOnlinePrice() == null || goodsInsertRequestData.getMdOnlinePrice().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdOnlinePrice());
//		this.mdGoodsVatrate = goodsInsertRequestData.getMdGoodsVatrate() == null || goodsInsertRequestData.getMdGoodsVatrate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate());
//		this.buySupplyDiscount = goodsInsertRequestData.getBuySupplyDiscount() == null || goodsInsertRequestData.getBuySupplyDiscount().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuySupplyDiscount());
//		this.buyRrpIncrement = goodsInsertRequestData.getBuyRrpIncrement() == null || goodsInsertRequestData.getBuyRrpIncrement().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyRrpIncrement());
//		this.buyExchangeRate = goodsInsertRequestData.getBuyExchangeRate() == null || goodsInsertRequestData.getBuyExchangeRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getBuyExchangeRate());
//		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate() == null || goodsInsertRequestData.getMdDiscountRate().trim().equals("")? null : Float.parseFloat(goodsInsertRequestData.getMdDiscountRate());
//		this.assortColor = goodsInsertRequestData.getAssortColor().get();
//		this.sellStaDt = goodsInsertRequestData.getSellStaDt().get();//.toLocalDateTime();
//		this.sellEndDt = goodsInsertRequestData.getSellEndDt().get();//.toLocalDateTime();
//		this.taxGb = goodsInsertRequestData.getTaxGb().get();
//		this.mdTax = goodsInsertRequestData.getMdTax().get();
//		this.mdYear = goodsInsertRequestData.getMdYear().get();
//		this.buyWhere = goodsInsertRequestData.getBuyWhere().get();
//		this.buyTax = goodsInsertRequestData.getBuyTax().get();
//		this.optionGbName = goodsInsertRequestData.getOptionGbName().get();
//		this.vendorId = goodsInsertRequestData.getVendorId().get();
//
//		this.optionUseYn = goodsInsertRequestData.getOptionUseYn().get();
	}

	@Id
	private String assortId;
	private String assortNm;
	private String assortDnm;
	private String assortEnm;
	private String assortModel;
	private String payMthCd;
	private String couponYn;
	private String packYn;
	private String deliConfirmYn;
	private String deliMth;
	private String addDeliGb;
	private Float deliCharge;
	private Long deliInterval;
	private Float margin;
	private String marginGb;
	private String marginApp;
	private String taxGb;
	private String adultGb;
	private String unionApply;
	private String cardFreeGb;
	private String divideMth;
	private String assortGb;
	private String assortState;
	private String reasonCd;
	private String vendorTrGb;
	private String siteUrl;
	private String luxuryYn;
	private Float asWidth;
	private Float asLength;
	private Float asHeight;
	private Float weight;
	private Long imgCnt;
	private String cardLimitYn;
	private String searchYn;
	private String sizeId;
	private String origin;
	private String quality;
	private Long leadTime;
	private String deliSure;
	private String reserveYn;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime resStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime resEndDt;
	private String claimSureYn;
	private String defaultYn;
	private String recommYn;
	private Long recommCnt;
	private Long recommQty;
	private String btMarkYn;
	private String shortageYn;
	private String sendbackRejectYn;
	private String setGb;
	private String userId;
	private String categoryId;
	private String brandId;
	private String dispCategoryId;
	private String hsCode;
	private String unit;
	private String vendorId;
	private String alAssortId;
	private String drtSalesGb;
	private String templateId;
	private String drtSalesRatio;
	private String speTaxYn;
	private String preItemYn;
	private String srhExpYn;
	private String nonsaleYn;
	private String siteGb;
	private String ownerId;
	private String deliNrgGb;
	private String manufactureNm;
	private String delayRewardYn;
	private Float callDisLimit;
	private Float deliPrice;
	private Float localPrice;
	private Float initLocalPrice;
	private Float localSale;
	private Float estiPrice;
	private String invoiceNm;
	private Float localDeliFee;
	private String cardFreeYn;
	private String itemAbbNm;
	private String disGb;
	private Float disRate;
	private Float reserveGive;
	private Float bonusReserve;
	private Float cashbagPoint;
	private String plGbn;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime plFromDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime plToDt;
	private String storageId;
	private String optionGb;
	private String shopSaleGb;
	private String sendbackPayGb;
	private String sendbackChangeYn;
	private String directPathNrgCd;
	private String marginCd;
	private String directPathGb;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime resShipStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime resShipEndDt;
	private String imgType;
	private String onlinedispYn;
	private String payType;
	private String freeGiftYn;
	private String currencyUnit;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime disStartDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime disEndDt;
	private String workGb;
	private Float cardFee;
	private String assortGrade;
	private String assortColor;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime sellStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime sellEndDt;
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
	private Float mdDiscountRate;
	private String optionGbName;
	private String optionUseYn;
	// 21-12-02 컬럼 추가
	private String custCategory;
	// 21-12-23 추가
	private String listImageData;
	private String addGoodsYn;
	private String addOptionNm;
	private String mainImageUrl;
	private Long stockCnt;
	// 22-03-25 추가
	private String channelGoodsNo;

	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY) // itvari 연관관계
	private List<Itvari> itvariList;

	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY) // Itbrnd 연관관계
	private Itbrnd itbrnd;

	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY) // ititmm 연관관계
	private List<Ititmm> ititmmList;

	@JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY) // itasrd 연관관계
	@JsonIgnore
	private List<Itasrd> itasrdList;

	@JoinColumn(name="dispCategoryId", referencedColumnName = "categoryId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Itcatg itcatg; // itcatg 연관관계

	@JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Itaimg> itaimg; // itaimg 연관관계

	@JoinColumn(name = "vendorId", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	// @LazyToOne(value = LazyToOneOption.NO_PROXY)
	private Cmvdmr cmvdmr; // cmvdmr 연관관계

}
