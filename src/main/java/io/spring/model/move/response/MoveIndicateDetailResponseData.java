package io.spring.model.move.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이동지시내역 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveIndicateDetailResponseData {
    public MoveIndicateDetailResponseData(Lsshpm lsshpm){
        this.shipId = lsshpm.getShipId();
//        this.oStorageId = lsshpm.geto 바깥에서 set
        this.storageId = lsshpm.getStorageId();
        this.moveIndDt = Utilities.removeTAndTransToStr(lsshpm.getApplyDay());
//        this.dealtypeCd = lsshpm.
    }
    private String shipId;
    @JsonProperty("oStorageId")
    private String oStorageId;
    private String storageId;
    private String moveIndDt;
    private String dealtypeCd;

	// 2021-11-15 추가
	private String purchaseNo;

    private List<Move> moves;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move implements SetOptionInterface {
        public Move(Lsshpd lsshpd, Lsshpm lsshpm, Lspchd lspchd){
            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.orderId = lsshpd.getOrderId();
            this.orderSeq = lsshpd.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId,orderSeq);
//            this.deliMethod = lsshpd.getd
            this.assortId = lsshpd.getAssortId();
            this.itemId = lsshpd.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(assortId,itemId);
            this.assortNm = lsshpd.getItasrt().getAssortNm();
            // 옵션은 바깥에서 set
            this.qty = lsshpd.getShipIndicateQty();
            this.cost = lsshpd.getLocalPrice();
            this.deliMethod = lsshpm.getDelMethod();
            this.purchaseNo = lspchd.getPurchaseNo();
            this.purchaseSeq = lspchd.getPurchaseSeq();
            this.purchaseKey = Utilities.addDashInMiddle(this.purchaseNo, this.purchaseSeq);
            this.purchaseDt = Utilities.removeTAndTransToStr(lspchd.getLspchm().getPurchaseDt());
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
        private String goodsKey;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long qty;
        private Float cost;
        // 21-11-12 추가
        private String purchaseNo;
        private String purchaseSeq;
        private String purchaseKey;
        private String purchaseDt;

		// 21-11-15 무게추가
		private Float weight;

    }
}
