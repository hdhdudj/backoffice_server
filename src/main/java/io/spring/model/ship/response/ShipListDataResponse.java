package io.spring.model.ship.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 출고 - 출고리스트 : 출고리스트 화면에서 조건 검색으로 리스트 가져올 때도 이용됨.
*/
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipListDataResponse {
    private String orderId;
    private String orderSeq;
    private String orderKey;
    private String receiveNm;
    private String receiveTel;
    private String receiveHp;
    private String receiveZipNo;
    private String receiveAddress;
    private String orderRequest;
    private String assortNm;
    private String optionNm;
    private String image;
    private Long itemQty;
    private String assortId;
    private String itemId;
    private String assortKey;
}
