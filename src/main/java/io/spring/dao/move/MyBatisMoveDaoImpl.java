package io.spring.dao.move;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.MoveMapper;

@Repository
public class MyBatisMoveDaoImpl implements MyBatisMoveDao {
	private final MoveMapper moveMapper;
	
	@Autowired
	public MyBatisMoveDaoImpl(MoveMapper moveMapper) {
		this.moveMapper = moveMapper;
	}
	
	

	@Override
	public List<HashMap<String, Object>> getOrderMoveList(HashMap<String, Object> param) {
		return moveMapper.getOrderMoveList(param);
	};

}
