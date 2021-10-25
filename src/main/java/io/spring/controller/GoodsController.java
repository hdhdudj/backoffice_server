package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GoodsInsertResponseData;
import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import io.spring.model.goods.response.GoodsSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import io.spring.service.goods.JpaGoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/goods")
@RequiredArgsConstructor
public class GoodsController {
	private final MyBatisGoodsDao goodsRepository;
	private final MyBatisCommonDao myBatisCommonDao;
	private final JpaGoodsService jpaGoodsService;
	private final JpaCommonService jpaCommonService;
	private final MyBatisCommonService myBatisCommonService;

	@RequestMapping(path = "/select")
	public ResponseEntity selectGoodsListAll() {
		log.debug("select goods");
		
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
		log.debug("insert goods");
		
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
		log.debug("save(insert or update) goods by jpa");
		System.out.println(goodsInsertRequestData.toString());

		goodsInsertRequestData.setAssortId(jpaCommonService.getNumberId(goodsInsertRequestData.getAssortId(), StringFactory.getStrSeqItasrt(), StringFactory.getIntNine())); // assort id 梨꾨쾲
	
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
		log.debug("get goods detail page");
		log.debug(assortId);
		
		GoodsSelectDetailResponseData responseData = jpaGoodsService.getGoodsDetailPage(assortId);
		LinkedList<String> categories = myBatisCommonService.findUpperCategory(responseData.getDispCategoryId());

		responseData.setCategoryValue(categories);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	// jpa로 get list
	@GetMapping(path="/getgoodslistjpa")
	public ResponseEntity getGoodsListJpa(@RequestParam @Nullable String shortageYn,
										  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate regDtBegin,
										  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate regDtEnd,
										  @RequestParam @Nullable String assortId,
										  @RequestParam @Nullable String assortNm){
		log.debug("get goods list data");
		GoodsSelectListResponseData goodsSelectListResponseData = jpaGoodsService.getGoodsList(shortageYn, regDtBegin, regDtEnd, assortId, assortNm);
		List<GoodsSelectListResponseData.Goods> responseData = null;
		if(goodsSelectListResponseData != null){
			responseData = goodsSelectListResponseData.getGoodsList();
		}
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	// 상품리스트조회(ititmm)
	@GetMapping(path = "/goods-item")
	public ResponseEntity getGoodsItem(@RequestParam String shortageYn,
			@RequestParam(required = false) String assortId, @RequestParam(required = false) String assortNm,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date regDtBegin,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date regDtEnd) {
		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("shortageYn", shortageYn);
		param.put("regDtBegin", regDtBegin);
		param.put("regDtEnd", regDtEnd);
		if (assortId != null && !assortId.trim().equals("")) {
			param.put("assortId", assortId);
		}

		if (assortNm != null) {
			param.put("assortNm", assortNm);
		}

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsItemList(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

	// 상품리스트조회(ititmm)
	@GetMapping(path = "/goods-item-fullcategory")
	public ResponseEntity getGoodsItemWithCategory(@RequestParam(required = false) String assortId,
			@RequestParam(required = false) String assortNm, @RequestParam(required = false) String vendorId,
			@RequestParam(required = false) String brandId, @RequestParam(required = false) String category) {
		HashMap<String, Object> param = new HashMap<String, Object>();

		if (assortId != null) {
			param.put("assortId", assortId);
		}

		if (assortNm != null) {
			param.put("assortNm", assortNm);
		}

		if (vendorId != null) {
			param.put("vendorId", vendorId);
		}

		if (brandId != null) {
			param.put("brandId", brandId);
		}

		if (category != null) {
			param.put("category", category);
		}

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsItemListWithCategory(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping(path="/getgoodslistmybatis")
	public ResponseEntity getGoodsList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate regDtBegin,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate regDtEnd,
			@RequestParam String shortageYn, @RequestParam @Nullable String assortId, @RequestParam @Nullable String assortNm) {
		log.debug("/goods/getgoodslistmybatis");

		LocalDateTime begin = regDtBegin.atStartOfDay();
		LocalDateTime end = regDtEnd.atTime(23,59,59);

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("shortageYn", shortageYn);
		param.put("regDtBegin", begin);
		param.put("regDtEnd", end);
		param.put("assortId", assortId);
		param.put("assortNm", assortNm);

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsList(param);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}
}
