package io.spring.dao.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TestDataList {

	@JsonProperty("tests")
	private final List<Test> testDatas;
	@JsonProperty("testsCount")
	private final int count;

	public TestDataList(List<Test> testDatas, int count) {

		this.testDatas = testDatas;
		this.count = count;
	}

}
