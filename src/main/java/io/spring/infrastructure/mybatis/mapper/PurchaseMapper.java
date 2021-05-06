package io.spring.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface PurchaseMapper {
    List<HashMap<String, Object>> selectPurchaseListByCondition(HashMap<String, Object> param);
}
