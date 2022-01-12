package io.spring.service.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import io.spring.jparepos.category.JpaItcatgRepository;
import io.spring.model.CategoryTree;
import io.spring.model.category.response.CategoryListResponseData;
import io.spring.model.category.response.CategorySelectOneResponseData;
import io.spring.model.goods.entity.Itcatg;
import lombok.RequiredArgsConstructor;

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

	public CategoryListResponseData getFullCategoryData() {
		CategoryListResponseData ret = new CategoryListResponseData();

		List<Itcatg> itcatgListAll = jpaItcatgRepository.findAll();

		List<HashMap<String, Object>> l = new ArrayList();

		for (Itcatg io : itcatgListAll) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("category_id", io.getCategoryId());
			m.put("category_nm", io.getCategoryNm());
			m.put("up_category_id", io.getUpCategoryId());
			m.put("is_bottom_yn", io.getIsBottomYn());
			l.add(m);
		}

		List<Itcatg> itcatgList = jpaItcatgRepository.findByUpCategoryId("A00000000");

		List<CategoryTree> child = new ArrayList();

		for (Itcatg o : itcatgList) {
			CategoryTree c = findSubCategoryList(l, o.getCategoryId());

			child.add(c);

		}

		ret.setCategoryList(child);
		return ret;

	}

	private CategoryTree findSubCategoryList(List<HashMap<String, Object>> list, String rootCategoryId) {

		List<HashMap<String, Object>> list1 = list;

		// System.out.println(rootCategoryId);

		HashMap<String, Object> map = list.stream().filter(o -> o.get("category_id").toString().equals(rootCategoryId))
				.findFirst().orElseGet(() -> null);
		;

		if (map == null) {
			return null;
		}

		// System.out.println(map);

		String cat = map.get("category_id").toString();
		String catNm = map.get("category_nm").toString();

		CategoryTree r = new CategoryTree();

		r.setValue(cat);
		r.setLabel(catNm);
		List<CategoryTree> child = new ArrayList();

		if (map.get("is_bottom_yn").equals("02")) {

			Stream<HashMap<String, Object>> s = list.stream()
					.filter(o -> o.get("up_category_id").toString().equals(map.get("category_id").toString()));

			s.forEach(o1 -> {
				// System.out.println(o1.get("category_nm"));
				CategoryTree c = findSubCategoryList(list1, o1.get("category_id").toString());
				child.add(c);
			});

		}

		if (child.size() > 0) {
			r.setChildren(child);
		}

		return r;

	}
	
	public LinkedList<String> findUpperCategory(String categoryId) {
		LinkedList<String> a =new LinkedList<String>();
		
		Itcatg itcatg = jpaItcatgRepository.findById(categoryId).orElseGet(() -> null);
		
		if (itcatg == null) {
			return null;
		}

		a.addFirst(itcatg.getCategoryId());
		
		String upCatId="";
		String catId="";
		
		upCatId = itcatg.getUpCategoryId();
		catId = upCatId;
		
		while (upCatId != "A00000000")  {
			// System.out.println(catId);
			Itcatg itcatgT = jpaItcatgRepository.findById(catId).orElseGet(() -> null);
			if(!itcatgT.getUpCategoryId().equals("A00000000")) {
				a.addFirst(itcatgT.getCategoryId()); 
			}
			upCatId = itcatgT.getUpCategoryId();
			catId = upCatId;
	 
	    }
		return a;
	}

	private CategoryTree findSubCategory(String rootCategoryId) {
		// System.out.println(rootCategoryId);


		Itcatg itcatg = jpaItcatgRepository.findById(rootCategoryId).orElseGet(() -> null);

//		System.out.println(itcatg);

		if (itcatg == null) {
			return null;
		}


		String cat = itcatg.getCategoryId();
		String catNm = itcatg.getCategoryNm();

		CategoryTree r = new CategoryTree();

		r.setValue(cat);
		r.setLabel(catNm);
		List<CategoryTree> child = new ArrayList();

		if (itcatg.getIsBottomYn().equals("02")) {
			List<Itcatg> itcatgList = jpaItcatgRepository.findByUpCategoryId(rootCategoryId);
			for (Itcatg o : itcatgList) {
				// System.out.println(o.getCategoryId());
				CategoryTree c = findSubCategory(o.getCategoryId());
				child.add(c);
			}

		}


		if (child.size() > 0) {
			r.setChildren(child);
		}

		return r;

	}


}

