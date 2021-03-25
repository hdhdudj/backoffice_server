package io.spring.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.core.user.Test;
import io.spring.core.user.TestRepository;
import io.spring.infrastructure.mybatis.mapper.TestMapper;

@Repository
public class MyBatisTestRepository implements TestRepository {
	private final TestMapper testMapper;

	@Autowired
	public MyBatisTestRepository(TestMapper testMapper) {
		this.testMapper = testMapper;
	}

	@Override
	public void save(Test test) {
		if (testMapper.findById(test.getId()) == null) {
			testMapper.insert(test);
		} else {
			testMapper.update(test);
		}
	}

	@Override
	public Optional<Test> findById(int id) {
		return Optional.ofNullable(testMapper.findById(id));
	}

	@Override
	public List<Test> findTests() {
		// TODO Auto-generated method stub
		return testMapper.findTests();
	}

}
