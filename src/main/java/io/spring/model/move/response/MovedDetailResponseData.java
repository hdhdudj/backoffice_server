package io.spring.model.move.response;

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
