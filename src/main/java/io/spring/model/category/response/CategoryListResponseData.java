package io.spring.model.category.response;

import java.util.List;

import io.spring.model.CategoryTree;


public class CategoryListResponseData {
	private List<CategoryTree> CategoryList;

	public CategoryListResponseData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<CategoryTree> getCategoryList() {
		return CategoryList;
	}

	public void setCategoryList(List<CategoryTree> categoryList) {
		CategoryList = categoryList;
	}

	@Override
	public String toString() {
		return "CategoryListResponseData [CategoryList=" + CategoryList + "]";
	}

}
