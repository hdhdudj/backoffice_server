package io.spring.model.move.response;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 상품 선택창 검색 시 상품 리스트를 반환할 때 이용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsModalListResponseData {
    public GoodsModalListResponseData(String storeCd, String purchaseVendorId, String assortId, String assortNm){
        this.storeCd = storeCd;
        this.purchaseVendorId = purchaseVendorId;
        this.assortId = assortId;
        this.assortNm = assortNm;
    }
    private String storeCd;
    private String purchaseVendorId;
    private String assortId;
    private String assortNm;
    private List<Goods> goods;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Goods{
        public Goods(Ititmc ititmc, Itasrt itasrt){
            this.assortId = ititmc.getAssortId();
            this.itemId = ititmc.getItemId();
            this.assortNm = itasrt.getAssortNm();
            this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
//            this.brandNm = itasrt.getIfBrand().getBrandNm(); 바깥에서 set
            this.qty = ititmc.getQty() == null? 0l : ititmc.getQty();
            this.availableQty = ititmc.getShipIndicateQty() == null? this.qty : this.qty - ititmc.getShipIndicateQty();
            this.cost = ititmc.getStockAmt();
//            this.storeCd = storeCd; // 바깥에서 set
            this.shipQty = 0l;
        }
        private String storeCd;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String brandNm;
        private String optionNm1;
        private String optionNm2;
        private Long qty;
        private Long orderQty;
        private Long availableQty;
        private Long shipQty;
        private Float cost;
    }
}
