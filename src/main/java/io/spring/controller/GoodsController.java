package io.spring.controller;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GoodsInsertResponseData;
import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import io.spring.model.goods.response.GoodsSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.goods.JpaGoodsService;
import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MyBatisGoodsDao goodsRepository;
	private MyBatisCommonDao myBatisCommonDao;
	private JpaGoodsService jpaGoodsService;
	private JpaCommonService jpaCommonService;

	@Autowired
	public GoodsController(MyBatisGoodsDao goodsRepository, MyBatisCommonDao myBatisCommonDao, JpaGoodsService jpaGoodsService, JpaCommonService jpaCommonService) {
		this.goodsRepository = goodsRepository;
		this.myBatisCommonDao = myBatisCommonDao;
		this.jpaGoodsService = jpaGoodsService;
		this.jpaCommonService = jpaCommonService;
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
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}
		
		return ResponseEntity.ok(res);
	}
	
	@RequestMapping(path = "/insert")
	public ResponseEntity insertGoodsMyBatis(@RequestBody GoodsInsertRequestData goodsInsertRequestData) {
		logger.debug("insert goods");
		
		HashMap<String, Object> arr = new HashMap<String, Object>();
		arr.put("seqName", "seq_ITASRT");
		HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
		System.out.println("x1 = " + x1.get("nextval"));
		
		goodsInsertRequestData.setAssortId(StringUtils.leftPad(Long.toString((long)x1.get("nextval")), 9, '0'));
		Boolean b = goodsRepository.insertGoods(goodsInsertRequestData);
		
		ApiResponseMessage res = null;
		
		return null;
	}
	
	@PostMapping(path = "/savebyjpa")
	public ResponseEntity saveGoodsJpa(@RequestBody GoodsInsertRequestData goodsInsertRequestData) {
		logger.debug("save(insert or update) goods by jpa");
		System.out.println(goodsInsertRequestData.toString());

		goodsInsertRequestData.setAssortId(jpaCommonService.getNumberId(goodsInsertRequestData.getAssortId(), StringFactory.getSeqItasrtStr(), StringFactory.getIntNine())); // assort id 梨꾨쾲
	
		System.out.println(goodsInsertRequestData.toString());
		GoodsInsertResponseData responseData = jpaGoodsService.sequenceInsertOrUpdateGoods(goodsInsertRequestData);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);

		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/{assortId}")
	public ResponseEntity getGoodsDetailJpa(@PathVariable("assortId") String assortId){
		logger.debug("get goods detail page");
		logger.debug(assortId);
		

		GoodsSelectDetailResponseData responseData = jpaGoodsService.getGoodsDetailPage(assortId);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	// jpa로 get list
	@GetMapping(path="/getgoodslistjpa")
	public ResponseEntity getGoodsListJpa(@RequestParam String shortageYn, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date regDtBegin, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam Date regDtEnd){
		logger.debug("get goods list data");
		List<GoodsSelectListResponseData.Goods> responseData = jpaGoodsService.getGoodsList(shortageYn, regDtBegin, regDtEnd).getGoodsList();
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping(path="/getgoodslistmybatis")
	public ResponseEntity getGoodsList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date regDtBegin,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date regDtEnd,
			@RequestParam String shortageYn) {
		logger.debug("get goods list data");

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("shortageYn", shortageYn);
		param.put("regDtBegin", regDtBegin);
		param.put("regDtEnd", regDtEnd);

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsList(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	// table 초기화용
	@RequestMapping(path = "/inittables")
	public void initTables(){
		jpaGoodsService.initTables();
	}
}
