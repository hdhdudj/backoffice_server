package io.spring.controller;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GoodsGetDetailResponseData;
import io.spring.model.goods.response.GoodsInsertResponseData;
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
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "����Ÿ ����", null);
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
	
	@PostMapping(path = "/insertpost")
	public ResponseEntity insertGoodsJpa(@RequestBody GoodsInsertRequestData goodsInsertRequestData) {
		logger.debug("insert goods by jpa");

		goodsInsertRequestData.setAssortId(jpaCommonService.getAssortId(goodsInsertRequestData)); // assort id 채번
		GoodsInsertResponseData responseData = jpaGoodsService.sequenceInsertOrUpdateGoods(goodsInsertRequestData);

		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);

		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/getgoodsdetail")
	public ResponseEntity getGoodsJpaByGet(@RequestParam(required = true) String assortId){
		logger.debug("get goods detail page");

		GoodsGetDetailResponseData responseData = jpaGoodsService.getGoodsDetailPage(assortId);

		ApiResponseMessage res = new ApiResponseMessage("ok","success", responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

//	 jpa로 get list
//	@GetMapping(path="/getgoodslist")
//	public ResponseEntity getGoodsList(@RequestParam String shortageYn, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date regDtBegin, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam Date regDtEnd){
//		logger.debug("get goods list data");
//		GoodsInsertResponseData responseData = jpaGoodsService.getGoodsList(shortageYn, regDtBegin, regDtEnd);
//		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
//		if(responseData == null){
//			return null;
//		}
//		return ResponseEntity.ok(res);
//	}

	@GetMapping(path="/getgoodslistmybatis")
	public ResponseEntity getGoodsList(@RequestParam String shortageYn, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date regDtBegin, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam Date regDtEnd){
		logger.debug("get goods list data");
		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsList(shortageYn, regDtBegin, regDtEnd);
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
