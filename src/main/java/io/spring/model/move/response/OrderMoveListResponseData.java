package io.spring.model.move.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문이동지시 조회 리스트 가져오는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMoveListResponseData {
    public OrderMoveListResponseData(Lsdpsd lsdpsd){
        this.depositNo = lsdpsd.getDepositNo();
        this.depositSeq = lsdpsd.getDepositSeq();
        this.assortId = lsdpsd.getAssortId();
        this.itemId = lsdpsd.getItemId();

        this.depositDt = Utilities.removeTAndTransToStr(lsdpsd.getLsdpsm().getDepositDt());

        TbOrderDetail tbOrderDetail = lsdpsd.getLspchd().getTbOrderDetail();
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.deliMethod = tbOrderDetail.getDeliMethod();
        this.qty = tbOrderDetail.getQty();
        this.orderStoreCd = tbOrderDetail.getStorageId();

        Itasrt itasrt = tbOrderDetail.getItasrt();
        this.assortNm = itasrt.getAssortNm();
//        this.optionNm1 = itasrt.getItvariList().get(0).getOptionNm(); 바깥에서 set
    }
    private String orderStoreCd;
    // lsdpsd
    private String depositNo;
    private String depositSeq;
    private String assortId;
    private String itemId;
    // lsdpsm
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private String depositDt;
    // tbOrderDetail
    private String orderId;
    private String orderSeq;
    private String deliMethod;
    private Long qty;
    // itasrt
    private String assortNm;
    // itvari
    private String optionNm1;
    private String optionNm2;
}
