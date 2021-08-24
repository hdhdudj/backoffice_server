package io.spring.model.move.response;

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
    private LocalDate startDt;
    private LocalDate endDt;
    private String storageId;
    private String assortId;
    private String assortNm;
    private List<Move> moves;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class Move {
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String moveIndGb;
        private String deliMethod;
        private String storageId;
        private LocalDate moveIndDt;
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
