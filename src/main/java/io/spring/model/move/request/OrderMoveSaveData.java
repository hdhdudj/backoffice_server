package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMoveSaveData {
    public OrderMoveSaveData(Lsdpsd lsdpsd){
        this.depositNo = lsdpsd.getDepositNo();
        this.depositSeq = lsdpsd.getDepositSeq();
        this.assortId = lsdpsd.getAssortId();
        this.itemId = lsdpsd.getItemId();

        this.depositDt = lsdpsd.getLsdpsm().getDepositDt();

        TbOrderDetail tbOrderDetail = lsdpsd.getLsdpsp().getTbOrderDetail();
        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.deliMethod = tbOrderDetail.getDeliMethod();
        this.qty = tbOrderDetail.getQty();

        Itasrt itasrt = tbOrderDetail.getItasrt();
        this.assortNm = itasrt.getAssortNm();
        this.optionNm = itasrt.getItvariList().get(0).getOptionNm();
    }

    // lsdpsd
    private String depositNo;
    private String depositSeq;
    private String assortId;
    private String itemId;
    // lsdpsm
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date depositDt;
    // tbOrderDetail
    private String orderId;
    private String orderSeq;
    private String deliMethod;
    private Long qty;
    // itasrt
    private String assortNm;
    // itvari
    private String optionNm;
}
