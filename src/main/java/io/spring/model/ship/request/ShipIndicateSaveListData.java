package io.spring.model.ship.request;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    public ShipIndicateSaveListData(Date startDt, Date endDt, String assortId, String assortNm, String vendorId){
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
            this.orderDt = Utilities.localDateTimeToDate(tbOrderDetail.getTbOrderMaster().getOrderDate());
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
            this.qty = 0l;
            // optionNm1, optionNm2는 외부에서 set
        }
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date orderDt;
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
        private Long availableQty;
        private Long qty;
		private String shipId;
		private String shipSeq;
		private String storageId;
		private String receiptDt;
    }
}
