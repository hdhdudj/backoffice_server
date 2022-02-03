package io.spring.model.ship.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.spring.infrastructure.custom.CustomLocalDateDeSerializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 출고 - 출고지시 : 출고지시 리스트(주문번호 기준) request DTO (출고지시 저장할 때 이용)
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateSaveListData {
    public ShipIndicateSaveListData(LocalDate startDt, LocalDate endDt, String assortId, String assortNm, String vendorId, String orderId){
        this.startDt = startDt;
        this.endDt = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = vendorId;
        this.orderId = orderId;
    }
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = CustomLocalDateDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
    private String assortId;
    private String storageId;
    private String assortNm;
    private String vendorId;
    private String orderId;
    private List<Ship> ships;
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{
        public Ship(TbOrderDetail tbOrderDetail) {
            TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
            Itasrt itasrt = tbOrderDetail.getItitmm().getItasrt();
            TbMember tbMember = tbOrderMaster.getTbMember();
            this.orderDt = tbOrderDetail.getTbOrderMaster().getOrderDate();
            this.orderId = tbOrderDetail.getOrderId();
            this.orderSeq = tbOrderDetail.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(this.orderId, this.orderSeq);
            this.assortGb = itasrt.getAssortGb();
            this.deliMethod = tbOrderDetail.getDeliMethod();
            this.assortId = tbOrderDetail.getAssortId();
            this.itemId = tbOrderDetail.getItemId();
            this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
            this.custNm = tbMember.getCustNm();
            this.assortNm = itasrt.getAssortNm();
//            this.qty = 0l;
            // optionNm1, optionNm2는 외부에서 set
        }
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime orderDt;
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private Long availableQty;
        private Long qty;
		private String shipId;
		private String shipSeq;
		private String storageId;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		private LocalDateTime receiptDt;
    }
}
