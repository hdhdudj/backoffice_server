package io.spring.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CategoryTree {
	private String label;
	private String value;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CategoryTree> children;

	@Override
	public String toString() {
		return "CategoryTree [label=" + label + ", value=" + value + ", children=" + children + "]";
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<CategoryTree> getChildren() {
		return children;
	}

	public void setChildren(List<CategoryTree> children) {
		this.children = children;
	}

	public CategoryTree() {
		super();
		// TODO Auto-generated constructor stub
	}


}
