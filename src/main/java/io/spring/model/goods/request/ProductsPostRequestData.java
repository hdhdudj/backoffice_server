package io.spring.model.goods.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ProductsPostRequestData {

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


	private Long masterId;

	private String brandId;

	private String categoryId;


	private Long stockCnt;
	private String saleYn;
	private String displayYn;
	
	private String userId;

	private List<AddInfo> addInfos;

	// image 관련
	private List<MainImage> MainImage;
	private List<AddImage> AddImage;

	@Getter
	@Setter
	@ToString
	public static class AddInfo {
		private Long sno;
		private String infoTitle;
		private String infoValue;

	}

	@Getter
	@Setter
	public static class MainImage {
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
		private String uid;
		private String name;
		private String url;
		private String imageGb;
		private String status;
		private Long sno;
		private String fileYn;
	}

}
