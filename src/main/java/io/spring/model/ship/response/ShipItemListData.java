package io.spring.model.ship.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *  출고 - 출고지시내역 : 출고지시번호를 받아 출고마스터와 출고디테일을 보여줄 때 쓰는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipItemListData {
    public ShipItemListData(Lsshpm lsshpm){
        this.shipId = lsshpm.getShipId();
        this.storageId = lsshpm.getStorageId();
        this.vendorId = lsshpm.getVendorId();
//        this.orderDt = lsshpm.get 밖에서 set
        this.shipIndicateDt =java.sql.Timestamp.valueOf(lsshpm.getReceiptDt());
    }
    private String shipId;
    private String storageId;
    private String vendorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date orderDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date shipIndicateDt;
    private List<Ship> ships;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{
        public Ship(Lsshpd lsshpd){
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId, shipSeq);
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.assortGb = lsshpd.getTbOrderDetail().getAssortGb();
            this.deliMethod = lsshpd.getTbOrderDetail().getDeliMethod();
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(assortId, itemId);
            // 옵션은 밖에서
            this.qty = lsshpd.getShipIndicateQty();
            this.cost = lsshpd.getLocalPrice();
        }
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String orderId;
        private String orderSeq;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
        private Float cost;
    }
}
