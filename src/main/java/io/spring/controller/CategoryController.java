package io.spring.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.category.response.CategoryListResponseData;
import io.spring.model.category.response.CategorySelectOneResponseData;
import io.spring.service.category.JpaCategoryService;
import io.spring.service.common.MyBatisCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value="/category")
@RequiredArgsConstructor
public class CategoryController {
    private final JpaCategoryService jpaCategoryService;
    private final MyBatisCommonService myBatisCommonService;

    @GetMapping(path="/getcatbycatid")
    public ResponseEntity getCategoryDateByCategoryId(@RequestParam String categoryId){
        CategorySelectOneResponseData categorySelectOneResponseData = jpaCategoryService.getCategoryDataByCategoryId(categoryId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), categorySelectOneResponseData);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path="/getcatbyupcatid")
    public ResponseEntity getCategoryDataByUpCategoryId(@RequestParam String upCategoryId){
        List<CategorySelectOneResponseData> categorySelectOneResponseDataList = jpaCategoryService.getCategoryDataByUpCategoryId(upCategoryId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), categorySelectOneResponseDataList);
        return ResponseEntity.ok(res);
    }

	@GetMapping(path = "/full_categories")
	public ResponseEntity getFullCategoryData() {

		CategoryListResponseData ret = jpaCategoryService.getFullCategoryData();

//		List<CategorySelectOneResponseData> categorySelectOneResponseDataList = jpaCategoryService
//				.getCategoryDataByUpCategoryId(upCategoryId);
		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				ret);
		return ResponseEntity.ok(res);
	}
	
	@GetMapping(path = "/up_categories")
	public ResponseEntity getUpCategoryData(@RequestParam String categoryId) {

		
		
		
	//	LinkedList<String> ret = jpaCategoryService.findUpperCategory(categoryId);
		
		LinkedList<String> ret = myBatisCommonService.findUpperCategory(categoryId);

//		List<CategorySelectOneResponseData> categorySelectOneResponseDataList = jpaCategoryService
//				.getCategoryDataByUpCategoryId(upCategoryId);
		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(),
				ret);
		return ResponseEntity.ok(res);
	}

    @GetMapping(path = "/category/trees")
    public ResponseEntity getCateTrees(){
        Map<String, Object> map = jpaCategoryService.getCateTrees();
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), map);
        return ResponseEntity.ok(res);
    }
}
