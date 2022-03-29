package io.spring.model.stock.reponse;

import io.spring.model.stock.request.GoodsStockRequest;
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
    public GoodsStockXml(GoodsStockRequest goodsStockRequest){
        int size = goodsStockRequest.getGoodsData().length;
        GoodsData[] goodsDatas = new GoodsData[size];
        for (int i = 0; i < size ; i++) {
            goodsDatas[i] = new GoodsData(goodsStockRequest.getGoodsData()[i]);
        }
        this.goodsData = goodsDatas;
    }
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
        public GoodsData(GoodsStockRequest.GoodsData goodsDatum) {
            this.goodsNo = goodsDatum.getGoodsNo();
            this.optionFl = goodsDatum.getOptionFl();
            this.totalStock = goodsDatum.getTotalStock();
            int size = goodsDatum.getStockOptionData().length;
            StockOptionData[] stocks = new StockOptionData[size];
            for (int i = 0; i < size ; i++) {
                stocks[i] = new StockOptionData(goodsDatum.getStockOptionData()[i]);
            }
            this.stockOptionData = stocks;
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
        public StockOptionData(GoodsStockRequest.StockOptionData stockOptionDatum) {
            this.sno = stockOptionDatum.getSno();
            this.stockCnt = stockOptionDatum.getStockCnt();
            this.optionViewFl = stockOptionDatum.getOptionViewFl();
            this.optionSellFl = stockOptionDatum.getOptionSellFl();
            this.optionPrice = stockOptionDatum.getOptionPrice();
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
