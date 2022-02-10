package io.spring.model.move.response;

import java.util.List;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Ititmc;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 선택창 검색 시 상품 리스트를 반환할 때 이용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoodsModalListResponseData {
    public GoodsModalListResponseData(String storeCd, String purchaseVendorId, String assortId, String assortNm){
        this.storageId = storeCd;
        this.purchaseVendorId = purchaseVendorId;
        this.assortId = assortId;
        this.assortNm = assortNm;
    }
    private String storageId;
    private String purchaseVendorId;
    private String assortId;
    private String assortNm;
    private List<Goods> goods;

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Goods implements SetOptionInterface {
		public Goods(Ititmc ititmc) {
			this.storageId = ititmc.getCmstgm().getStorageId();
			this.rackNo = ititmc.getStorageId(); // 랙의 재고를 가져오는 방식임.
            this.assortId = ititmc.getAssortId();
            this.itemId = ititmc.getItemId();
			this.assortNm = ititmc.getItasrt().getAssortNm();
			this.channelId = ititmc.getItasrt().getVendorId();
            this.goodsKey = Utilities.addDashInMiddle(this.assortId, this.itemId);
            this.depositDt = Utilities.removeTAndTransToStr(ititmc.getEffStaDt());
//            this.brandNm = itasrt.getIfBrand().getBrandNm(); 바깥에서 set
            this.qty = ititmc.getQty() == null? 0l : ititmc.getQty();
            this.availableQty = ititmc.getShipIndicateQty() == null? this.qty : this.qty - ititmc.getShipIndicateQty();
            this.cost = ititmc.getStockAmt();
//            this.storeCd = storeCd; // 바깥에서 set
            this.moveQty = 0l;

			this.optionNm1 = ititmc.getItitmm().getItvari1() == null ? ""
					: ititmc.getItitmm().getItvari1().getOptionNm();
			this.optionNm2 = ititmc.getItitmm().getItvari2() == null ? ""
					: ititmc.getItitmm().getItvari2().getOptionNm();
			this.optionNm3 = ititmc.getItitmm().getItvari3() == null ? ""
					: ititmc.getItitmm().getItvari3().getOptionNm();

			this.brandNm = ititmc.getItasrt().getItbrnd() == null ? "" : ititmc.getItasrt().getItbrnd().getBrandNm();

        }
        private String storageId;
        private String depositDt;
        private String assortId;
        private String itemId;
        private String goodsKey;
        private String assortNm;
        private String brandNm;
        private String optionNm1;
        private String optionNm2;
        private String optionNm3;
        private String channelId;
        private Long qty;
        private Long orderQty;
        private Long availableQty;
        private Long moveQty;
        private Float cost;
		private String rackNo;

    }
}
