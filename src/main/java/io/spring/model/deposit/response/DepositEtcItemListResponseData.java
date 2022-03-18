package io.spring.model.deposit.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.entity.Lsdpsd;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositEtcItemListResponseData {

	public DepositEtcItemListResponseData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm,
			String depositNo, String depositGb, String storageId) {
	        this.startDt = startDt;
	        this.endDt = endDt;
	        this.assortId = assortId;
	        this.assortNm = assortNm;
			this.depositNo = depositNo;
	        this.depositGb = depositGb;
	        this.storageId = storageId;
	    }

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate startDt;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate endDt;
	private String depositNo;
	private String assortId;
	private String assortNm;
	private String depositGb;
	private String storageId;

	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		public Item( Lsdpsd lsdpsd) {
	            this.depositNo = lsdpsd.getDepositNo();
	            this.depositSeq = lsdpsd.getDepositSeq();
	            this.depositKey = Utilities.addDashInMiddle(this.depositNo, this.depositSeq);
	            this.assortId = lsdpsd.getAssortId();
	            this.itemId = lsdpsd.getItemId();
	            this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
	            this.extraUnitcost = lsdpsd.getExtraUnitcost();
	            this.depositDt = Utilities.removeTAndTransToStr(lsdpsd.getLsdpsm().getDepositDt());

				this.assortNm = lsdpsd.getItasrt().getAssortNm();
				this.brandNm = lsdpsd.getItasrt().getItbrnd() == null ? ""
						: lsdpsd.getItasrt().getItbrnd().getBrandNm();

				this.optionNm1 = lsdpsd.getItitmm().getItvari1() == null ? ""
						: lsdpsd.getItitmm().getItvari1().getOptionNm();
				this.optionNm2 = lsdpsd.getItitmm().getItvari2() == null ? ""
						: lsdpsd.getItitmm().getItvari2().getOptionNm();
				this.optionNm3 = lsdpsd.getItitmm().getItvari3() == null ? ""
						: lsdpsd.getItitmm().getItvari3().getOptionNm();

				this.storageId=lsdpsd.getLsdpsm().getStoreCd();
				this.depositGb = lsdpsd.getLsdpsm().getDepositGb();
				
				this.itemGrade = lsdpsd.getItemGrade();
				this.rackNo = lsdpsd.getRackNo();
				this.depositQty = lsdpsd.getDepositQty();

	        }

			private String depositNo;
			private String depositSeq;
			private String depositKey;
			private String depositDt;
			private String assortId;
			private String itemId;
			private String goodsKey;
			private String assortNm;
			private String brandNm;
			private String optionNm1;
			private String optionNm2;
			private String optionNm3;
			private Long depositQty;
			private String itemGrade;
			private Float extraUnitcost;
			private String storageId;
			private String rackNo;
			private String depositGb;
			private String channelGoodsNo;
	}

}
