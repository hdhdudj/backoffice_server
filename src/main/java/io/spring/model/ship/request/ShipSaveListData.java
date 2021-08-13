package io.spring.model.ship.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 출고 - 출고처리 : 출고처리 수량변경 후 저장하기 위해 받을 때 쓰는 request DTO (출고처리 저장할 때 이용)
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipSaveListData {
    private Date startDt;
    private Date endDt;
    private String shipId;
    private String assortId;
    private String assortNm;
    private String vendorId;
    private List<Ship> ships;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{
        private Date shipIndDt;
        private String shipId;
        private String shipSeq;
        private String shipKey;
        private String blNo;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
    }
}
