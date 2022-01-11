package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.move.response.MoveIndicateListResponseData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 엑셀 업로드시 받는 request DTO
 */
@Getter
public class MoveListExcelRequestData {
        public MoveListExcelRequestData(){}
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate startDt;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate endDt;
        private String storageId;
        private String shipId;
        private String assortId;
        private String assortNm;
        private List<MoveListExcelRequestData.Move> moves;
        @Getter
        @Setter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class Move implements SetOptionInterface {
            public Move(Lsshpd lsshpd){
                TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
                Itasrt itasrt = lsshpd.getItasrt();

                this.shipId = lsshpd.getShipId();
                this.shipSeq = lsshpd.getShipSeq();
                this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
                this.moveIndGb = lsshpd.getShipGb();
                this.deliMethod = lsshpd.getShipGb().equals(StringFactory.getGbThree())? tbOrderDetail.getDeliMethod():lsshpd.getLsshpm().getDelMethod(); // 주문인가?
                this.orderId = lsshpd.getShipGb().equals(StringFactory.getGbThree())? tbOrderDetail.getOrderId():null;
                this.orderSeq = lsshpd.getShipGb().equals(StringFactory.getGbThree())? tbOrderDetail.getOrderSeq():null;
                this.orderKey = orderId != null? Utilities.addDashInMiddle(orderId,orderSeq):null;
                this.storageId = lsshpd.getLsshpm().getStorageId();
                this.oStorageId = lsshpd.getOStorageId();
                this.moveIndDt = Utilities.removeTAndTransToStr(lsshpd.getLsshpm().getInstructDt());
                this.assortId = lsshpd.getAssortId();
                this.itemId = lsshpd.getItemId();
                this.goodsKey = Utilities.addDashInMiddle(assortId,itemId);
                this.assortNm = itasrt.getAssortNm();
                // 옵션명은 바깥에서
                this.qty = lsshpd.getShipIndicateQty();
                this.cost = lsshpd.getLocalPrice();
            }
            private String shipId;
            private String shipSeq;
            private String shipKey;
            private String moveIndGb;
            private String deliMethod;
            private String storageId;
            @JsonProperty("oStorageId")
            private String oStorageId;
            private String moveIndDt;
            private String orderId;
            private String orderSeq;
            private String orderKey;
            private String assortId;
            private String itemId;
            private String goodsKey;
            private String assortNm;
            private String optionNm1;
            private String optionNm2;
            private String optionNm3;
            private Long qty;
            private Float cost;
            //선적일자 : shipmentDt, BL번호 : blNo, 운송형태 : movementKd, 도착예정일자 : estiArrvDt, 컨테이너 종류 : containerKd, 컨테이너 수량 : containerQty
            private String blNo;
            private String movementKd;
            @JsonSerialize(using = LocalDateSerializer.class)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private LocalDate shipmentDt;
            @JsonSerialize(using = LocalDateSerializer.class)
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private LocalDate estiArrvDt;
            private String containerKd;
            private Long containerQty;
        }
}
