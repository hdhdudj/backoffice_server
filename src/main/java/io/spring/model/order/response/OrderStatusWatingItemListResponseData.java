package io.spring.model.order.response;

import java.util.List;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusWatingItemListResponseData {
	public OrderStatusWatingItemListResponseData(String statusCd, int waitCnt) {
		this.statusCd = statusCd;
		this.waitCnt = waitCnt;
	}

	private String statusCd;
	private int waitCnt;

	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		public Item(TbOrderDetail tod) {
			this.channelGb = tod.getTbOrderMaster().getChannelGb();

			this.orderDate = Utilities.removeTAndTransToStr(tod.getTbOrderMaster().getOrderDate());
			this.orderId = tod.getOrderId();
			this.orderSeq = tod.getOrderSeq();
			this.orderKey = Utilities.addDashInMiddle(this.orderId, this.orderSeq);
			this.statusCd = tod.getStatusCd();
			this.custNm = tod.getTbOrderMaster().getOrderName();
			this.assortId = tod.getAssortId();
			this.itemId = tod.getItemId();
			this.goodsKey = Utilities.addDashInMiddle(this.itemId, this.itemId);
			this.assortNm = tod.getGoodsNm();
			this.optionInfo = tod.getOptionInfo();
			this.optionNm1 = tod.getItitmm().getItvari1() == null ? "" : tod.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = tod.getItitmm().getItvari2() == null ? "" : tod.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = tod.getItitmm().getItvari3() == null ? "" : tod.getItitmm().getItvari3().getOptionNm();
			this.qty = tod.getQty();
			this.salePrice = tod.getSalePrice();

			this.deliPrice = tod.getDeliPrice();

			this.channelOrderNo = tod.getChannelOrderNo();
			this.channelOrderSeq = tod.getChannelOrderSeq();
			this.channelOrderKey = Utilities.addDashInMiddle(this.channelOrderNo, this.channelOrderSeq);

			this.payDt = Utilities.removeTAndTransToStr(tod.getTbOrderMaster().getPayDt());

			this.vendorNm = tod.getItitmm().getItasrt().getCmvdmr() == null ? ""
					: tod.getItitmm().getItasrt().getCmvdmr().getVdNm();

			this.brandNm = tod.getItitmm().getItasrt().getItbrnd() == null ? ""
					: tod.getItitmm().getItasrt().getItbrnd().getBrandNm();

		}

		private String channelGb;
		// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
		// timezone = "Asia/Seoul")
		private String orderDate;
		private String orderId; // 주문번호
		private String orderSeq; // 주문순번
		private String orderKey; // 주문키
		private String statusCd; // 주문상태
		private String custNm;
		private String assortId; // 품목코드
		private String itemId; // 상품코드
		private String goodsKey;
		// private String goodsNm; // 상품명
		private String assortNm;
		private String optionInfo; // 제휴옵션정보
		private String optionNm1; // 옵션1
		private String optionNm2; // 옵션2
		private String optionNm3; // 옵션3
		private Long qty; // 수량
		private double salePrice; // 판매가
		private double deliPrice; // 배송비
		private String channelOrderNo;
		private String channelOrderSeq;
		private String channelOrderKey;
		private String payDt;
		private String vendorNm;
		private String brandNm;
	}

}
