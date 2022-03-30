package io.spring.model.goods.response;

import java.time.LocalDate;
import java.util.List;

import io.spring.model.goods.entity.TbGoods;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsListResponseData {
	public GoodsListResponseData(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId,
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
	private List<Goods> goodsList;

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Goods {
		public Goods(TbGoods tbGoods) {
			this.assortId = tbGoods.getAssortId();
			this.assortNm = tbGoods.getAssortNm();
			this.shortageYn = tbGoods.getShortageYn();
			this.brandId = tbGoods.getBrandId();
			this.dispCategoryId = tbGoods.getDispCategoryId();
			this.brandNm = tbGoods.getItbrnd() == null ? "" : tbGoods.getItbrnd().getBrandNm();
			this.categoryNm = tbGoods.getCategoryId() == null || tbGoods.getCategoryId().trim().equals("") ? ""
					: tbGoods.getItcatg().getCategoryNm();
		}

		private String assortNm;
		private String brandNm;
		private String shortageYn;
		private String assortId;
		private String brandId;
		private String categoryNm;
		private String dispCategoryId;
	}

}
