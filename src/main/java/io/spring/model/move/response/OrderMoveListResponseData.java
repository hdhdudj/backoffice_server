package io.spring.model.move.response;

import java.util.HashMap;

import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.SetOptionInterface;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.entity.Lspchd;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문이동지시 조회 리스트 가져오는 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMoveListResponseData implements SetOptionInterface {
    public OrderMoveListResponseData(Lspchd lspchd){
		TbOrderDetail tbOrderDetail = lspchd.getTbOrderDetail();
        this.depositNo = lspchd.getDepositNo();
        this.depositSeq = lspchd.getDepositSeq();
        this.depositKey = Utilities.addDashInMiddle(depositNo,depositSeq);
        this.assortId = lspchd.getAssortId();
        this.itemId = lspchd.getItemId();
		this.depositDt = Utilities.removeTAndTransToStr(lspchd.getLsdpsd().getLsdpsm().getDepositDt());

        this.orderId = tbOrderDetail.getOrderId();
        this.orderSeq = tbOrderDetail.getOrderSeq();
        this.orderKey = Utilities.addDashInMiddle(orderId,orderSeq);
        this.deliMethod = tbOrderDetail.getDeliMethod();
        this.qty = tbOrderDetail.getQty();
		this.orderStoreCd = tbOrderDetail.getStorageId();

        Itasrt itasrt = tbOrderDetail.getItasrt();
        this.assortNm = itasrt.getAssortNm();
//        this.optionNm1 = itasrt.getItvariList().get(0).getOptionNm(); 바깥에서 set
    }

	public OrderMoveListResponseData(HashMap<String, Object> m) {
		this.depositNo = (String) m.get("depositNo");
		this.depositSeq = (String) m.get("depositSeq");
		this.depositKey = (String) m.get("depositKey");
		this.assortId = (String) m.get("assortId");
		this.itemId = (String) m.get("itemId");
		this.depositDt = m.get("depositDt").toString().substring(0, 19);

		this.orderId = (String) m.get("orderId");
		this.orderSeq = (String) m.get("orderSeq");
		this.orderKey = (String) m.get("orderKey");
		this.deliMethod = (String) m.get("deliMethod");
		this.qty = Long.valueOf((int) m.get("qty"));
		
		
		
		this.orderStoreCd = (String) m.get("orderStorageId");

		this.assortNm = (String) m.get("assortNm");

		this.optionNm1 = (String) m.get("optionNm1");
		this.optionNm2 = (String) m.get("optionNm2");
	}

	private String orderStoreCd;
    // lsdpsd
    private String depositNo;
    private String depositSeq;
    private String depositKey;
    private String assortId;
    private String itemId;
    // lsdpsm
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private String depositDt;
    // tbOrderDetail
    private String orderId;
    private String orderSeq;
    private String orderKey;
    private String deliMethod;
    private Long qty;
    // itasrt
    private String assortNm;
    // itvari
    private String optionNm1;
    private String optionNm2;
}
