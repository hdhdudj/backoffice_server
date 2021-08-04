package io.spring.model.ship.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIndicateSaveData {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    // tbOrderMaster
    private Date orderDt;

    // tbOrderDetail
    private String orderId;
    private String orderSeq;
    private String assortGb;
    private String deliMethod;
    private String assortId;
    private String itemId;

    // tbMember
    private String custNm;

    // Itasrt
    private String assortNm;

    // Itvari
    private String optionNm1;
    private String optionNm2;

    // 입력받음.
    private Long qty;
}
