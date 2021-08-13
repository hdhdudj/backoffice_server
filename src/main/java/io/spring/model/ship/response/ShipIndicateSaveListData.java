package io.spring.model.ship.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 출고 - 출고처리 : 출고처리 저장을 위해 조건 조회하면 불려오는 리스트를 가져올 때 쓰는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateSaveListData {
    private Date startDt;
    private Date endDt;
    private String shipId;
    private String assortId;
    private String itemId;
    private String vendorId;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
        private Date shipIndDt;
        private String shipId;
        private String shipSeq;
        private String trackNo;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
    }
}
