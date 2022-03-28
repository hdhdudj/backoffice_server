package io.spring.model.stock.request;

import io.spring.model.stock.reponse.GoodsStockXml;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
public class GoodsStockRequest {
    private GoodsData[] goodsData;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GoodsData{
        private String goodsNo;
        private String optionFl;
        private Long totalStock;
        private StockOptionData[] stockOptionData;
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class StockOptionData{
        private String sno;
        private Long stockCnt;
        private String optionViewFl;
        private String optionSellFl;
        private Float optionPrice;
    }
}
