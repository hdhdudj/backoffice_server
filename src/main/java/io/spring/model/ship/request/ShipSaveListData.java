package io.spring.model.ship.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
        private String optionNm3;
        private Long qty;
    }
}
