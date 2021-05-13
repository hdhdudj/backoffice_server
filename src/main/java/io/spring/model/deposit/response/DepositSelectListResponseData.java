package io.spring.model.deposit.response;

import io.spring.model.deposit.entity.Lsdpsd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositSelectListResponseData {
    public DepositSelectListResponseData(Lsdpsd lsdpsd){
        this.depositNo = lsdpsd.getDepositNo();
        this.depositSeq = lsdpsd.getDepositSeq();
        this.assortId = lsdpsd.getAssortId();
        this.itemId = lsdpsd.getItemId();
        this.extraUnitcost = lsdpsd.getExtraUnitcost();
    }
    private String depositNo;
    private String depositSeq;
    private String depositVendorId;
    private String vdNm;
    private String assortId;
    private String itemId;
    private String assortNm;
    private String optionNm1;
    private String optionNm2;
    private Long depositQty;
    private Float extraUnitcost;
}
