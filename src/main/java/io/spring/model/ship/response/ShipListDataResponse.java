package io.spring.model.ship.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbMemberAddress;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 출고 - 출고리스트 : 출고리스트 화면에서 조건 검색으로 리스트 가져올 때도 이용됨.
 * 주문번호/주문자명/수취인명/수취인전화번호/수취인휴대폰번호/수취인우편번호/주소/주문요청사항/상품명/옵션/이미지/상품수량/상품코드
*/
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipListDataResponse {
    public ShipListDataResponse(LocalDate start, LocalDate end, String shipId, String assortId, String assortNm, String vendorId)
    {
        this.startDt = start;
        this.endDt = end;
        this.shipId = shipId;
        this.assortId = assortId;
        this.assortNm = assortNm;
        this.vendorId = vendorId;
    }
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDt;
    @JsonSerialize(using = LocalDateSerializer.class)
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
        public Ship(Lsshpd lsshpd){
            TbOrderDetail tod = lsshpd.getTbOrderDetail();
            TbOrderMaster tom = tod.getTbOrderMaster();
            TbMemberAddress tma = tom.getTbMemberAddress();
            Itasrt itasrt = tod.getItasrt();
            Ititmm ititmm = tod.getItitmm();
            Itvari itvari1 = ititmm.getItvari1();
            Itvari itvari2 = ititmm.getItvari2();
            Itvari itvari3 = ititmm.getItvari3();
            this.orderId = tod.getOrderId();
            this.orderSeq = tod.getOrderSeq();
            this.orderKey = Utilities.addDashInMiddle(orderId, orderSeq);
            this.custNm = tom.getOrderName();
            this.receiverNm = tma.getDeliNm();
            this.receiverTel = tma.getDeliTel();
            this.receiverHp = tma.getDeliHp();
            this.receiverZonecode = tma.getDeliZonecode();
            this.receiverAddr1 = tma.getDeliAddr1();
            this.receiverAddr2 = tma.getDeliAddr2();
            this.orderMemo = tom.getOrderMemo();
            this.assortNm = itasrt.getAssortNm();
            this.optionNm1 = itvari1 == null? "" : itvari1.getOptionNm();
            this.optionNm2 = itvari2 == null? "" : itvari2.getOptionNm();
            this.optionNm3 = itvari3 == null? "" : itvari3.getOptionNm();
            this.imagePath = tod.getListImageData();
            this.purchaseQty = tod.getQty();
            this.assortId = itasrt.getAssortId();
            this.itemId = ititmm.getItemId();
            this.itemKey = Utilities.addDashInMiddle(assortId, itemId);
        }
        private String orderId;
        private String orderSeq;
        private String orderKey;
        private String custNm;
        private String receiverNm;
        private String receiverTel;
        private String receiverHp;
        private String receiverZonecode;
        private String receiverAddr1;
        private String receiverAddr2;
        private String orderMemo;
        private String assortNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String imagePath;
        private Long purchaseQty;
        private String assortId;
        private String itemId;
        private String itemKey;
    }
}
