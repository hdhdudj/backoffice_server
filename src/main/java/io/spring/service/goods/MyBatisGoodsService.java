package io.spring.service.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.model.goods.response.GetStockListResponseData;
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

}
