package io.spring.service.goods;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.model.goods.response.GetStockListResponseData;
import io.spring.model.goods.response.ProductsListResponseData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyBatisGoodsService {
    private final MyBatisGoodsDao myBatisGoodsDao;

	public List<HashMap<String, Object>> getGoodsList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsList(param);

        return goodsList;
    }

	public List<HashMap<String, Object>> getGoodsItemList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsItemList(param);

		return goodsList;
	}

	public List<HashMap<String, Object>> getGoodsItemListWithCategory(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsItemListWithCategory(param);

		return goodsList;
	}

	public List<HashMap<String, Object>> getGoodsStockList(HashMap<String, Object> param) {
		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getGoodsStockList(param);

		return goodsList;
	}

	public GetStockListResponseData getItitmc(HashMap<String, Object> param) {
		String storageId = param.get("storageId") == null ? null : param.get("storageId").toString();
		String vendorId = param.get("vendorId") == null ? null : param.get("vendorId").toString();
		String assortId = param.get("assortId") == null ? null : param.get("assortId").toString();
		String assortNm = param.get("assortNm") == null ? null : param.get("assortNm").toString();
		String channelGoodsNo = param.get("channelGoodsNo") == null ? null : param.get("channelGoodsNo").toString();

		List<HashMap<String, Object>> goodsList = myBatisGoodsDao.getItitmc(param);

		List<GetStockListResponseData.Goods> l = new ArrayList<>();

		GetStockListResponseData ret = new GetStockListResponseData(storageId, vendorId, assortId, assortNm);

		for (HashMap<String, Object> o : goodsList) {

			GetStockListResponseData.Goods goods = new GetStockListResponseData.Goods(o);

			goods.setOrderQty(0L);

			l.add(goods);

		}

		ret.setGoods(l);

		return ret;
	}

	public ProductsListResponseData getItemList(LocalDate start, LocalDate end, String saleYn, String displayYn,
			Long productId, String productNm, Long masterId, String masterNm) {

		LocalDateTime startDt = start.atStartOfDay();
		LocalDateTime endDt = end.atTime(23, 59, 59);

		ProductsListResponseData r = new ProductsListResponseData(start, end, saleYn, displayYn, productId, productNm,
				masterId, masterNm);

		List<ProductsListResponseData.Product> l = getProductsList(startDt, endDt, saleYn, displayYn, productId,
				productNm,
				masterId, masterNm);

		r.setProductsList(l);
		return r;

	}

	private List<ProductsListResponseData.Product> getProductsList(LocalDateTime start, LocalDateTime end,
			String saleYn,
			String displayYn, Long productId, String productNm, Long masterId, String masterNm) {

		HashMap<String, Object> p = new HashMap<String, Object>();

		p.put("start", start);
		p.put("end", end);
		p.put("saleYn", saleYn);
		p.put("productId", productId);
		p.put("productNm", productNm);
		p.put("masterId", masterId);
		p.put("masterNm", masterNm);

		List<HashMap<String, Object>> l = myBatisGoodsDao.getProductsList(p);

		List<ProductsListResponseData.Product> r = new ArrayList<>();

		for (HashMap<String, Object> o : l) {

			System.out.println(o);
			ProductsListResponseData.Product v = new ProductsListResponseData.Product(o);

			// v.setMainImage(getProductMainImage(o.getProductId()));
			// v.setAddImage(getProductAddImage(o.getProductId()));
			// v.setAddInfos(getProductAddInfo(o.getProductId()));

			r.add(v);
		}



		return r;

	}

}
