package io.spring.model.ship.response;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 출고 - 출고지시리스트 : 출고지시일자, 출고지시번호, 상품코드or상품명, 구매처 조건에 맞는 출고지시 리스트 DTO
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
        public Ship(TbOrderDetail tbOrderDetail, Lsshpm lsshpm, Lsshpd lsshpd){
            TbMember tbMember = tbOrderDetail.getTbOrderMaster().getTbMember();
            this.shipIndDt = java.sql.Timestamp.valueOf(lsshpm.getReceiptDt());
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.assortGb = tbOrderDetail.getAssortGb();
            this.deliMethod = tbOrderDetail.getDeliMethod();
            this.assortId = tbOrderDetail.getAssortId();
            this.itemId = tbOrderDetail.getItemId();
            this.custNm = tbMember==null? null : tbMember.getCustNm();
            this.assortNm = tbOrderDetail.getGoodsNm();
            this.blNo = lsshpm.getBlNo(); // 트래킹 번호
            this.shipDt = Utilities.removeTAndTransToStr(lsshpm.getApplyDay());
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId, orderSeq);
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
    }
}
