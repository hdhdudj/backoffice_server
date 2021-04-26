package io.spring.model.goods;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsResponseData {
    @Builder
    public GoodsResponseData(GoodsRequestData goodsRequestData, List<Attributes> attributesList, List<Items> itemsList){
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
        this.attributesList = attributesList;
        this.itemsList = itemsList;
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
    private List<Items> itemsList;

    @Getter
    @Setter
    public class Attributes{
        private String seq;
        private String optionNm;
        private String variationGb;
    }

    @Getter
    @Setter
    public class Items{
        private String itemId;
        private String color;
        private String size;
    }
}