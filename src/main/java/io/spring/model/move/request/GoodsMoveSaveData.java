package io.spring.model.move.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsMoveSaveData {
    private String assortId;
    private String itemId;
    private String assortNm;
    private String optionNm;
    private Long canShipQty; // 이동가능수량
    private Long shipQty; // 이동수량
}
