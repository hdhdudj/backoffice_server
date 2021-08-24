package io.spring.model.move.response;

import io.spring.model.ship.entity.Lsshpm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    }
    private String shipId;
    private String oStorageId;
    private String storageId;
    private String moveIndDt;
    private String delitypeCd;
    private List<Move> moves;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move{
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
        private Long qty;
        private Float cost;
    }
}
