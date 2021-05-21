package io.spring.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.dao.common.MyBatisCommonDao;
import io.spring.dao.goods.MyBatisGoodsDao;
import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.common.entity.Testenum2;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import io.spring.service.goods.JpaGoodsService;

@RestController
@RequestMapping(value = "/common")
public class CommonController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private MyBatisCommonService myBatisCommonService;
	private JpaCommonService jpaCommonService;

	

	@Autowired
	public CommonController(MyBatisCommonService myBatisCommonService,JpaCommonService jpaCommonService) {
		this.myBatisCommonService = myBatisCommonService;
		this.jpaCommonService = jpaCommonService;
	}
	@GetMapping(path="/brand_search")
		public ResponseEntity selectBrandSearchList(@RequestParam(required = false) String codeId,@RequestParam(required = false) String codeNm) {
	//public ResponseEntity selectBrandSearchList() {
		
		
		System.out.println(codeId);
		System.out.println(codeNm);
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		if(codeId==null) {
			codeId="%";
		}
		if(codeNm==null) {
			codeNm="%";
		}		
		
		
		param.put("codeId", codeId);
		param.put("codeNm", codeNm);
		
		List<HashMap<String, Object>> r = myBatisCommonService.getBrandSearchList(param);
		
		ApiResponseMessage res = null;
		
		if(r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS","", r);
		}
		else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}
		
		return ResponseEntity.ok(res);
	}	
	
	@PostMapping(path="/save_testenum2")
	public ResponseEntity saveTestenum2() {
		
		Testenum2 a = new Testenum2();
		System.out.println(a);
		
		//a.setAssortGb("03");
		jpaCommonService.saveTestEnum2(a);
		
		System.out.println(a);
		ApiResponseMessage res = null;
		
		if(a != null) {
			res = new ApiResponseMessage<Testenum2>("SUCCESS","", a);
		}
		else {
			res = new ApiResponseMessage<Testenum2>("ERROR", "ERROR", null);
		}
		
		return ResponseEntity.ok(res);
		
	}
		
	
}
