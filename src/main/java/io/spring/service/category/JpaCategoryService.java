package io.spring.service.category;

import io.spring.jparepos.category.JpaItcatgRepository;
import io.spring.model.category.response.CategorySelectOneResponseData;
import io.spring.model.goods.entity.Itcatg;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaCategoryService {
    private final JpaItcatgRepository jpaItcatgRepository;

    public List<CategorySelectOneResponseData> getCategoryDataByUpCategoryId(String upCategoryId) {
        List<Itcatg> itcatgList = jpaItcatgRepository.findByUpCategoryId(upCategoryId);
        List<CategorySelectOneResponseData> categorySelectOneResponseDataList = new ArrayList<>();
        for(Itcatg itcatg : itcatgList){
            CategorySelectOneResponseData categorySelectOneResponseData = new CategorySelectOneResponseData(itcatg);
            categorySelectOneResponseDataList.add(categorySelectOneResponseData);
        }
        return categorySelectOneResponseDataList;
    }

    public CategorySelectOneResponseData getCategoryDataByCategoryId(String categoryId) {
        Itcatg itcatg = jpaItcatgRepository.findById(categoryId).orElseGet(() -> null);
        return new CategorySelectOneResponseData(itcatg);
    }
}
