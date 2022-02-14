package io.spring.dao.common;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import io.spring.infrastructure.mybatis.mapper.CommonMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisCommonDaoImpl implements MyBatisCommonDao {
	private final CommonMapper commonMapper;

	@Override
	public HashMap<String, Object> getSequence(HashMap<String, Object> param) {
		HashMap<String, Object> r = commonMapper.getSequence(param);
		System.out.println("aaacccc33444");
		return r;
	}

	@Override
	public HashMap<String, Object> getCategory(HashMap<String, Object> param) {
		HashMap<String, Object> r = commonMapper.getCategory(param);
		System.out.println(r);
		// TODO Auto-generated method stub
		return r;
	}

	@Override
	public List<HashMap<String, Object>> getBrandSearchList(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getBrandSearchList(param);
	}
	
	@Override
	public List<HashMap<String, Object>> getPurchaseVendorSearchList(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getPurchaseVendorSearchList(param);
	}
	
	
	@Override
	public List<HashMap<String, Object>> getCommonStorage(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getCommonStorage(param);
	}

	@Override
	public List<HashMap<String, Object>> getCommonPurchaseVendor(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getCommonPurchaseVendor(param);
	}
	
	

	@Override
	public List<HashMap<String, Object>> getCommonOrderStatus(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getCommonOrderStatus(param);
	}

	@Override
	public HashMap<String, Object> getCommonDefaultRack(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return commonMapper.getCommonDefaultRack(param);
	}
}
