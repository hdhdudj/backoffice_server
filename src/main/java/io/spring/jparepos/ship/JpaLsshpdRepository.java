package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.idclass.LsshpdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            "join fetch tod.itasrt ita " +
            "join fetch tod.ititmm itm " +
            "join fetch itm.itvari1 iv1 " +
            "left outer join fetch itm.itvari2 iv2 " +
            "left outer join fetch itm.itvari3 iv3 " +
            "where lsshpd.excAppDt between ?1 and ?2 " +
            "and (?3 is null or trim(?3)= '' or lsshpd.shipId = ?3) " +
            "and (?4 is null or trim(?4)= '' or ita.assortId = ?4) " +
            "and (?5 is null or trim(?5)= '' or ita.assortNm like concat('%',?5,'%')) " +
            "and (?6 is null or trim(?6)= '' or ita.vendorId=?6) " +
            "and tod.statusCd=?7")
    List<Lsshpd> findShipList(LocalDateTime start, LocalDateTime end, String shipId, String assortId, String assortNm, String vendorId, String statusCd);
}
