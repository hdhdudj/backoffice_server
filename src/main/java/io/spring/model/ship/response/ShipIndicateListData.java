package io.spring.model.ship.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 출고지시리스트 : 출고지시일자, 출고지시번호, 상품코드or상품명, 구매처 조건에 맞는 출고지시 리스트 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateListData {
    public ShipIndicateListData(Date startDt, Date endDt, String shipId, String assortId, String assortNm, String vendorId){
        this.startDt = startDt;
        this.endDt = endDt;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = vendorId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
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
        public Ship(TbOrderDetail tbOrderDetail){
//            Lspchd lspchd = tbOrderDetail.getLspchd();
//            Lsdpsd lsdpsd = tbOrderDetail.getLspchd().getLsdpsd();
//            this.shipDt = lsdpsd.getExcAppDt();
        }
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date shipDt;
        private String shipId;
        private String shipSeq;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
    }
}
