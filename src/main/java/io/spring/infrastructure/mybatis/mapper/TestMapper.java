package io.spring.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.spring.core.user.Test;

@Mapper
public interface TestMapper {
	void insert(@Param("test") Test test);

	Test findById(@Param("id") int i);

	void update(@Param("test") Test test);

	List<Test> findTests();

}
