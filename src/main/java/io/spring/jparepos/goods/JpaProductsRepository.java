package io.spring.jparepos.goods;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.goods.entity.Products;

public interface JpaProductsRepository extends JpaRepository<Products, Long> {


	@Query("select t from Products t " + "left join fetch t.itbrnd b "
			+ "left join fetch t.productsMaster m " + "where t.regDt " + "between :start and :end "
			+ "and (:saleYn is null or trim(:saleYn)='' or t.saleYn = :saleYn) "
			+ "and (:displayYn is null or trim(:displayYn)='' or t.displayYn = :displayYn) "
			+ "and (:masterId is null or m.masterId = :masterId) "
			+ "and (:masterNm is null or trim(:masterNm)='' or m.masterNm  like concat('%',:masterNm,'%')) "
			+ "and (:productId is null or t.productId = :productId) "
			+ "and (:productNm is null or trim(:productNm)='' or t.productNm like concat('%',:productNm,'%'))")
	List<Products> findList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("saleYn") String saleYn, @Param("displayYn") String displayYn,
			@Param("productId") Long productId, @Param("productNm") String productNm, @Param("masterId") Long masterId,
			@Param("masterNm") String masterNm);

}
