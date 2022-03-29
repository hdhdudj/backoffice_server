package io.spring.dao.user;

import lombok.Data;

@Data
public class Test {
	private int id;
	private String test1;
	private String test2;
	private String test3;
	private String test4;

	public Test(int id, String test1, String test2, String test3, String test4) {
		super();
		this.id = id;
		this.test1 = test1;
		this.test2 = test2;
		this.test3 = test3;
		this.test4 = test4;
	}

	public Test(String test1, String test2, String test3, String test4) {
		super();

		this.test1 = test1;
		this.test2 = test2;
		this.test3 = test3;
		this.test4 = test4;
	}

}
