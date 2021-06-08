package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.common.entity.Testenum2;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/common")
@RequiredArgsConstructor
public class CommonController {
	private final MyBatisCommonService myBatisCommonService;
	private final JpaCommonService jpaCommonService;

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
