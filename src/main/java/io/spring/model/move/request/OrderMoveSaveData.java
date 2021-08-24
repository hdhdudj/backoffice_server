package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 주문이동지시 저장 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMoveSaveData {
    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDt;
    private String storageId;
    private String assortId;
    private String assortNm;
    private String deliMethod;
    private List<Move> moves;
//    public OrderMoveSaveData(Lsdpsd lsdpsd){
//        this.depositNo = lsdpsd.getDepositNo();
//        this.depositSeq = lsdpsd.getDepositSeq();
//        this.assortId = lsdpsd.getAssortId();
//        this.itemId = lsdpsd.getItemId();
//
//        this.depositDt = lsdpsd.getLsdpsm().getDepositDt();
//
//        TbOrderDetail tbOrderDetail = lsdpsd.getLsdpsp().getTbOrderDetail();
//        this.orderId = tbOrderDetail.getOrderId();
//        this.orderSeq = tbOrderDetail.getOrderSeq();
//        this.deliMethod = tbOrderDetail.getDeliMethod();
//        this.qty = tbOrderDetail.getQty();
//
//        Itasrt itasrt = tbOrderDetail.getItasrt();
//        this.assortNm = itasrt.getAssortNm();
//        this.optionNm = itasrt.getItvariList().get(0).getOptionNm();
//    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Move{
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
        private String storageId;
        // itasrt
        private String assortNm;
        // itvari
        private String optionNm1;
        private String optionNm2;
    }
}
