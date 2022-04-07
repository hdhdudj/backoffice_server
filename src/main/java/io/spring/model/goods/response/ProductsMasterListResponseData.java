package io.spring.model.goods.response;

import java.util.List;

import io.spring.model.goods.entity.ProductsMaster;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductsMasterListResponseData {

	public ProductsMasterListResponseData(String masterNm) {
		this.masterNm = masterNm;
	}

	private String masterNm;
	private List<Master> mastersList;

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

}
