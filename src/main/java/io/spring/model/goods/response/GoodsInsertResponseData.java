package io.spring.model.goods.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.spring.infrastructure.custom.CustomLocalDateTimeDeSerializer;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.request.GoodsInsertRequestData;
import lombok.*;

import java.time.LocalDateTime;
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
        this.margin = goodsInsertRequestData.getMargin();
        this.description = goodsInsertRequestData.getDescription();
        this.attributesList = attributesList;
        this.itemsList = itemsList;
    }

    private String assortId;
    private String assortColor;
    // 상품기본설정 화면
    private String assortNm; // 상품명
    private String assortModel; // 모델번호
    private String taxGb; // 과세/면세
    private String assortState; // 상품상태 : 진행중(01), 일시중지(02), 단품(03), 품절(04)
    private String shortageYn; // 판매상태 : 진행중(01), 중지(02)
    private String asLength; // 깊이
    private String asHeight; // 높이
    private String asWidth; // 너비
    private String weight; // 무게
    private String origin; // 원산지
    private String brandId; // 브랜드 코드
    private String dispCategoryId; // erp 카테고리 코드
    private String manufactureNm; // 제조회사
    private String localSale; // 판매가
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime sellStaDt; // 판매기간 - 시작
    @JsonDeserialize(using = CustomLocalDateTimeDeSerializer.class)
    private LocalDateTime sellEndDt; // 판매기간 - 끝
    private String deliPrice; // 매입가
    private String localPrice; // 정가
    private String margin; // 마진율

    // 이미지 설정 화면

    // 상품 가격 관리(MD팀) 화면
    private String mdRrp; // RRP
    private String mdYear; // 자료연도
    private String mdTax; // TAX(자료)
    private String mdVatrate; // 부가세율
    private String mdDiscountRate; // 할인율
    private String mdGoodsVatrate; // 상품마진율

    // 상품 가격 관리(구매팀) 화면
    private String vendorNm; // 구매처명
    private String vendorId; // 구매처 코드
    private String buyRrpIncrement; // RRP 인상률
    private String buySupplyDiscount; //
    private String buyTax; // TAX(구매)
    private String mdMargin; // 정기마진율
    private String buyExchangeRate; // 적용환율

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
            this.addPrice = ititmm.getAddPrice() == null? "" : Float.toString(ititmm.getAddPrice());
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
