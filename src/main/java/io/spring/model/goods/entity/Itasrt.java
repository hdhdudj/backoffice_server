package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *  ITASRT table의 Entity
 *  ITASRT : 상품 정보 table
 */

@Entity
@Table(name = "itasrt")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itasrt extends CommonProps implements PersistentAttributeInterceptable{

	public Itasrt(GoodsInsertRequestData goodsInsertRequestData){
		this.assortId = goodsInsertRequestData.getAssortId();
		this.assortNm = goodsInsertRequestData.getAssortNm();
		this.assortModel = goodsInsertRequestData.getAssortModel();
		this.margin = goodsInsertRequestData.getMargin().trim().equals("") || goodsInsertRequestData.getMargin() == null? null : Float.parseFloat(goodsInsertRequestData.getMargin());
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.assortGb = goodsInsertRequestData.getAssortGb();
		this.assortState = goodsInsertRequestData.getAssortState();
		this.asWidth = goodsInsertRequestData.getAsWidth().trim().equals("") || goodsInsertRequestData.getAsWidth() == null? null : Float.parseFloat(goodsInsertRequestData.getAsWidth());
		this.asLength = goodsInsertRequestData.getAsLength().trim().equals("") || goodsInsertRequestData.getAsLength() == null? null : Float.parseFloat(goodsInsertRequestData.getAsLength());
		this.asHeight = goodsInsertRequestData.getAsHeight().trim().equals("") || goodsInsertRequestData.getAsHeight() == null? null : Float.parseFloat(goodsInsertRequestData.getAsHeight());
		this.weight = goodsInsertRequestData.getWeight().trim().equals("") || goodsInsertRequestData.getWeight() == null? null : Float.parseFloat(goodsInsertRequestData.getWeight());
		this.origin = goodsInsertRequestData.getOrigin();
		this.shortageYn = goodsInsertRequestData.getShortageYn();
		this.brandId = goodsInsertRequestData.getBrandId();
		this.dispCategoryId = goodsInsertRequestData.getDispCategoryId();
		this.siteGb = StringFactory.getGbOne(); // 01 하드코딩
		this.ownerId = goodsInsertRequestData.getAsVendorId();
		this.manufactureNm = goodsInsertRequestData.getManufactureNm();
		this.deliPrice = goodsInsertRequestData.getDeliPrice().trim().equals("") || goodsInsertRequestData.getDeliPrice() == null? null : Float.parseFloat(goodsInsertRequestData.getDeliPrice());
		this.localPrice = goodsInsertRequestData.getLocalPrice().trim().equals("") || goodsInsertRequestData.getLocalPrice() == null? null : Float.parseFloat(goodsInsertRequestData.getLocalPrice());
		this.localSale = goodsInsertRequestData.getLocalSale().trim().equals("") || goodsInsertRequestData.getLocalSale() == null? null : Float.parseFloat(goodsInsertRequestData.getLocalSale());
	//	this.localDeliFee = goodsInsertRequestData.getLocalDeliFee();
		this.assortColor = goodsInsertRequestData.getAssortColor();
		this.sellStaDt = goodsInsertRequestData.getSellStaDt();//.toLocalDateTime();
		this.sellEndDt = goodsInsertRequestData.getSellEndDt();//.toLocalDateTime();
		this.taxGb = goodsInsertRequestData.getTaxGb();
		this.mdRrp = goodsInsertRequestData.getMdRrp().trim().equals("") || goodsInsertRequestData.getMdRrp() == null? null : Float.parseFloat(goodsInsertRequestData.getMdRrp());
		this.mdTax = goodsInsertRequestData.getMdTax();
		this.mdYear = goodsInsertRequestData.getMdYear();
		this.mdMargin = goodsInsertRequestData.getMdMargin().trim().equals("") || goodsInsertRequestData.getMdMargin() == null? null : Float.parseFloat(goodsInsertRequestData.getMdMargin());
		this.mdVatrate = goodsInsertRequestData.getMdVatrate().trim().equals("") || goodsInsertRequestData.getMdVatrate() == null? null : Float.parseFloat(goodsInsertRequestData.getMdVatrate());
		this.mdOfflinePrice = goodsInsertRequestData.getMdOfflinePrice().trim().equals("") || goodsInsertRequestData.getMdOfflinePrice() == null? null : Float.parseFloat(goodsInsertRequestData.getMdOfflinePrice());
		this.mdOnlinePrice = goodsInsertRequestData.getMdOnlinePrice().trim().equals("") || goodsInsertRequestData.getMdOnlinePrice() == null? null : Float.parseFloat(goodsInsertRequestData.getMdOnlinePrice());
		this.mdGoodsVatrate = goodsInsertRequestData.getMdGoodsVatrate().trim().equals("") || goodsInsertRequestData.getMdGoodsVatrate() == null? null : Float.parseFloat(goodsInsertRequestData.getMdGoodsVatrate());
		this.buyWhere = goodsInsertRequestData.getBuyWhere();
		this.buyTax = goodsInsertRequestData.getBuyTax();
		this.buySupplyDiscount = goodsInsertRequestData.getBuySupplyDiscount().trim().equals("") || goodsInsertRequestData.getBuySupplyDiscount() == null? null : Float.parseFloat(goodsInsertRequestData.getBuySupplyDiscount());
		this.buyRrpIncrement = goodsInsertRequestData.getBuyRrpIncrement().trim().equals("") || goodsInsertRequestData.getBuyRrpIncrement() == null? null : Float.parseFloat(goodsInsertRequestData.getBuyRrpIncrement());
		this.buyExchangeRate = goodsInsertRequestData.getBuyExchangeRate().trim().equals("") || goodsInsertRequestData.getBuyExchangeRate() == null? null : Float.parseFloat(goodsInsertRequestData.getBuyExchangeRate());
//		this.sizeType = goodsInsertRequestData.getSizeType();
		this.mdDiscountRate = goodsInsertRequestData.getMdDiscountRate().trim().equals("") || goodsInsertRequestData.getMdDiscountRate() == null? null : Float.parseFloat(goodsInsertRequestData.getMdDiscountRate());
		this.optionGbName = goodsInsertRequestData.getOptionGbName();
		this.vendorId = goodsInsertRequestData.getVendorId();

		this.optionUseYn = goodsInsertRequestData.getOptionUseYn();
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
	private Date resStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date resEndDt;
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
	private Date plFromDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date plToDt;
	private String storageId;
	private String optionGb;
	private String shopSaleGb;
	private String sendbackPayGb;
	private String sendbackChangeYn;
	private String directPathNrgCd;
	private String marginCd;
	private String directPathGb;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date resShipStaDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date resShipEndDt;
	private String imgType;
	private String onlinedispYn;
	private String payType;
	private String freeGiftYn;
	private String currencyUnit;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date disStartDt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
	private Date disEndDt;
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

	//// 다른 테이블과 엮으면 나오는 프로퍼티들
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY) // itvari 연관관계
	private List<Itvari> itvariList;

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


//	@JoinColumn(name="brandId", referencedColumnName = "channelBrandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JsonIgnore
//	@NotFound(action = NotFoundAction.IGNORE)
//	private IfBrand ifBrand; // ifBrand 연관관계

	@JoinColumn(name="dispCategoryId", referencedColumnName = "categoryId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Itcatg itcatg; // itcatg 연관관계

	@JoinColumn(name="assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@BatchSize(size = 100)
	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Itaimg> itaimg; // itaimg 연관관계

	@JoinColumn(name = "vendorId", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
//	@NotFound(action = NotFoundAction.IGNORE)
	private Cmvdmr cmvdmr; // cmvdmr 연관관계

	@JoinColumn(name = "brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@LazyToOne(value = LazyToOneOption.PROXY)
//	@NotFound(action = NotFoundAction.IGNORE)
	private IfBrand ifBrand;
	public IfBrand getIfBrand() {
		if (interceptor!=null) {
			return (IfBrand)interceptor.readObject(this, "ifBrand", ifBrand);
		}
		return ifBrand;
	}

	public void setIfBrand(IfBrand ifBrand) {
		if (interceptor!=null) {
			this.ifBrand = (IfBrand) interceptor.writeObject(this,"ifBrand", this.ifBrand, ifBrand);
			return ;
		}
		this.ifBrand = ifBrand;
	}

	@Transient
	private PersistentAttributeInterceptor interceptor;

	@Override
	public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
		return interceptor;
	}

	@Override
	public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor interceptor) {
		this.interceptor = interceptor;
	}
}
