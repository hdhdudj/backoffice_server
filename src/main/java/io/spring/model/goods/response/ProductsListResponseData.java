package io.spring.model.goods.response;

import java.time.LocalDate;
import java.util.List;

import io.spring.model.goods.entity.Products;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductsListResponseData {
	public ProductsListResponseData(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId,
			String assortNm) {
		this.regDtBegin = regDtBegin;
		this.regDtEnd = regDtEnd;
		this.assortId = assortId;
		this.shortageYn = shortageYn;
		this.assortNm = assortNm;
	}

	private LocalDate regDtBegin;
	private LocalDate regDtEnd;
	private String assortId;
	private String shortageYn;
	private String assortNm;
	private List<Products> productsList;

}
