package io.spring.model.goods.response;

import io.spring.model.goods.entity.Itasrt;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsSelectListResponseData {
    public GoodsSelectListResponseData(String shortageYn, LocalDate regDtBegin, LocalDate regDtEnd, String assortId, String assortNm){
        this.regDtBegin = regDtBegin;
        this.regDtEnd = regDtEnd;
        this.assortId = assortId;
        this.shortageYn = shortageYn;
        this.assortNm = assortNm;
    }
    private LocalDate regDtBegin;
    private LocalDate regDtEnd;
    private String assortId;
    private String shortageYn;
    private String assortNm;
    private List<Goods> goodsList;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Goods{
        public Goods(Itasrt itasrt){
            this.assortId = itasrt.getAssortId();
            this.assortNm = itasrt.getAssortNm();
            this.shortageYn = itasrt.getShortageYn();
            this.brandId = itasrt.getBrandId();
            this.dispCategoryId = itasrt.getDispCategoryId();
//            this.brandNm = itasrt.getIfBrand().getBrandNm(); 바깥에서 set
            this.categoryNm = itasrt.getCategoryId() == null || itasrt.getCategoryId().trim().equals("")? "" : itasrt.getItcatg().getCategoryNm();
        }
        private String assortNm;
        private String brandNm;
        private String shortageYn;
        private String assortId;
        private String brandId;
        private String categoryNm;
        private String dispCategoryId;
    }

}
