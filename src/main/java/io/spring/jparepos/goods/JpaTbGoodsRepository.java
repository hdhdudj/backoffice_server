package io.spring.jparepos.goods;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.goods.entity.TbGoods;

public interface JpaTbGoodsRepository extends JpaRepository<TbGoods, String> {
	@Query("select i from TbGoods i left join fetch i.tbGoodsOptionValueList v where i.assortId=?1")
	TbGoods findByAssortId(String assortId);

	@Query("select t from TbGoods t " + "left join fetch t.itcatg c " + "left join fetch t.itbrnd b " + "where t.regDt "
			+ "between COALESCE(:start, :oldDay) and COALESCE(:end, :doomsDay) "
			+ "and (:shortageYn is null or trim(:shortageYn)='' or t.shortageYn = :shortageYn) "
			+ "and (:assortId is null or trim(:assortId)='' or t.assortId = :assortId) "
			+ "and (:assortNm is null or trim(:assortNm)='' or t.assortNm like concat('%',:assortNm,'%'))")
	List<TbGoods> findMasterList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("shortageYn") String shortageYn, @Param("assortId") String assortId,
			@Param("assortNm") String assortNm, @Param("oldDay") LocalDateTime oldDay,
			@Param("doomsDay") LocalDateTime doomsDay);
}
