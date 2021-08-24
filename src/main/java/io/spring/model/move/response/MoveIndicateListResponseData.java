package io.spring.model.move.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 이동지시리스트(상품, 주문) 조회 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveIndicateListResponseData {
    public MoveIndicateListResponseData(LocalDate startDt,LocalDate endDt,String storageId,String assortId,String assortNm){
        this.startDt = startDt;
        this.endDt = endDt;
        this.storageId = storageId;
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
    private String assortId;
    private String assortNm;
    private List<Move> moves;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move {
        public Move(Lsshpd lsshpd){
            TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
            Itasrt itasrt = lsshpd.getItasrt();

            this.shipId = lsshpd.getShipId();
            this.shipSeq = lsshpd.getShipSeq();
            this.shipKey = Utilities.addDashInMiddle(shipId,shipSeq);
            this.moveIndGb = lsshpd.getShipGb();
            this.deliMethod = lsshpd.getOrderId() == null? null:tbOrderDetail.getDeliMethod();
            this.storageId = lsshpd.getOStorageId();
            this.moveIndDt = Utilities.removeTAndTransToStr(lsshpd.getRegDt());
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
        private String moveIndDt;
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
