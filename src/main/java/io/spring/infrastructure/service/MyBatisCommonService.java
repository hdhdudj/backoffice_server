package io.spring.infrastructure.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.core.common.CommonRepository;
import io.spring.infrastructure.mybatis.mapper.CommonMapper;

@Repository
public class MyBatisCommonService implements CommonRepository {
	private final CommonMapper commonMapper;

	@Autowired
	public MyBatisCommonService(CommonMapper commonMapper) {
		this.commonMapper = commonMapper;
	}

	@Override
	public HashMap<String, Object> getSequence(HashMap<String, Object> param) {
		HashMap<String, Object> r = commonMapper.getSequence(param);
		System.out.println("aaacccc33444");
		return r;
	}

}
