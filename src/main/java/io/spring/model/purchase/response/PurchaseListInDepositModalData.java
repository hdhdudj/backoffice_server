package io.spring.model.purchase.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseListInDepositModalData {
    private Date startDt;
    private Date endDt;
    private String purchaseVendorId;
    private Purchase purchases;

    @Getter
    @Setter
    public static class Purchase{
        private String purchaseNo;
        private String purchaseStatus;
    }
}
