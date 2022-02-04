package io.spring.model.move.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 이동지시리스트(상품, 주문) 조회 DTO
 */
@Getter
@Setter
public class MoveIndicateListResponseData {
    public MoveIndicateListResponseData(){}
    public MoveIndicateListResponseData(LocalDate startDt,LocalDate endDt,String storageId,String oStorageId,String assortId,String assortNm){
        this.startDt = startDt;
        this.endDt = endDt;
        this.storageId = storageId;
        this.oStorageId = oStorageId;
        this.assortId = assortId;
        this.assortNm = assortNm;
    }
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDt;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDt;
    private String storageId;
    @JsonProperty("oStorageId")
    private String oStorageId;
    private String assortId;
    private String assortNm;
    private List<Move> moves;
    @Getter
    @Setter
    public static class Move implements SetOptionInterface {
        public Move(){}
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
            this.qty = Utilities.nullOrEmptyFilter(lsshpd.getShipIndicateQty()) == null? "" : lsshpd.getShipIndicateQty().toString();
            this.cost = Utilities.nullOrEmptyFilter(lsshpd.getLocalPrice()) == null? "" : lsshpd.getLocalPrice().toString();
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
        private String qty;
        private String cost;
    }
}
