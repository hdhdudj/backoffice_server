package io.spring.model.ship.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 출고 - 출고지시 : list response
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateSaveListResponseData {
    public ShipIndicateSaveListResponseData(Date startDt, Date endDt, String assortId, String assortNm, String vendorId){
        this.startDt = startDt;
        this.endDt = endDt;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = vendorId;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDt;
    private String assortId;
    private String assortNm;
    private String vendorId;
    private List<Ship> ships;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Ship{
        public Ship(TbOrderDetail tbOrderDetail) {
        TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
        Itasrt itasrt = tbOrderDetail.getItasrt();
        TbMember tbMember = tbOrderMaster.getTbMember();
        this.orderDt = tbOrderDetail.getTbOrderMaster().getOrderDate();
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.assortGb = itasrt.getAssortGb();
        this.deliMethod = tbOrderDetail.getDeliMethod();
        this.assortId = tbOrderDetail.getAssortId();
        this.itemId = tbOrderDetail.getItemId();
        this.custNm = tbMember.getCustNm();
        this.assortNm = itasrt.getAssortNm();
//        this.availableQty =
        this.qty = 0l;
        // optionNm1, optionNm2는 외부에서 set
        }
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date orderDt;
        private String orderId;
        private String orderSeq;
        private String assortGb;
        private String deliMethod;
        private String assortId;
        private String itemId;
        private String custNm;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private Long availableQty;
        private Long qty;
    }
}