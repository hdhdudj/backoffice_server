package io.spring.api;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.spring.core.common.CommonRepository;
import io.spring.core.goods.GoodsRepository;
import io.spring.data.goods.GoodsRequestData;
import io.spring.infrastructure.util.ApiResponseMessage;

@RestController
@RequestMapping(value = "/goods")
public class GoodsApi {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private GoodsRepository goodsRepository;
	private CommonRepository commonRepository;
	
	@Autowired
	public GoodsApi(GoodsRepository goodsRepository, CommonRepository commonRepository) {
		this.goodsRepository = goodsRepository;
		this.commonRepository = commonRepository;
	}
	
	@RequestMapping(path = "/select")
	public ResponseEntity selectGoodsListAll() {
		logger.debug("select goods");
		
		List<HashMap<String, Object>> r = goodsRepository.selectGoodsListAll();
		
		ApiResponseMessage res = null;
		
		if(r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS","", r);
		}
		else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "데이타 없음", null);
		}
		
		return ResponseEntity.ok(res);
	}
	
	@RequestMapping(path = "/insert")
	public ResponseEntity insertGoods(@RequestBody GoodsRequestData goodsRequestData) {
		logger.debug("insert goods");
		
		HashMap<String, Object> arr = new HashMap<String, Object>();
		arr.put("seqName", "seq_ITASRT");
		HashMap<String, Object> x1 = commonRepository.getSequence(arr);
		System.out.println("x1 = " + x1.get("nextval"));
		
		goodsRequestData.setAssortId((long)x1.get("nextval"));
		Boolean b = goodsRepository.insertGoods(goodsRequestData);
		
		ApiResponseMessage res = null;
		
		
		return null;
	}
}
