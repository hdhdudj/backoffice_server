package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.common.entity.Suppliers;
import io.spring.model.goods.request.ProductsPostRequestData;
import io.spring.model.vendor.entity.Cmvdmr;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Products extends CommonProps implements Serializable {

	public Products(ProductsPostRequestData o) {

		if(o.getProductId()!=null) {
			this.productId = o.getProductId();			
			this.setRegId(o.getUserId());
		}

		this.setUpdId(o.getUserId());

		this.productNm = o.getProductNm();
		this.productDnm=o.getProductDnm();
		this.productEnm=o.getProductEnm();
		this.productModel=o.getProductModel();

		this.salePrice = o.getSalePrice();
		this.offlineSalePrice = o.getOfflineSalePrice();
		this.overseasSalePrice = o.getOverseasSalePrice();

		this.optionValue1 = o.getOptionValue1();
		this.optionValue2 = o.getOptionValue2();
		this.optionValue3 = o.getOptionValue3();
		this.optionValue4 = o.getOptionValue4();
		this.optionValue5 = o.getOptionValue5();

		this.goodsDescription = o.getGoodsDescription();
		this.shortDescription = o.getShortDescription();

		this.supplierId = o.getSupplierId();
		this.masterId = o.getMasterId();

		this.brandId = o.getBrandId().equals("") ? null : o.getBrandId();
		this.categoryId = o.getCategoryId().equals("") ? null : o.getCategoryId();

		this.stockCnt = o.getStockCnt();
		this.saleYn = o.getSaleYn();
		this.displayYn = o.getDisplayYn();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;

	private String productNm;
	private String productDnm;
	private String productEnm;
	private String productModel;

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

	private Long supplierId;
	private String vendorId;

	private Long masterId;

	private String brandId;
	private String categoryId;

	private Long stockCnt;
	private String saleYn;
	private String displayYn;


	@JoinColumn(name = "vendorId", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Cmvdmr cmvdmr; // cmvdmr 연관관계

	@JoinColumn(name = "supplierId", referencedColumnName = "supplierId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Suppliers suppliers; // cmvdmr 연관관계

	@JoinColumn(name = "brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	@ManyToOne(fetch = FetchType.LAZY)
	private Itbrnd itbrnd; // cmvdmr 연관관계

}
