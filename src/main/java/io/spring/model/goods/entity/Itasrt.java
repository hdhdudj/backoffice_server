package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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

	public Itasrt(GoodsRequestData goodsRequestData){
		this.assortId = goodsRequestData.getAssortId();
		this.assortNm = goodsRequestData.getAssortNm();
		this.assortModel = goodsRequestData.getAssortModel();
		this.margin = goodsRequestData.getMargin();
		this.taxGb = goodsRequestData.getTaxGb();
		this.assortGb = goodsRequestData.getAssortGb();
		this.assortState = goodsRequestData.getAssortState();
//		this.asWidth = goodsRequestData.getAsWidth();
//		this.asLength = goodsRequestData.getAsLength();
//		this.asHeight = goodsRequestData.getAsHeight();
//		this.weight = goodsRequestData.getWeight();
		this.origin = goodsRequestData.getOrigin();
		this.shortageYn = goodsRequestData.getShortageYn();
		this.brandId = goodsRequestData.getBrandId();
		this.dispCategoryId = goodsRequestData.getDispCategoryId();
		this.siteGb = "01";
		this.asVendorId = goodsRequestData.getAsVendorId();
		this.manufactureNm = goodsRequestData.getManufactureNm();
//		this.deliPrice = goodsRequestData.getDeliPrice();
//		this.localPrice = goodsRequestData.getLocalPrice();
//		this.localSale = goodsRequestData.getLocalSale();
		this.assortColor = goodsRequestData.getAssortColor();
		this.sellStaDt = goodsRequestData.getSellStaDt();
		this.sellEndDt = goodsRequestData.getSellEndDt();
		this.taxGb = goodsRequestData.getTaxGb();
		this.mdRrp = goodsRequestData.getMdRrp();
		this.mdTax = goodsRequestData.getMdTax();
		this.mdYear = goodsRequestData.getMdYear();
		this.mdMargin = goodsRequestData.getMdMargin();
		this.mdMargin = goodsRequestData.getMdMargin();
		this.mdVatrate = goodsRequestData.getMdVatrate();
		this.mdOfflinePrice = goodsRequestData.getMdOfflinePrice();
		this.mdOnlinePrice = goodsRequestData.getMdOnlinePrice();
		this.mdGoodsVatrate = goodsRequestData.getMdGoodsVatrate();
		this.buyWhere = goodsRequestData.getBuyWhere();
		this.buyTax = goodsRequestData.getBuyTax();
		this.buySupplyDiscount = goodsRequestData.getBuySupplyDiscount();
		this.buyRrpIncrement = goodsRequestData.getBuyRrpIncrement();
		this.buyExchangeRate = goodsRequestData.getBuyExchangeRate();
		this.sizeType = goodsRequestData.getSizeType();
		this.mdDiscountRate = goodsRequestData.getMdDiscountRate();
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
