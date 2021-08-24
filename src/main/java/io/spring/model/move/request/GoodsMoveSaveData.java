package io.spring.model.move.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 상품이동지시 저장 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsMoveSaveData {
    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate moveIndDt;
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
        private String storeCd;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String optionNm;
        private Long availableQty; // 이동가능수량
        private Long orderQty; // 주문수량
        private Long moveQty; // 이동수량
    }
}
