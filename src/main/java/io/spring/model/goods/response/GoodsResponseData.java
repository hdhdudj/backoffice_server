package io.spring.model.goods.response;

import java.util.List;

import io.spring.infrastructure.util.PropertyUtil;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.entity.TbGoods;
import io.spring.model.goods.entity.TbGoodsAddInfo;
import io.spring.model.goods.entity.TbGoodsImage;
import io.spring.model.goods.entity.TbGoodsOptionSupplier;
import io.spring.model.goods.entity.TbGoodsOptionValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoodsResponseData {

	public GoodsResponseData(TbGoods tbGoods) {
		this.assortId = tbGoods.getAssortId();
		this.assortNm = tbGoods.getAssortNm();
		this.assortEnm = tbGoods.getAssortEnm();
		this.assortDnm = tbGoods.getAssortDnm();
		this.assortGb = tbGoods.getAssortGb();
		this.assortColor = tbGoods.getAssortColor();
		this.brandId = tbGoods.getBrandId();
		this.origin = tbGoods.getOrigin();
		this.manufactureNm = tbGoods.getManufactureNm();
		this.assortModel = tbGoods.getAssortModel();
		this.optionGbName = tbGoods.getOptionGbName();
		this.taxGb = tbGoods.getTaxGb();
		this.assortState = tbGoods.getAssortState();
		this.shortageYn = tbGoods.getShortageYn();
		this.localPrice = tbGoods.getLocalPrice() == null ? null : tbGoods.getLocalPrice() + "";
		this.localSale = tbGoods.getLocalSale() == null ? null : tbGoods.getLocalSale() + "";
		this.localDeliFee = tbGoods.getLocalDeliFee() == null ? null : tbGoods.getLocalDeliFee() + "";
		this.margin = tbGoods.getMargin() == null ? null : tbGoods.getMargin() + "";
		this.mdRrp = tbGoods.getMdRrp() == null ? null : tbGoods.getMdRrp() + "";
		this.mdYear = tbGoods.getMdYear();
		this.mdVatrate = tbGoods.getMdVatrate() == null ? null : tbGoods.getMdVatrate() + "";
		this.mdDiscountRate = tbGoods.getMdDiscountRate() == null ? null : tbGoods.getMdDiscountRate() + "";
		this.mdGoodsVatrate = tbGoods.getMdGoodsVatrate() == null ? null : tbGoods.getMdGoodsVatrate() + "";
		this.mdMargin = tbGoods.getMdMargin() == null ? null : tbGoods.getMdMargin() + "";
		this.buyWhere = tbGoods.getBuyWhere();
		this.buySupplyDiscount = tbGoods.getBuySupplyDiscount() == null ? null : tbGoods.getBuySupplyDiscount() + "";
		this.buyExchangeRate = tbGoods.getBuyExchangeRate() == null ? null : tbGoods.getBuyExchangeRate() + "";
		this.buyRrpIncrement = tbGoods.getBuyRrpIncrement() == null ? null : tbGoods.getBuyRrpIncrement() + "";

		this.sellStaDt = Utilities.removeTAndTransToStr(tbGoods.getSellStaDt());
		this.sellEndDt = Utilities.removeTAndTransToStr(tbGoods.getSellEndDt());

		this.deliPrice = tbGoods.getDeliPrice() == null ? null : Float.toString(tbGoods.getDeliPrice());
		this.asWidth = tbGoods.getAsWidth() == null ? null : Float.toString(tbGoods.getAsWidth());
		this.asLength = tbGoods.getAsLength() == null ? null : Float.toString(tbGoods.getAsLength());
		this.asHeight = tbGoods.getAsHeight() == null ? null : Float.toString(tbGoods.getAsHeight());
		this.weight = tbGoods.getWeight() == null ? null : Float.toString(tbGoods.getWeight());

		this.buyTax = tbGoods.getBuyTax();
		this.mdTax = tbGoods.getMdTax();
		this.vendorId = tbGoods.getVendorId();

//		this.brandNm = (tbGoods.getIfBrand() != null ? tbGoods.getIfBrand().getBrandNm() : ""); ???????????? set

		this.optionUseYn = tbGoods.getOptionUseYn();

		this.dispCategoryId = tbGoods.getDispCategoryId();

		this.goodsDescription = tbGoods.getGoodsDescription();
		this.shortDescription = tbGoods.getShortDescription();

		// this.brandNm = itasr

//        this.regDt = tbGoods.getRegDt();
//        this.updDt = tbGoods.getUpdDt();
	}

	private String assortId;
//	    @CreationTimestamp
//	    private Date regDt;
	private String regId;
	private String updId;
//	    @UpdateTimestamp
//	    private Date updDt;

	// ?????????????????? ??????
	private String assortNm; // ?????????

	private String assortDnm; // ?????????
	private String assortEnm; // ?????????

	private String assortModel; // ????????????
	private String taxGb; // ??????/??????
	private String assortState; // ???????????? : ?????????(01), ????????????(02), ??????(03), ??????(04)
	private String shortageYn; // ???????????? : ?????????(01), ??????(02)
	private String asLength; // ??????
	private String asHeight; // ??????
	private String asWidth; // ??????
	private String weight; // ??????
	private String origin; // ?????????
	private String brandId; // ????????? ??????
	private String dispCategoryId; // erp ???????????? ??????
	private String manufactureNm; // ????????????
	private String localSale; // ?????????
//	    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private String sellStaDt; // ???????????? - ??????
//	    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
	private String sellEndDt; // ???????????? - ???
	private String deliPrice; // ?????????
	private String localPrice; // ??????
	private String margin; // ?????????

	// ????????? ?????? ??????

	// ?????? ?????? ??????(MD???) ??????
	private String mdRrp; // RRP
	private String mdYear; // ????????????
	private String mdTax; // TAX(??????)
	private String mdVatrate; // ????????????
	private String mdDiscountRate; // ?????????
	private String mdGoodsVatrate; // ???????????????

	// ?????? ?????? ??????(?????????) ??????
	private String vendorNm; // ????????????
	private String vendorId; // ????????? ??????
	private String buyRrpIncrement; // RRP ?????????
	private String buySupplyDiscount; //
	private String buyTax; // TAX(??????)
	private String mdMargin; // ???????????????
	private String buyExchangeRate; // ????????????

	private String optionGbName;
	private String assortGb;
	private String siteGb;
	private String asVendorId;
	private String localDeliFee;
	private String assortColor;

	private String mdOfflinePrice;
	private String mdOnlinePrice;
	private String buyWhere;
	private String sizeType;
	private String optionUseYn;

	private String brandNm;

	private String goodsDescription;
	private String shortDescription;

	private List<AddInfo> addInfos;

	// ititmm
//	    @SerializedName("items")
//	    @Expose
	private List<GoodsResponseData.Items> items;

	// itvari
//	    @SerializedName("attributes")
//	    @Expose
	private List<GoodsResponseData.Attributes> attributes;

	// ititmm
	@Getter
	@Setter
	public static class Items {
		// private String assortId;
		private String itemId;
		private String seq1;
		private String seq2;
		private String seq3;
		private String value1;
		private String value2;
		private String value3;
		private String addPrice;
		private String shortageYn;
		private String status1;
		private String status2;
		private String status3;
		List<itemSupplier> itemSupplier;
	}

	@Getter
	@Setter
	public static class itemSupplier {
		private Long sno; // key
		private String itemId;
		private String supplierId;
		private float salePrice;
		private float offlineSalePrice;
		private float overseasSalePrice;
		private Long stockCnt;
		private String saleYn;

		public itemSupplier(TbGoodsOptionSupplier o) {

			this.sno = o.getSno(); // key
			this.itemId = o.getItemId();
			this.supplierId = o.getSupplierId();
			this.salePrice = o.getSalePrice();
			this.stockCnt = o.getStockCnt();
			this.saleYn = o.getSaleYn();
			
			this.offlineSalePrice = o.getOfflineSalePrice();
			this.overseasSalePrice = o.getOverseasSalePrice();


		}

	}

	// image ????????????
	private List<UploadMainImage> uploadMainImage;
	private List<UploadAddImage> uploadAddImage;
	private List<String> deleteImage;

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UploadMainImage {
		public UploadMainImage(Itaimg itaimg) {

			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");

			this.uid = itaimg.getImageSeq() + "";
			this.name = itaimg.getImageName();
			this.imageGb = itaimg.getImageGb();
			this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();

		}

		public UploadMainImage(TbGoodsImage o) {

			this.uid = o.getItaimg() == null ? "temp-" + o.getSno() : o.getItaimg().getImageSeq().toString();
			// this.name = o.getItaimg() == null ? "" : o.getItaimg().getImageName();
			this.name = o.getItaimg() == null ? o.getImageUrl().substring(o.getImageUrl().lastIndexOf("/") + 1)
					: o.getItaimg().getImageName();

			this.imageGb = o.getItaimg() == null ? "01" : o.getItaimg().getImageGb(); // itaimg.getImageGb();
			this.status = o.getItaimg() == null ? "01" : o.getItaimg().getImageStatus(); // itaimg.getImageStatus();
			this.url = o.getImageUrl();
			this.sno = o.getSno();

		}

		private String uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class UploadAddImage {
		public UploadAddImage(Itaimg itaimg) {
			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");

			this.uid = itaimg.getImageSeq() + "";
			this.name = itaimg.getImageName();
			this.imageGb = itaimg.getImageGb();
			this.status = itaimg.getImageStatus();
			this.url = prefixUrl + itaimg.getImagePath() + itaimg.getImageName();
		}

		public UploadAddImage(TbGoodsImage o) {

			this.uid = o.getItaimg() == null ? "temp-" + o.getSno() : o.getItaimg().getImageSeq().toString();
			this.name = o.getItaimg() == null
					? o.getImageUrl().substring(o.getImageUrl().lastIndexOf("/") + 1)
					: o.getItaimg().getImageName();
			this.imageGb = o.getItaimg() == null ? "02" : o.getItaimg().getImageGb(); // itaimg.getImageGb();
			this.status = o.getItaimg() == null ? "01" : o.getItaimg().getImageStatus(); // itaimg.getImageStatus();
			this.url = o.getImageUrl();
			this.sno = o.getSno();

		}

		private String uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
	}

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Attributes {
		public Attributes(Itvari itvari) {
			this.seq = itvari.getSeq();
			this.value = itvari.getOptionNm();
			this.variationGb = itvari.getVariationGb();
		}

		public Attributes(TbGoodsOptionValue tgov) {
			this.seq = tgov.getSeq();
			this.value = tgov.getOptionNm();
			this.variationGb = tgov.getVariationGb();
		}

		private String seq;
		private String value;
		private String variationGb;
	}

	@Getter
	@Setter
	public static class AddInfo {

		public AddInfo(TbGoodsAddInfo tgai) {
			this.sno = tgai.getSno();
			this.infoTitle = tgai.getInfoTitle();
			this.infoValue = tgai.getInfoValue();
		}

		private Long sno;
		private String infoTitle;
		private String infoValue;

	}
}
