package io.spring.model.ship.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *  출고 - 출고지시내역 : 출고지시번호를 받아 출고마스터와 출고디테일을 보여줄 때 쓰는 DTO
 *  출고 - 출고내역 : response DTO
 */
@Getter
@Setter
public class ShipItemListData {
    public ShipItemListData(){}
    public ShipItemListData(Lsshpm lsshpm){
        this.shipId = lsshpm.getShipId();
        this.storageId = lsshpm.getStorageId();
        this.channelId = lsshpm.getChannelId();
//        this.orderDt = lsshpm.get 밖에서 set
        this.shipIndicateDt = Utilities.removeTAndTransToStr(lsshpm.getReceiptDt());
        this.shipDt = Utilities.removeTAndTransToStr(lsshpm.getApplyDay());
    }
    private String shipId;
    private String storageId;
    private String channelId;
    private String orderDt;
    private String shipIndicateDt;
    private List<Ship> ships;
    // 출고내역에만 있는 요소
    private String shipDt;

    @Getter
    @Setter
    public static class Ship implements SetOptionInterface {
        public Ship(){}
        public Ship(Lsshpd lsshpd){
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId, shipSeq);
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId,orderSeq);
            this.assortGb = lsshpd.getTbOrderDetail().getAssortGb();
            this.deliMethod = lsshpd.getTbOrderDetail().getDeliMethod();
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(assortId, itemId);
            // 옵션은 밖에서
            this.qty = lsshpd.getShipIndicateQty() == null? null : lsshpd.getShipIndicateQty() + "";
            this.cost = lsshpd.getLocalPrice() == null? null : lsshpd.getLocalPrice()+"";
        }
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String qty;
        private String cost;
    }
}
