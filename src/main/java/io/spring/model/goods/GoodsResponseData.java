package io.spring.model.goods;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsResponseData {
    public GoodsResponseData(GoodsRequestData goodsRequestData){
        this.assortId = goodsRequestData.getAssortId();
        this.assortNm = goodsRequestData.getAssortNm();
        this.assortColor = goodsRequestData.getAssortColor();
        this.brandId = goodsRequestData.getBrandId();
        this.origin = goodsRequestData.getOrigin();
        this.manufactureNm = goodsRequestData.getManufactureNm();
        this.assortModel = goodsRequestData.getAssortModel();
        this.taxGb = goodsRequestData.getTaxGb();
        this.longDesc = goodsRequestData.getLongDesc();
        this.shortDesc = goodsRequestData.getShortDesc();
    }

    private String assortId;
    private String assortNm;
    private String assortColor;
    private String brandId;
    private String origin;
    private String manufactureNm;
    private String assortModel;
    private String taxGb;
    private String longDesc;
    private String shortDesc;
    private List<Attributes> attributesList;
    private List<Items> items;

    @Getter
    @Setter
    class Attributes{
        private String seq;
        private String optionNm;
        private String variationGb;
    }

    @Getter
    @Setter
    class Items{
        private String itemId;
        private String color;
        private String size;
    }
}
