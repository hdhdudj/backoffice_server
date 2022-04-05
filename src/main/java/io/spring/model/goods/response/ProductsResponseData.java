package io.spring.model.goods.response;

import java.util.List;

import io.spring.model.goods.entity.Products;
import io.spring.model.goods.entity.ProductsAddInfo;
import io.spring.model.goods.entity.ProductsImage;
import io.spring.model.goods.entity.ProductsMaster;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class ProductsResponseData {

	public ProductsResponseData(Products p) {
		
		this.productId=p.getProductId();
		this.productNm=p.getProductNm();
		this.productDnm=p.getProductDnm();
		this.productEnm=p.getProductEnm();
		this.productModel=p.getProductModel();

		this.supplierId=p.getSupplierId();
		this.supplierNm=p.getSuppliers()==null?null:p.getSuppliers().getSupplierNm();
		this.salePrice=p.getSalePrice();
		this.offlineSalePrice=p.getOfflineSalePrice();
		this.overseasSalePrice=p.getOverseasSalePrice();

		this.optionValue1=p.getOptionValue1();
		this.optionValue2=p.getOptionValue2();
		this.optionValue3=p.getOptionValue3();
		this.optionValue4=p.getOptionValue4();
		this.optionValue5=p.getOptionValue5();

		this.goodsDescription=p.getGoodsDescription();
		this.shortDescription=p.getShortDescription();

		this.brandId=p.getBrandId();
		this.brandNm = p.getItbrnd() == null ? null : p.getItbrnd().getBrandNm();
		this.categoryId = p.getCategoryId();
		this.categoryNm = p.getCategoryId() == null ? null : p.getCategoryId();
		this.stockCnt = p.getStockCnt();
		this.saleYn = p.getSaleYn();
		this.displayYn = p.getDisplayYn();

		

	}

	private Long productId;
	private String productNm;
	private String productDnm;
	private String productEnm;
	private String productModel;

	private Long supplierId;
	private String supplierNm;
	private float salePrice;
	private float offlineSalePrice;
	private float overseasSalePrice;

	private String optionValue1;
	private String optionValue2;
	private String optionValue3;
	private String optionValue4;
	private String optionValue5;

	private String goodsDescription;
	private String shortDescription;

	private String brandId;
	private String brandNm;
	private String categoryId;
	private String categoryNm;
	private Long stockCnt;
	private String saleYn;
	private String displayYn;
	private String userId;

	private Master master;

	private List<AddInfo> addInfos;

	// image 관련
	private List<MainImage> MainImage;
	private List<AddImage> AddImage;

	@Getter
	@Setter
	public static class Master {

		public Master(ProductsMaster o) {

			this.masterId = o.getMasterId();
			this.masterNm = o.getMasterNm();
			this.masterDescription = o.getMasterDescription();
			this.optionKey1 = o.getOptionKey1();
			this.optionKey2 = o.getOptionKey2();
			this.optionKey3 = o.getOptionKey3();
			this.optionKey4 = o.getOptionKey4();
			this.optionKey5 = o.getOptionKey5();
			
		}

		private Long masterId;
		private String masterNm;
		private String masterDescription;
		private String optionKey1;
		private String optionKey2;
		private String optionKey3;
		private String optionKey4;
		private String optionKey5;

	}

	@Getter
	@Setter
	@ToString
	public static class AddInfo {

		public AddInfo(ProductsAddInfo tgai) {
			this.sno = tgai.getSno();
			this.infoTitle = tgai.getInfoTitle();
			this.infoValue = tgai.getInfoValue();
		}

		private Long sno;
		private String infoTitle;
		private String infoValue;

	}

	@Getter
	@Setter
	public static class MainImage {

		public MainImage(ProductsImage o) {

			if (o.getFileYn().toUpperCase().equals("Y")) {
				this.uid = o.getImageSeq().toString();
				this.name = o.getItaimg().getImageName();


			} else {
				this.uid = "temp-" + o.getSno();
				this.name = o.getImageUrl().substring(o.getImageUrl().lastIndexOf("/") + 1);
			}


			this.imageGb = "01";
			this.status = "01";
			this.url = o.getImageUrl();
			this.sno = o.getSno();
			this.fileYn = o.getFileYn();

		}

		private String uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
		private String fileYn;
	}

	@Getter
	@Setter
	public static class AddImage {

		public AddImage(ProductsImage o) {

			if (o.getFileYn().toUpperCase().equals("Y")) {
				this.uid = o.getImageSeq().toString();
				this.name = o.getItaimg().getImageName();

			} else {
				this.uid = "temp-" + o.getSno();
				this.name = o.getImageUrl().substring(o.getImageUrl().lastIndexOf("/") + 1);
			}

			this.imageGb = "01";
			this.status = "01";
			this.url = o.getImageUrl();
			this.sno = o.getSno();
			this.fileYn = o.getFileYn();

		}

		private String uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
		private String fileYn;
	}


}
