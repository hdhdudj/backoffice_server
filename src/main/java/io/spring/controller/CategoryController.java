package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.category.response.CategorySelectOneResponseData;
import io.spring.service.category.JpaCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value="/category")
@RequiredArgsConstructor
public class CategoryController {
    private final JpaCategoryService jpaCategoryService;

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
}
