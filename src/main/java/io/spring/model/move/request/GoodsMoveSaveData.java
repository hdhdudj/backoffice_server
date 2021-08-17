package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsMoveSaveData {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date shipIndDt;
    private String oStoreCd; // 목적지(to) 코드 (출고) - 발주할 때 넣는 창고 
    private String storeCd; // 나가는(from) 창고 - 출고할 때 넣는 창고
    private String deliMethod;
    private List<Goods> goods;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Goods{
        public Goods(Ititmc ititmc){
            this.assortId = ititmc.getAssortId();
            this.itemId = ititmc.getItemId();
        }
        private String assortId;
        private String itemId;
        private String assortNm;
        private String optionNm;
        private Long canShipQty; // 이동가능수량
        private Long orderQty; // 주문수량
        private Long shipQty; // 이동수량
    }
}
