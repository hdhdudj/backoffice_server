package io.spring.model.goods.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetStockListResponseData {
	public GetStockListResponseData(String storeCd, String purchaseVendorId, String assortId, String assortNm) {
		this.storageId = storeCd;
		this.purchaseVendorId = purchaseVendorId;
		this.assortId = assortId;
		this.assortNm = assortNm;
	}

	private String storageId;
	private String purchaseVendorId;
	private String assortId;
	private String assortNm;
	private List<Goods> goods;

	@Getter
	@Setter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Goods {
		public Goods(Ititmc ititmc) {
			this.storageId = ititmc.getCmstgm().getStorageId();
			this.rackNo = ititmc.getStorageId(); // 랙의 재고를 가져오는 방식임.
			this.assortId = ititmc.getAssortId();
			this.itemId = ititmc.getItemId();
			this.assortNm = ititmc.getItasrt().getAssortNm();
			this.channelId = ititmc.getItasrt().getVendorId();
			this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
			this.effStaDt = Utilities.removeTAndTransToStr(ititmc.getEffStaDt());
//	            this.brandNm = itasrt.getIfBrand().getBrandNm(); 바깥에서 set
			this.qty = ititmc.getQty() == null ? 0l : ititmc.getQty();
			this.availableQty = ititmc.getShipIndicateQty() == null ? this.qty : this.qty - ititmc.getShipIndicateQty();
			this.cost = ititmc.getStockAmt();
//	            this.storeCd = storeCd; // 바깥에서 set
			this.moveQty = 0l;

			this.optionNm1 = ititmc.getItitmm().getItvari1() == null ? ""
					: ititmc.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = ititmc.getItitmm().getItvari2() == null ? ""
					: ititmc.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = ititmc.getItitmm().getItvari3() == null ? ""
					: ititmc.getItitmm().getItvari3().getOptionNm();

			this.brandNm = ititmc.getItasrt().getItbrnd() == null ? "" : ititmc.getItasrt().getItbrnd().getBrandNm();

			this.itemGrade = ititmc.getItemGrade();

		}

		public Goods(HashMap<String, Object> o) {
			this.storageId = o.get("storageId") == null ? null : o.get("storageId").toString();
			this.rackNo = o.get("rackNo") == null ? null : o.get("rackNo").toString();
			this.assortId = o.get("assortId") == null ? null : o.get("assortId").toString();
			this.itemId = o.get("itemId") == null ? null : o.get("itemId").toString();
			this.assortNm = o.get("assortNm") == null ? null : o.get("assortNm").toString();
			this.channelId = o.get("channelId") == null ? null : o.get("channelId").toString();
			this.goodsKey = o.get("goodsKey") == null ? null : o.get("goodsKey").toString();
			
			// LocalDateTime esdt = (LocalDateTime) o.get("effStaDt");
			
			this.effStaDt = o.get("effStaDt") == null ? null
					: Utilities.removeTAndTransToStr((LocalDateTime) o.get("effStaDt"));
//	            this.brandNm = itasrt.getIfBrand().getBrandNm(); 바깥에서 set
			this.qty = o.get("qty") == null ? null : (Long) o.get("qty");


			this.availableQty = o.get("availableQty") == null ? null : (Long) o.get("availableQty");

			this.cost = o.get("cost") == null ? null : ((BigDecimal) o.get("cost")).floatValue();

//			this.cost = (float) o.get("cost");
//	            this.storeCd = storeCd; // 바깥에서 set
			this.moveQty = 0l;

			this.optionNm1 = o.get("optionNm1") == null ? null : o.get("optionNm1").toString();
			this.optionNm2 = o.get("optionNm2") == null ? null : o.get("optionNm2").toString();
			this.optionNm3 = o.get("optionNm3") == null ? null : o.get("optionNm3").toString();

			this.brandNm = o.get("brandNm") == null ? null : o.get("brandNm").toString();
			this.itemGrade = o.get("itemGrade") == null ? null : o.get("itemGrade").toString();
			this.channelGoodsNo = o.get("channelGoodsNo") == null ? null : o.get("channelGoodsNo").toString();

		}

		private String storageId;
		private String effStaDt;
		private String assortId;
		private String itemId;
		private String goodsKey;
		private String assortNm;
		private String brandNm;
		private String optionNm1;
		private String optionNm2;
		private String optionNm3;
		private String channelId;
		private Long qty;
		private Long orderQty;
		private Long availableQty;
		private Long moveQty;
		private Float cost;
		private String rackNo;
		private String itemGrade;
		private String channelGoodsNo;

	}
}
