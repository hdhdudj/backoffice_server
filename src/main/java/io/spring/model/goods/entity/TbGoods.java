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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsPostRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TbGoods table의 Entity TbGoods : 상품 정보 table
 */

@DynamicUpdate
@Entity
@Table(name = "tb_goods")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class TbGoods extends CommonProps implements PersistentAttributeInterceptable, Serializable {
public class TbGoods extends CommonProps implements Serializable {

	public TbGoods(GoodsPostRequestData goodsPostRequestData) {
		this.assortId = goodsPostRequestData.getAssortId();
		this.assortNm = goodsPostRequestData.getAssortNm();
		this.assortModel = goodsPostRequestData.getAssortModel();
		this.taxGb = goodsPostRequestData.getTaxGb();
		this.assortGb = goodsPostRequestData.getAssortGb();
		this.assortState = goodsPostRequestData.getAssortState();
		this.margin = goodsPostRequestData.getMargin() == null || goodsPostRequestData.getMargin().trim().equals("")
				? null
				: Float.parseFloat(goodsPostRequestData.getMargin());
		this.asWidth = goodsPostRequestData.getAsWidth() == null || goodsPostRequestData.getAsWidth().trim().equals("")
				? null
				: Float.parseFloat(goodsPostRequestData.getAsWidth());
		this.asLength = goodsPostRequestData.getAsLength() == null
				|| goodsPostRequestData.getAsLength().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getAsLength());
		this.asHeight = goodsPostRequestData.getAsHeight() == null
				|| goodsPostRequestData.getAsHeight().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getAsHeight());
		this.weight = goodsPostRequestData.getWeight() == null || goodsPostRequestData.getWeight().trim().equals("")
				? null
				: Float.parseFloat(goodsPostRequestData.getWeight());
		this.origin = goodsPostRequestData.getOrigin();
		this.shortageYn = goodsPostRequestData.getShortageYn();
		this.brandId = goodsPostRequestData.getBrandId();
		this.dispCategoryId = goodsPostRequestData.getDispCategoryId();
		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
		this.ownerId = goodsPostRequestData.getAsVendorId();
		this.manufactureNm = goodsPostRequestData.getManufactureNm();
		this.deliPrice = goodsPostRequestData.getDeliPrice() == null
				|| goodsPostRequestData.getDeliPrice().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getDeliPrice());
		this.localPrice = goodsPostRequestData.getLocalPrice() == null
				|| goodsPostRequestData.getLocalPrice().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getLocalPrice());
		this.localSale = goodsPostRequestData.getLocalSale() == null
				|| goodsPostRequestData.getLocalSale().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getLocalSale());
		this.mdRrp = goodsPostRequestData.getMdRrp() == null || goodsPostRequestData.getMdRrp().trim().equals("")
				? null
				: Float.parseFloat(goodsPostRequestData.getMdRrp());
		this.mdMargin = goodsPostRequestData.getMdMargin() == null
				|| goodsPostRequestData.getMdMargin().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdMargin());
		this.mdVatrate = goodsPostRequestData.getMdVatrate() == null
				|| goodsPostRequestData.getMdVatrate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdVatrate());
		this.mdOfflinePrice = goodsPostRequestData.getMdOfflinePrice() == null
				|| goodsPostRequestData.getMdOfflinePrice().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdOfflinePrice());
		this.mdOnlinePrice = goodsPostRequestData.getMdOnlinePrice() == null
				|| goodsPostRequestData.getMdOnlinePrice().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdOnlinePrice());
		this.mdGoodsVatrate = goodsPostRequestData.getMdGoodsVatrate() == null
				|| goodsPostRequestData.getMdGoodsVatrate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdGoodsVatrate());
		this.buySupplyDiscount = goodsPostRequestData.getBuySupplyDiscount() == null
				|| goodsPostRequestData.getBuySupplyDiscount().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuySupplyDiscount());
		this.buyRrpIncrement = goodsPostRequestData.getBuyRrpIncrement() == null
				|| goodsPostRequestData.getBuyRrpIncrement().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuyRrpIncrement());
		this.buyExchangeRate = goodsPostRequestData.getBuyExchangeRate() == null
				|| goodsPostRequestData.getBuyExchangeRate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getBuyExchangeRate());
		this.mdDiscountRate = goodsPostRequestData.getMdDiscountRate() == null
				|| goodsPostRequestData.getMdDiscountRate().trim().equals("") ? null
						: Float.parseFloat(goodsPostRequestData.getMdDiscountRate());
//		this.sizeType = goodsInsertRequestData.getSizeType();
		// this.localDeliFee = goodsInsertRequestData.getLocalDeliFee();
		this.assortColor = goodsPostRequestData.getAssortColor();
		this.sellStaDt = goodsPostRequestData.getSellStaDt();// .toLocalDateTime();
		this.sellEndDt = goodsPostRequestData.getSellEndDt();// .toLocalDateTime();
		this.taxGb = goodsPostRequestData.getTaxGb();
		this.mdTax = goodsPostRequestData.getMdTax();
		this.mdYear = goodsPostRequestData.getMdYear();
		this.buyWhere = goodsPostRequestData.getBuyWhere();
		this.buyTax = goodsPostRequestData.getBuyTax();
		this.optionGbName = goodsPostRequestData.getOptionGbName();
		this.vendorId = goodsPostRequestData.getVendorId();

		this.optionUseYn = goodsPostRequestData.getOptionUseYn();

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

	private String goodsDescription;
	private String shortDescription;

//
//	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY) // itvari 연관관계
	private List<TbGoodsOptionValue> tbGoodsOptionValueList;
//
//	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY) // Itbrnd 연관관계
	private Itbrnd itbrnd;
//
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	@JsonIgnore
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY) // ititmm 연관관계
	private List<TbGoodsOption> tbGoodsOptionList;
//
//	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	@BatchSize(size = 100)
//	@OneToMany(fetch = FetchType.LAZY) // itasrd 연관관계
//	@JsonIgnore
//	private List<Itasrd> itasrdList;
//
	@JoinColumn(name = "dispCategoryId", referencedColumnName = "categoryId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Itcatg itcatg; // itcatg 연관관계
//
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY)
//	@JsonIgnore
	private List<Itaimg> itaimg; // itaimg 연관관계
//
	@JoinColumn(name = "vendorId", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Cmvdmr cmvdmr; // cmvdmr 연관관계

}
