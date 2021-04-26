package io.spring.model.goods.entity;

import io.spring.model.goods.GoodsRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
		this.assortColor = goodsRequestData.getAssortColor();
		this.brandId = goodsRequestData.getBrandId();
		this.origin = goodsRequestData.getOrigin();
		this.manufactureNm = goodsRequestData.getManufactureNm();
		this.assortModel = goodsRequestData.getAssortModel();
		this.taxGb = goodsRequestData.getTaxGb();

//		SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
//		Date time = new Date();
//		String time1 = format.format(time);
//
//		this.regDt = time1;
//		this.updDt = time1;
		this.regId = "123"; // 추후 추가
		this.updId = "123"; // 추후 추가
	}

	@Id
	private String assortId;

	private String regId;
	private String updId;
	@Column(name = "reg_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private String regDt;
	@Column(name = "upd_dt", nullable = false, updatable = false, insertable = false, columnDefinition = "ON UPDATE CURRENT_TIMESTAMP")
	private String updDt;

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
	private String deliCharge;
	private String deliInterval;
	private String margin;
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
	private String asWidth;
	private String asLength;
	private String asHeight;
	private String weight;
	private String imgCnt;
	private String cardLimitYn;
	private String searchYn;
	private String sizeId;
	private String origin;
	private String quality;
	private String leadTime;
	private String deliSure;
	private String reserveYn;
	private String resStaDt;
	private String resEndDt;
	private String claimSureYn;
	private String defaultYn;
	private String recommYn;
	private String recommCnt;
	private String recommQty;
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
	private String asVendorId;
	private String deliNrgGb;
	private String manufactureNm;
	private String delayRewardYn;
	private String callDisLimit;
	private String deliPrice;
	private String localPrice;
	private String initLocalPrice;
	private String localSale;
	private String estiPrice;
	private String invoiceNm;
	private String localDeliFee;
	private String cardFreeYn;
	private String itemAbbNm;
	private String disGb;
	private String disRate;
	private String reserveGive;
	private String bonusReserve;
	private String cashbagPoint;
	private String plGbn;
	private String plFromDt;
	private String plToDt;
	private String storageId;
	private String optionGb;
	private String shopSaleGb;
	private String sendbackPayGb;
	private String sendbackChangeYn;
	private String directPathNrgCd;
	private String marginCd;
	private String directPathGb;
	private String resShipStaDt;
	private String resShipEndDt;
	private String imgType;
	private String onlinedispYn;
	private String payType;
	private String freeGiftYn;
	private String currencyUnit;
	private String disStartDt;
	private String disEndDt;
	private String workGb;
	private String cardFee;
	private String assortGrade;
	private String assortColor;
	private String sellStaDt;
	private String sellEndDt;
	private String mdRrp;
	private String mdTax;
	private String mdYear;
	private String mdMargin;
	private String mdVatrate;
	private String mdOfflinePrice;
	private String mdOnlinePrice;
	private String mdGoodsVatrate;
	private String buyWhere;
	private String buyTax;
	private String buySupplyDiscount;
	private String buyRrpIncrement;
	private String buyExchangeRate;
	private String sizeType;
	private String mdDiscountRate;
}
