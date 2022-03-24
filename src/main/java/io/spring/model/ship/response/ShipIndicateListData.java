package io.spring.model.ship.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 출고 - 출고처리 : 출고처리 화면에서 조건 검색으로 리스트 가져올 때도 이용됨.
*/
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateListData {
	public ShipIndicateListData(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm,
			String channelId, String orderId) {
        this.startDt = startDt;
        this.endDt = endDt;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.channelId = channelId;
		this.orderId = orderId;
    }
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;
    private String shipId;
    private String assortId;
    private String assortNm;
    private String channelId;
	private String orderId;

    private List<Ship> ships;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship implements SetOptionInterface {
        public Ship(TbOrderDetail tbOrderDetail, TbOrderMaster tbOrderMaster, Lsshpm lsshpm, Lsshpd lsshpd){
//            TbMember tbMember = tbOrderDetail.getTbOrderMaster().getTbMember();
            this.shipIndDt = java.sql.Timestamp.valueOf(lsshpm.getReceiptDt());
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.assortGb = tbOrderDetail.getAssortGb();
            this.deliMethod = tbOrderDetail.getDeliMethod();
            this.assortId = tbOrderDetail.getAssortId();
            this.itemId = tbOrderDetail.getItemId();
            this.custNm = tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliNm();//tbMember==null? null : tbMember.getCustNm();
            this.assortNm = tbOrderDetail.getGoodsNm();
            this.blNo = lsshpm.getBlNo(); // 트래킹 번호
            this.shipDt = Utilities.removeTAndTransToStr(lsshpm.getApplyDay());
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId, orderSeq);
			this.rackNo = lsshpd.getRackNo();

			this.optionNm1 = lsshpd.getItitmm().getItvari1() == null ? ""
					: lsshpd.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = lsshpd.getItitmm().getItvari2() == null ? ""
					: lsshpd.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = lsshpd.getItitmm().getItvari3() == null ? ""
					: lsshpd.getItitmm().getItvari3().getOptionNm();
            this.orderDt = tbOrderMaster.getOrderDate();
            this.channelOrderNo = tbOrderMaster.getChannelOrderNo();
            // 옵션은 외부 set
            // qty는 외부 set
        }

        // 출고리스트에서만 쓰이는 요소
        private String shipDt;
//        private String 송장번호

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date shipIndDt;
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String blNo;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long qty;
		private String rackNo;
        // 2022-03-24 추가
        private String channelOrderNo;
        private String channelGoodsNo;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime orderDt;
    }
}
