package io.spring.model.goods.response;

import java.time.LocalDate;
import java.util.List;

<<<<<<< Updated upstream
import io.spring.model.goods.entity.Products;
import lombok.AccessLevel;
=======
>>>>>>> Stashed changes
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
<<<<<<< Updated upstream
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductsListResponseData {
	public ProductsListResponseData(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId,
			String assortNm) {
		this.regDtBegin = regDtBegin;
		this.regDtEnd = regDtEnd;
		this.assortId = assortId;
		this.shortageYn = shortageYn;
		this.assortNm = assortNm;
=======
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
>>>>>>> Stashed changes
	}

	private LocalDate regDtBegin;
	private LocalDate regDtEnd;
<<<<<<< Updated upstream
	private String assortId;
	private String shortageYn;
	private String assortNm;
	private List<Products> productsList;
=======
	private Long productId;

	private String productNm;
	private String saleYn;
	private String displayYn;
	private Long masterId;
	private String masterNm;
	private List<ProductsResponseData> productsList;
>>>>>>> Stashed changes

}
