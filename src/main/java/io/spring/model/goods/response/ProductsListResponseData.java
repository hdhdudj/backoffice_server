package io.spring.model.goods.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductsListResponseData {
	public ProductsListResponseData(LocalDate regDtBegin, LocalDate regDtEnd, String saleYn, String displayYn,
			Long productId, String productNm, Long masterId, String masterNm) {
		this.regDtBegin = regDtBegin;
		this.regDtEnd = regDtEnd;
		this.saleYn = saleYn;
		this.displayYn = displayYn;
		this.productId = productId;

		this.productNm = productNm;
		this.masterId = masterId;
		this.masterNm = masterNm;

	}

	private LocalDate regDtBegin;
	private LocalDate regDtEnd;

	private Long productId;

	private String productNm;
	private String saleYn;
	private String displayYn;
	private Long masterId;
	private String masterNm;
	private List<Product> productsList;

	@Getter
	@Setter
	public static class Product {

		public Product(HashMap<String, Object> p) {


			this.productId = new Long((Integer) p.get("productId"));

			this.productNm = p.get("productNm") == null ? null : p.get("productNm").toString();
			this.productDnm = p.get("productDnm") == null ? null : p.get("productDnm").toString();
			this.productEnm = p.get("productEnm") == null ? null : p.get("productEnm").toString();
			this.productModel = p.get("productModel") == null ? null : p.get("productModel").toString();

			this.supplierId = p.get("supplierId") == null ? null : new Long((Integer) p.get("supplierId"));
			this.supplierNm = p.get("supplierNm") == null ? null : p.get("supplierNm").toString();
			this.salePrice = p.get("salePrice") == null ? null : ((BigDecimal) p.get("salePrice")).floatValue();
			this.offlineSalePrice = p.get("offlineSalePrice") == null ? null
					: ((BigDecimal) p.get("offlineSalePrice")).floatValue();
			this.overseasSalePrice = p.get("overseasSalePrice") == null ? null
					: ((BigDecimal) p.get("overseasSalePrice")).floatValue();

			this.optionValue1 = p.get("optionValue1") == null ? null : p.get("optionValue1").toString();
			this.optionValue2 = p.get("optionValue2") == null ? null : p.get("optionValue2").toString();
			this.optionValue3 = p.get("optionValue3") == null ? null : p.get("optionValue3").toString();
			this.optionValue4 = p.get("optionValue4") == null ? null : p.get("optionValue4").toString();
			this.optionValue5 = p.get("optionValue5") == null ? null : p.get("optionValue5").toString();

			this.goodsDescription = p.get("goodsDescription") == null ? null : p.get("goodsDescription").toString();
			this.shortDescription = p.get("shortDescription") == null ? null : p.get("shortDescription").toString();

			this.brandId = p.get("brandId") == null ? null : p.get("brandId").toString();
			this.brandNm = p.get("brandNm") == null ? null : p.get("brandNm").toString();
			this.stockCnt = p.get("stockCnt") == null ? null : new Long((Integer) p.get("stockCnt"));
			this.saleYn = p.get("saleYn") == null ? null : p.get("saleYn").toString();
			this.displayYn = p.get("displayYn") == null ? null : p.get("displayYn").toString();

			this.masterId = p.get("masterId") == null ? null : new Long((Integer) p.get("masterId"));

			this.masterNm = p.get("masterNm") == null ? null : p.get("masterNm").toString();

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

		private Long masterId;
		private String masterNm;

	}


}
