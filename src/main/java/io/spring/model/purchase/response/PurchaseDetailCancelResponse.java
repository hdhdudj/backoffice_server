package io.spring.model.purchase.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseDetailCancelResponse {
    private String purchaseNo;
    private String userId;
    private List<Items> items;
    @Getter
    @Setter
    public static class Items{
        private String purchaseSeq;
    }
}
