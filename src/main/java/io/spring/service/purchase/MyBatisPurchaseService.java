package io.spring.service.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.purchase.MyBatisPurchaseDao;
import io.spring.model.purchase.response.PurchaseItemResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisPurchaseService {
    private final MyBatisPurchaseDao myBatisPurchaseDao;

    public List<HashMap<String, Object>> getPurchaseList(HashMap<String, Object> param) {
        List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getPurchaseList(param);
        return purchaseList;
    }

	public List<HashMap<String, Object>> getOrderListByPurchaseVendor(HashMap<String, Object> param) {
		List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getOrderListByPurchaseVendor(param);
		return purchaseList;
	}

	public List<HashMap<String, Object>> getOrderListByPurchaseVendorItem(HashMap<String, Object> param) {
		List<HashMap<String, Object>> purchaseList = myBatisPurchaseDao.getOrderListByPurchaseVendorItem(param);
		return purchaseList;
	}

	public PurchaseItemResponseData getPurchase(HashMap<String, Object> param) {

		HashMap<String, Object> o = myBatisPurchaseDao.getPurchase(param);

		List<HashMap<String, Object>> items = myBatisPurchaseDao.getPurchaseItems(param);

		PurchaseItemResponseData r = new PurchaseItemResponseData();

		// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss",
		// timezone = "Asia/Seoul")

		r.setPurchaseNo(o.get("purchaseNo").toString());
		r.setPurchaseDt((Date) o.get("purchaseDt"));
		r.setPurchaseStatus(o.get("purchaseStatus").toString());
		r.setPurchaseVendorId(o.get("purchaseVendorId").toString());
		r.setPurchaseVendorNm(o.get("purchaseVendorNm").toString());
		r.setCarrier(o.get("carrier").toString());
		r.setDelivery(o.get("delivery").toString());
		r.setPayment(o.get("payment").toString());
		r.setSiteOrderNo(o.get("siteOrderNo").toString());
		r.setStorageNm(o.get("storageNm").toString());
		r.setStoreCd(o.get("storeCd").toString());
		r.setTerms(o.get("terms").toString());
		r.setPurchaseGb(o.get("purchaseGb").toString());

		List<PurchaseItemResponseData.Items> l = new ArrayList<>();

		for (HashMap<String, Object> ob : items) {
			
			
			PurchaseItemResponseData.Items item = new PurchaseItemResponseData.Items();
			
			
			item.setPurchaseNo(ob.get("purchaseNo").toString());
			item.setPurchaseSeq(ob.get("purchaseSeq").toString());
			item.setPurchaseGb(ob.get("purchaseGb").toString());

			if (ob.get("orderId") != null) {
				item.setOrderId(ob.get("orderId").toString());

			}

			if (ob.get("orderSeq") != null) {
				item.setOrderSeq(ob.get("orderSeq").toString());

			}

			item.setAssortId(ob.get("assortId").toString());
			item.setItemId(ob.get("itemId").toString());
			item.setAssortNm(ob.get("assortNm").toString());

			if (ob.get("optionNm1") != null) {
				item.setOptionNm1(ob.get("optionNm1").toString());

			}
			if (ob.get("OptionNm2") != null) {
				item.setOptionNm2(ob.get("OptionNm2").toString());

			}

			if (ob.get("deliMethod") != null) {
				item.setDeliMethod(ob.get("deliMethod").toString());

			}

			BigDecimal mdRrp = (BigDecimal) ob.get("mdRrp");

			item.setMdRrp(mdRrp.floatValue());

			BigDecimal buySupplyDiscount = (BigDecimal) ob.get("buySupplyDiscount");

			item.setBuySupplyDiscount(buySupplyDiscount.floatValue());

			item.setPurchaseQty(Long.valueOf((int) ob.get("purchaseQty")));

			BigDecimal purchaseUnitAmt = (BigDecimal) ob.get("purchaseUnitAmt");

			item.setPurchaseUnitAmt(purchaseUnitAmt.floatValue());

			l.add(item);
			

		}

		r.setItems(l);


		return r;
	}



}
