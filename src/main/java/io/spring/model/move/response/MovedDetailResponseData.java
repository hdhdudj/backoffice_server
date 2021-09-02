package io.spring.model.move.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 이동내역 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovedDetailResponseData {
    public MovedDetailResponseData(String shipId){
        this.shipId = shipId;
    }
    private String shipId;
    private String oStorageId;
    private String storageId;
    private LocalDate startDt;
    private LocalDate endDt;
    private String shipGb;
    private String blNo;
    private List<Move> moves;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move{
        public Move(Lsshpm lsshpm, Lsshpd lsshpd){
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId,orderSeq);
            this.deliMethod = lsshpm.getDelMethod();
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.assortNm = lsshpd.getItasrt().getAssortNm();
            // 옵션은 밖에서
            this.qty = lsshpd.getShipQty();
            this.cost = lsshpd.getLocalPrice();
        }
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
        private Float cost;
    }
}
