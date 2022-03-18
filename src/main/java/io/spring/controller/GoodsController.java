package io.spring.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import io.spring.infrastructure.util.Utilities;
import io.spring.model.goods.request.GoodsInsertRequestData;
import io.spring.model.goods.response.GetStockListResponseData;
import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import io.spring.model.goods.response.GoodsSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import io.spring.service.goods.JpaGoodsService;
import io.spring.service.goods.MyBatisGoodsService;
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

	private final MyBatisGoodsService myBatisGoodsService;



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
	
//	@RequestMapping(path = "/insert")
//	public ResponseEntity insertGoodsMyBatis(@RequestBody GoodsInsertRequestData goodsInsertRequestData) {
//		log.debug("insert goods");
//
//		HashMap<String, Object> arr = new HashMap<String, Object>();
//		arr.put("seqName", "seq_ITASRT");
//		HashMap<String, Object> x1 = myBatisCommonDao.getSequence(arr);
//		System.out.println("x1 = " + x1.get("nextval"));
//
//		goodsInsertRequestData.setAssortId(StringUtils.leftPad(Long.toString((long)x1.get("nextval")), 9, '0'));
//		Boolean b = goodsRepository.insertGoods(goodsInsertRequestData);
//
//		ApiResponseMessage res = null;
//
//		return null;
//	}

	/**
	 * 상품 등록 및 수정
	 */
	@PostMapping(path = "/save")
	public ResponseEntity saveGoodsJpa(@RequestBody @Valid GoodsInsertRequestData goodsInsertRequestData) {
		log.debug("save(insert or update) goods by jpa");
		System.out.println(goodsInsertRequestData.toString());

		goodsInsertRequestData.setAssortId(jpaCommonService.getNumberId(goodsInsertRequestData.getAssortId(), StringFactory.getStrSeqItasrt(), StringFactory.getIntNine())); // assort id 梨꾨쾲

		System.out.println(goodsInsertRequestData.toString());
		String assortId = jpaGoodsService.sequenceInsertOrUpdateGoods(goodsInsertRequestData);
//		GoodsSelectDetailResponseData responseData = jpaGoodsService.getGoodsDetailPage(goodsInsertRequestData.getAssortId());
		Map<String, String> responseMap = new HashMap<>();
		responseMap.put(StringFactory.getStrAssortId(), assortId);
		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseMap);

//		if(responseData == null){
//			return null;
//		}
		return ResponseEntity.ok(res);
	}

	/**
	 * 상품 상세 내용
	 */
	@GetMapping(path = "/{assortId}")
	public ResponseEntity getGoodsDetailJpa(@PathVariable("assortId") String assortId){
		log.debug("get goods detail page");
		log.debug(assortId);
		
		GoodsSelectDetailResponseData responseData = jpaGoodsService.getGoodsDetailPage(assortId);
		LinkedList<String> categories = myBatisCommonService.findUpperCategory(responseData.getDispCategoryId());

		responseData.setCategoryValue(categories == null? new LinkedList<>() : categories);

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), responseData);
		if(responseData == null){
			return null;
		}
		return ResponseEntity.ok(res);
	}

	/**
	 * 마스터 기준으로 상품 목록을 가져옴
	 */
	@GetMapping(path="/items/master")
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
//	@GetMapping(path = "/goods-item")
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
//	@GetMapping(path = "/goods-item-fullcategory")

	/**
	 * itmeId 기준으로 상품 목록을 가져옴
	*/
	@GetMapping(path = "/items/detail")
	public ResponseEntity getGoodsItemWithCategory(@RequestParam(required = false) String assortId,
			@RequestParam(required = false) String assortNm, @RequestParam(required = false) String vendorId,
			@RequestParam(required = false) String brandId, @RequestParam(required = false) String category,
			@RequestParam(required = false) String channelGoodsNo) {
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

		if (channelGoodsNo != null) {
			param.put("channelGoodsNo", channelGoodsNo);
		}

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsItemListWithCategory(param);
		for(HashMap<String, Object> map : responseData){
			Utilities.changeNullToEmpty(map);
		}
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/stock/items")
	public ResponseEntity getGoodsList(@RequestParam @Nullable String storageId,
			@RequestParam @Nullable String assortId) {

		HashMap<String, Object> map = new HashMap<>();

		if (storageId != null && !storageId.equals("")) {
			map.put("storageId", storageId);
		}
		if (assortId != null && !assortId.equals("")) {
			map.put("assortId", assortId);
		}

		List<HashMap<String, Object>> responseData = goodsRepository.getGoodsStockList(map);
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", responseData);
		if (responseData == null) {
			return null;
		}
		return ResponseEntity.ok(res);

	}

	// @PathVariable("assortId") String assortId
	@GetMapping(path = "/stock/storage/{storageId}")
	public ResponseEntity getStockList(@PathVariable("storageId") String storageId,
			@RequestParam @Nullable String vendorId, @RequestParam @Nullable String assortId,
			@RequestParam @Nullable String assortNm, @RequestParam @Nullable String channelGoodsNo) {

		HashMap<String, Object> map = new HashMap<>();

		if (storageId != null && !storageId.equals("")) {
			map.put("storageId", storageId);
		}
		if (assortId != null && !assortId.equals("")) {
			map.put("assortId", assortId);
		}

		if (vendorId != null && !vendorId.equals("")) {
			map.put("vendorId", vendorId);
		}

		if (assortNm != null && !assortNm.equals("")) {
			map.put("assortNm", assortNm);
		}

		if (channelGoodsNo != null && !channelGoodsNo.equals("")) {
			map.put("channelGoodsNo", channelGoodsNo);
		}

		GetStockListResponseData r = myBatisGoodsService.getItitmc(map);

		// List<HashMap<String, Object>> responseData = goodsRepository.getItitmc(map);

		// GetStockListResponseData r = jpaGoodsService.getStockList(storageId,
		// vendorId, assortId, assortNm,
		// channelGoodsNo);
		//


		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), r);
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

	@GetMapping(path="/batch-size-test")
	public ResponseEntity batchSizeTest() {
		log.debug("/goods/batch-size-test");

		jpaGoodsService.batchSizeTest();
		ApiResponseMessage res = new ApiResponseMessage("ok", "success", null);

		return ResponseEntity.ok(res);
	}
}
