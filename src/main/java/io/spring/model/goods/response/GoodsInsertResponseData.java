package io.spring.model.goods.response;

import io.spring.model.goods.entity.Ititmm;
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
        private String value;
        private String variationGb;
    }

    @Getter
    @Setter
    public static class Items{
        public Items(Ititmm ititmm){
            this.variationSeq1 = ititmm.getVariationSeq1();
            this.variationSeq2 = ititmm.getVariationSeq2();
            this.variationSeq3 = ititmm.getVariationSeq3();
            this.itemId = ititmm.getItemId();
            // value는 바깥에서 set
            this.addPrice = Float.toString(ititmm.getAddPrice() == null? null : ititmm.getAddPrice());
            this.shortYn = ititmm.getShortYn();
        }
        private String itemId;
        private String variationValue1;
        private String variationValue2;
        private String variationValue3;
        private String variationSeq1;
        private String variationSeq2;
        private String variationSeq3;
        private String addPrice;
        private String shortYn;
    }
}
