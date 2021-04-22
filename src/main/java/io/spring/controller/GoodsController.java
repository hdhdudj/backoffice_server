package io.spring.controller;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.goods.GoodsRequestData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.service.goods.JpaGoodsService;
import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private MyBatisGoodsDao goodsRepository;
	private MyBatisCommonDao myBatisCommonDao;
	private JpaGoodsService jpaGoodsService;

	@Autowired
	public GoodsController(MyBatisGoodsDao goodsRepository, MyBatisCommonDao myBatisCommonDao, JpaGoodsService jpaGoodsService) {
		this.goodsRepository = goodsRepository;
		this.myBatisCommonDao = myBatisCommonDao;
		this.jpaGoodsService = jpaGoodsService;
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
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "����Ÿ ����", null);
		}
		
		return ResponseEntity.ok(res);
	}
	
	@RequestMapping(path = "/insert")
	public ResponseEntity insertGoodsMyBatis(@RequestBody GoodsRequestData goodsRequestData) {
		logger.debug("insert goods");
		
		HashMap<String, Object> arr = new HashMap<String, Object>();
		arr.put("seqName", "seq_ITASRT");
		HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
		System.out.println("x1 = " + x1.get("nextval"));
		
		goodsRequestData.setAssortId(StringUtils.leftPad(Long.toString((long)x1.get("nextval")), 9, '0'));
		Boolean b = goodsRepository.insertGoods(goodsRequestData);
		
		ApiResponseMessage res = null;
		
		return null;
	}
	
	
	@RequestMapping(path = "/inserttest")
	public ResponseEntity insertGoodsJpa(@RequestBody GoodsRequestData goodsRequestData) {
		logger.debug("insert goods by jpa");
		Itasrt itasrt = new Itasrt(goodsRequestData);
		// itasrt에 goods 정보 저장
		long assortId = jpaGoodsService.save(itasrt);
		goodsRequestData.setAssortId(StringUtils.leftPad(Long.toString(assortId), 9, '0'));

		// itasrd에 연관 정보 저장
		jpaGoodsService.save(goodsRequestData);

		// itvari에 assort_id별 옵션요소 저장(색상, 사이즈)
		jpaGoodsService.saveItvariList(goodsRequestData);

		// ititmm에 assort_id별 item 저장
		jpaGoodsService.saveItemList(goodsRequestData);
		ApiResponseMessage res = null;

//		if(r.size() > 0) {
//			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS","", r);
//		}
//		else {
//			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "����Ÿ ����", null);
//		}

		return ResponseEntity.ok(res);
	}
}
