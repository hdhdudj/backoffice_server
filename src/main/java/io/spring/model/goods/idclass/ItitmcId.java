package io.spring.model.goods.idclass;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ItitmcId implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

	public ItitmcId(String storageId, LocalDateTime depositDt, DepositListWithPurchaseInfoData.Deposit deposit) {
		this.storageId = storageId;
		this.assortId = deposit.getAssortId();
		this.itemId = deposit.getItemId();
		this.itemGrade = "11";
		this.effStaDt = depositDt;
		this.effEndDt = depositDt;
	}

	public ItitmcId(String storageId, LocalDateTime effStaDt, String assortId, String itemId, String itemGrade) {
		this.storageId = storageId;
		this.assortId = assortId;
		this.itemId = itemId;
		this.itemGrade = itemGrade;
		this.effStaDt = effStaDt;
		this.effEndDt = effStaDt;
	}

    private String storageId;
    private String assortId;
    private String itemId;
    private String itemGrade;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effEndDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime effStaDt;
}
