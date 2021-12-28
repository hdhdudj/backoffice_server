package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.idclass.LsshpdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaLsshpdRepository extends JpaRepository<Lsshpd, LsshpdId> {
    @Query("select max(d.shipSeq) from Lsshpd d where d.shipId=?1")
    String findMaxSeq(String shipId);

    Lsshpd findByShipIdAndShipSeq(String shipId, String shipSeq);

    List<Lsshpd> findByShipId(String shipId);

    @Query("select lsshpd from Lsshpd lsshpd " +
            "join fetch lsshpd.tbOrderDetail tod " +
            "join fetch tod.tbOrderMaster tom " +
            "join fetch tom.tbMemberAddress tma " +
            "join fetch tod.ititmm itm " +
            "join fetch itm.itasrt ita " +
            "join fetch itm.itvari1 iv1 " +
            "left outer join fetch itm.itvari2 iv2 " +
            "left outer join fetch itm.itvari3 iv3 " +
            "where lsshpd.excAppDt between :start and :end " +
            "and (:shipId is null or trim(:shipId)= '' or lsshpd.shipId = :shipId) " +
            "and (:shipSeq is null or trim(:shipSeq)= '' or lsshpd.shipSeq = :shipSeq) " +
            "and (:assortId is null or trim(:assortId)= '' or ita.assortId = :assortId) " +
            "and (:assortNm is null or trim(:assortNm)= '' or ita.assortNm like concat('%',:assortNm,'%')) " +
            "and (:vendorId is null or trim(:vendorId)= '' or ita.vendorId=:vendorId) " +
            "and tod.statusCd=:statusCd")
    List<Lsshpd> findShipList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                              @Param("shipId") String shipId, @Param("shipSeq") String shipSeq, @Param("assortId") String assortId,
                              @Param("assortNm") String assortNm, @Param("vendorId") String vendorId, @Param("statusCd") String statusCd);
}
