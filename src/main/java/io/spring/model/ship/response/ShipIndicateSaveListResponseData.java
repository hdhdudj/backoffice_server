package io.spring.model.ship.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 출고 - 출고지시 : list response
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateSaveListResponseData {
	public ShipIndicateSaveListResponseData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm,
			String vendorId, String orderId, String channelGoodsNo) {
        this.startDt = startDt;
        this.endDt = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = vendorId;
		this.orderId = orderId;
		this.channelGoodsNo = channelGoodsNo;
    }

	public ShipIndicateSaveListResponseData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm,
			String vendorId, String orderId) {
		this.startDt = startDt;
		this.endDt = endDt;
		this.assortId = assortId;
		this.assortNm = assortNm;
		this.vendorId = vendorId;
		this.orderId = orderId;

	}

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
    private String assortId;
    private String assortNm;
    private String vendorId;
	private String orderId;
	private String channelGoodsNo;
    private List<Ship> ships;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship implements SetOptionInterface {

		// 사용안함 2021-10-18 jb
		public Ship(TbOrderDetail tbOrderDetail) {
			TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
			Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
//			TbMember tbMember = tbOrderMaster.getTbMember();
			this.orderDt = tbOrderDetail.getTbOrderMaster().getOrderDate().toString();
			this.assortGb = itasrt.getAssortGb();
			this.orderId = tbOrderDetail == null ? null : tbOrderDetail.getOrderId();
			this.orderSeq = tbOrderDetail == null ? null : tbOrderDetail.getOrderSeq();
			this.orderKey = Utilities.addDashInMiddle(orderId, orderSeq);
			this.deliMethod = tbOrderDetail == null ? null : tbOrderDetail.getDeliMethod();
			this.assortId = tbOrderDetail == null ? null : tbOrderDetail.getAssortId();
			this.itemId = tbOrderDetail == null ? null : tbOrderDetail.getItemId();
			this.orderNm = tbOrderMaster.getTbMemberAddress().getDeliNm();//tbMember == null ? null : tbMember.getCustNm();
			this.assortNm = itasrt.getAssortNm();
//        this.availableQty =
			this.qty = 0l;
			// optionNm1, optionNm2는 외부에서 set
		}

		public Ship(HashMap<String, Object> map) {
			this.assortGb = (String) map.get("assortGb");
			this.orderId = (String) map.get("orderId");
			this.orderSeq = (String) map.get("orderSeq");
			LocalDateTime locTm = (LocalDateTime) map.get("orderDt");
			String addLocTm = locTm.getSecond() == 0? ":00" : "";
			this.orderDt = ((LocalDateTime) map.get("orderDt")).toString().replace('T', ' ') + addLocTm;
			this.orderKey = (String) map.get("orderKey");
			this.deliMethod = (String) map.get("deliMethod");
			this.assortId = (String) map.get("assortId");
			this.itemId = (String) map.get("itemId");
			this.orderNm = (String) map.get("orderNm");
			this.receiverNm = (String) map.get("receiverNm");
			this.assortNm = (String) map.get("assortNm");
			this.availableQty = Long.valueOf((int) map.get("qty"));
			this.qty = 0l;
			this.shipId = (String) map.get("shipId");
			this.shipSeq = (String) map.get("shipSeq");
			this.storageId = (String) map.get("storageId");

			LocalDateTime locRd = (LocalDateTime) map.get("receiptDt");
			String addLocRd = locRd.getSecond() == 0 ? ":00" : "";

			this.receiptDt = ((LocalDateTime) map.get("receiptDt")).toString().replace('T', ' ') + addLocRd;
			// map.get("receiptDt").toString();

			this.optionNm1 = (String) map.get("optionNm1");
			this.optionNm2 = (String) map.get("optionNm2");
			this.optionNm3 = (String) map.get("optionNm3");
			this.rackNo = (String) map.get("rackNo");

			this.channelGoodsNo = (String) map.get("channelGoodsNo");
			this.channelOrderNo = (String) map.get("channelOrderNo");

		}

//		@JsonSerialize(using = LocalDateTimeSerializer.class)
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private String orderDt;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String orderNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long availableQty;
        private Long qty;
		private String shipId;
		private String shipSeq;
		private String storageId;
		private String receiptDt;
		private String rackNo;

		// 2022-03-24 추가
		private String channelOrderNo;
		private String channelGoodsNo;
		// 2022-03-25 추가
		private String receiverNm;
    }
}
