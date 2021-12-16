package io.spring.model.ship.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 출고 - 출고리스트 : 출고리스트 화면에서 조건 검색으로 리스트 가져올 때도 이용됨.
*/
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipListDataResponse {
    public ShipListDataResponse(LocalDate start, LocalDate end, String shipId, String assortId, String assortNm, String channelId)
    {
        this.startDt = start;
        this.endDt = end;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = channelId;
    }
    private LocalDate startDt;
    private LocalDate endDt;
    private String shipId;
    private String assortId;
    private String assortNm;
    private String vendorId;
    private List<Ship> ships;

    @Setter
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String receiveNm;
        private String receiveTel;
        private String receiveHp;
        private String receiveZipNo;
        private String receiveAddress;
        private String orderRequest;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String imagePath;
        private Long itemQty;
        private String assortId;
        private String itemId;
        private String assortKey;
    }
}
