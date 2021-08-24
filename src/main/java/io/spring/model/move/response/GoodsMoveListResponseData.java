package io.spring.model.move.response;

import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsMoveListResponseData {
    public GoodsMoveListResponseData(Ititmc ititmc){
        this.assortId = ititmc.getAssortId();
        this.itemId = ititmc.getItemId();
        this.assortNm = ititmc.getItasrt().getAssortNm();
        this.optionNm = ititmc.getItasrt().getItvariList().size() > 0? ititmc.getItasrt().getItvariList().get(0).getOptionNm() : null;
        this.canMoveQty = ititmc.getQty() - ititmc.getShipIndicateQty();
        this.moveQty = 0l;
    }
    private String assortId;
    private String itemId;
    private String assortNm;
    private String optionNm;
    private Long canMoveQty; // 이동가능수량
    private Long moveQty; // 이동수량
}
