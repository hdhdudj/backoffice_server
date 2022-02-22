package io.spring.model.deposit.response;

import java.util.List;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositEtcItemResponseData {

	public DepositEtcItemResponseData(Lsdpsm lsdpsm) {
		this.depositDt = Utilities.removeTAndTransToStr(lsdpsm.getDepositDt());
		this.storageId = lsdpsm.getStoreCd();
		this.vendorId = lsdpsm.getVendorId();
		this.depositGb = lsdpsm.getDepositGb();
		this.depositType = lsdpsm.getDepositType();
		this.depositNo = lsdpsm.getDepositNo();

	}

	private String depositNo;
	private String depositDt;
	private String storageId;
	private String vendorId = "AAAAAA"; // AAAAAA trdst 고정
	private String depositGb; // 기타입고 11
	private String depositType = "01"; // 입고 01
	private List<Item> items;
	// 21-12-08 추가
	private String memo;

	@Getter
	@Setter
	public static class Item {

		public Item(Lsdpsd lsdpsd) {

			this.depositNo = lsdpsd.getDepositNo();
			this.depositSeq = lsdpsd.getDepositSeq();
			this.depositKey = Utilities.addDashInMiddle(depositNo, depositSeq);


			this.assortId = lsdpsd.getAssortId();
			this.itemId = lsdpsd.getAssortId();

			this.goodsKey = Utilities.addDashInMiddle(assortId, itemId);

			this.itemGrade = lsdpsd.getItemGrade();
			this.assortNm = lsdpsd.getItasrt().getAssortNm();

			this.brandNm = lsdpsd.getItasrt().getItbrnd() == null ? "" : lsdpsd.getItasrt().getItbrnd().getBrandNm();

			this.optionNm1 = lsdpsd.getItitmm().getItvari1() == null ? ""
					: lsdpsd.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = lsdpsd.getItitmm().getItvari2() == null ? ""
					: lsdpsd.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = lsdpsd.getItitmm().getItvari3() == null ? ""
					: lsdpsd.getItitmm().getItvari3().getOptionNm();

			this.depositQty = lsdpsd.getDepositQty();
			this.extraUnitcost = lsdpsd.getExtraUnitcost();
			this.rackNo = lsdpsd.getRackNo();

			this.effStaDt = Utilities.removeTAndTransToStr(lsdpsd.getExcAppDt());

		}

		private String depositNo;
		private String depositSeq;
		private String depositKey;

		private String assortId;
		private String itemId;
		private String goodsKey;

		private String assortNm;
		private String brandNm;
		private String optionNm1;
		private String optionNm2;
		private String optionNm3;

		private String itemGrade;
		private Long depositQty;
		private Float extraUnitcost;
		private String rackNo;
		private String effStaDt;

	}

}
