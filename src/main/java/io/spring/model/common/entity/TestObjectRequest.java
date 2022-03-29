package io.spring.model.common.entity;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestObjectRequest {

	private List<Item> items;

	@Getter
	@Setter
	public static class Item {
		private String label;
		private Object value;
	}

}

