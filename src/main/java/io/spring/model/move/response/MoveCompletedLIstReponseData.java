package io.spring.model.move.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.move.request.MoveListExcelRequestData;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이동리스트 조회용 DTO
 */
@Getter
@Setter
public class MoveCompletedLIstReponseData {
    public MoveCompletedLIstReponseData(){}
    public MoveCompletedLIstReponseData(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId){
        this.startDt = startDt.toString();
        this.endDt = endDt.toString();
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.storageId = storageId;
    }
    public MoveCompletedLIstReponseData(MoveListExcelRequestData moveListExcelRequestData){
        this.startDt = moveListExcelRequestData.getStartDt().toString();
        this.endDt = moveListExcelRequestData.getEndDt().toString();
        this.shipId = moveListExcelRequestData.getShipId();
        this.assortId = moveListExcelRequestData.getAssortId();
        this.assortNm = moveListExcelRequestData.getAssortNm();
        this.storageId = moveListExcelRequestData.getStorageId();
    }
    private String startDt;
    private String endDt;
    private String shipId;
    private String assortId;
    private String assortNm;
    private String storageId;
    private List<Move> moves;

    @Getter
    @Setter
    public static class Move implements SetOptionInterface {
        public Move(){}
        public Move(Lsshpm lsshpm, Lsshpd lsshpd){
            this.shipDt = Utilities.removeTAndTransToStr(lsshpm.getApplyDay());
            this.shipIndDt = Utilities.removeTAndTransToStr(lsshpm.getReceiptDt());
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.trackNo = lsshpm.getBlNo();
            this.storageId = lsshpm.getStorageId();
            this.oStorageId = lsshpm.getOStorageId();
            this.shipGb = lsshpd.getShipGb();
            this.deliMethod = lsshpm.getDelMethod();
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(assortId,itemId);
            this.assortNm = lsshpd.getItasrt().getAssortNm();
            // 옵션은 바깥에서 set
            this.qty = lsshpd.getShipIndicateQty().toString();

            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(this.orderId, this.orderSeq);
            this.shipmentDt = lsshpm.getShipmentDt() == null? "" : lsshpm.getShipmentDt().toString();
            this.blNo = lsshpm.getBlNo();
            this.movementKd = lsshpm.getMovementKd();
            this.estiArrvDt = lsshpm.getEstiArrvDt() == null? "" : lsshpm.getEstiArrvDt().toString();
            this.containerKd = lsshpm.getContainerKd();
            this.containerQty = lsshpm.getContainerQty() == null? "" : lsshpm.getContainerQty().toString();
        }
        private String shipDt;
        private String shipIndDt;
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String trackNo; // lsshpm.blNo
        private String storageId;
		@JsonProperty("oStorageId")
        private String oStorageId;
        private String shipGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String qty;
        // 12-21-30 추가된 컬럼
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String shipmentDt;
        private String blNo;
        private String movementKd;
        private String estiArrvDt;
        private String containerKd;
        private String containerQty;
    }
}
