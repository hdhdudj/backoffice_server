package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaItasrtRepository extends JpaRepository<Itasrt, String>{
    @Query("select i from Itasrt i left join fetch i.itvariList v where i.assortId=?1")
    Itasrt findByAssortId(String assortId);

    @Query("select t from Itasrt t " +
            "left join fetch t.itcatg c " +
            "where t.regDt " +
            "between COALESCE(:start, :oldDay) and COALESCE(:end, :doomsDay) " +
            "and (:shortageYn is null or trim(:shortageYn)='' or t.shortageYn = :shortageYn) " +
            "and (:assortId is null or trim(:assortId)='' or t.assortId = :assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or t.assortNm like concat('%',:assortNm,'%'))")
    List<Itasrt> findMasterList(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                @Param("shortageYn") String shortageYn,
                                @Param("assortId") String assortId,
                                @Param("assortNm") String assortNm,
                                @Param("oldDay") LocalDateTime oldDay,
                                @Param("doomsDay") LocalDateTime doomsDay
                                );
}
