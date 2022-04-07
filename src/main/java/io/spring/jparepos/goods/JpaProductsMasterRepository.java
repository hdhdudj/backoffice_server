package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.goods.entity.ProductsMaster;

public interface JpaProductsMasterRepository extends JpaRepository<ProductsMaster, Long> {
	@Query("select t from ProductsMaster t " + "where 1=1 "
			+ "and (:masterNm is null or trim(:masterNm)='' or t.masterNm  like concat('%',:masterNm,'%')) ")
	List<ProductsMaster> findList(@Param("masterNm") String masterNm);
}
