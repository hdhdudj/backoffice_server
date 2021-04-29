package io.spring.model.goods.response;

import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsInsertResponseData {
    @Builder
    public GoodsInsertResponseData(GoodsInsertRequestData goodsInsertRequestData, List<Attributes> attributesList, List<Items> itemsList){
        this.assortId = goodsInsertRequestData.getAssortId();
        this.assortNm = goodsInsertRequestData.getAssortNm();
        this.assortColor = goodsInsertRequestData.getAssortColor();
        this.brandId = goodsInsertRequestData.getBrandId();
        this.origin = goodsInsertRequestData.getOrigin();
        this.manufactureNm = goodsInsertRequestData.getManufactureNm();
        this.assortModel = goodsInsertRequestData.getAssortModel();
        this.taxGb = goodsInsertRequestData.getTaxGb();
        this.description = goodsInsertRequestData.getDescription();
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
    private List<GoodsInsertRequestData.Description> description;
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
