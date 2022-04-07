package io.spring.model.goods.response;

import java.time.LocalDate;
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
	private List<ProductsResponseData> productsList;


}
