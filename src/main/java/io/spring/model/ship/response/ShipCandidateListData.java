package io.spring.model.ship.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.purchase.entity.Lspchd;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
  * 출고 - 출고지시리스트 : 출고지시일자, 출고지시번호, 상품코드or상품명, 구매처 조건에 맞는 출고지시 리스트 DTO
  */
@Setter
@Getter
public class ShipCandidateListData {
    public ShipCandidateListData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String vendorId, String orderId) {
        this.start = startDt;
        this.end = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.orderId = orderId;
        this.vendorId = vendorId;
    }
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate start;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate end;
    private String assortId;
    private String assortNm;
    private String orderId;
    private String storageId;
    private String vendorId;
    private List<Ship> ships;


    @Setter
    @Getter
    public static class Ship implements SetOptionInterface {
        public Ship(Lsdpsd lsdpsd){
            Lspchd lspchd = lsdpsd.getLspchd();
            TbOrderDetail tbOrderDetail = lspchd.getTbOrderDetail();
            TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
            Ititmm ititmm = lsdpsd.getItitmm();
            Itasrt itasrt = ititmm.getItasrt();
            this.orderDt = Utilities.nullOrEmptyFilter(tbOrderMaster.getOrderDate());
            this.orderId = tbOrderDetail.getOrderId();
            this.orderSeq = tbOrderDetail.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(this.orderId, this.orderSeq);
            this.assortGb = tbOrderDetail.getAssortGb();
            this.deliMethod = tbOrderDetail.getDeliMethod();
            this.assortId = tbOrderDetail.getAssortId();
            this.itemId = tbOrderDetail.getItemId();
            this.custNm = tbOrderMaster.getOrderName();
            this.assortNm = itasrt.getAssortNm();
            this.availableQty = Utilities.nullOrEmptyFilter(lsdpsd.getDepositQty());
            // 옵션은 밖에서
        }

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime orderDt;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long availableQty;
    }
}
