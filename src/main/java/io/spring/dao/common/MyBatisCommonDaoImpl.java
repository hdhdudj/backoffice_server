package io.spring.dao.common;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.CommonMapper;

@Repository
public class MyBatisCommonDaoImpl implements MyBatisCommonDao {
	private final CommonMapper commonMapper;

	@Autowired
	public MyBatisCommonDaoImpl(CommonMapper commonMapper) {
		this.commonMapper = commonMapper;
	}

	@Override
	public HashMap<String, Object> getSequence(HashMap<String, Object> param) {
		HashMap<String, Object> r = commonMapper.getSequence(param);
		System.out.println("aaacccc33444");
		return r;
	}

}
