package io.spring.model.stock.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsStockXml {
    public GoodsStockXml(String goodsNo, String optionFl, Long totalStock){
        this.goodsData = new GoodsData[1];
        this.goodsData[0] = new GoodsData(goodsNo, optionFl, totalStock, new StockOptionData("a"));
    }
    @XmlElement(name = "goods_data")
    private GoodsData[] goodsData;

    @Getter
    @Setter
    @XmlRootElement(name = "goods_data")
    @XmlAccessorType(XmlAccessType.FIELD)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GoodsData{
        public GoodsData(String goodsNo, String optionFl, Long totalStock, StockOptionData stockOptionData){
            this.goodsNo = goodsNo;
            this.optionFl = optionFl;
            this.totalStock = totalStock;
            this.stockOptionData = null;
//            this.stockOptionData = new StockOptionData[1];
//            this.stockOptionData[0] = new StockOptionData("a");
        }
        @XmlAttribute(name="idx")
        int idx;
        private String goodsNo;
        private String optionFl;
        private Long totalStock;
        @XmlElement(name = "stockOptionData")
        private StockOptionData[] stockOptionData;
    }

    @Getter
    @Setter
    @XmlRootElement(name = "stockOptionData")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class StockOptionData{
        public StockOptionData(String a){
            this.sno = null;
            this.stockCnt = null;
            this.optionViewFl = null;
            this.optionSellFl = null;
            this.optionPrice = null;
        }
        @XmlAttribute(name="idx")
        int idx;
        private String sno;
        private Long stockCnt;
        private String optionViewFl;
        private String optionSellFl;
        private Float optionPrice;
    }
}
