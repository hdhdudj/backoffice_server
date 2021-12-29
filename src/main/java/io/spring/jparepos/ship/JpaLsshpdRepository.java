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

    /**
     * 국내입고처리 - 발주선택창 조회 쿼리
     */
    @Query("select distinct(lsd) from Lsshpd lsd " +
            "join fetch lsd.lsshpm lsm " +
            "join fetch lsd.lspchd ld " +
            "join fetch ld.lspchm lm " +
            "join fetch ld.lspchb lb " +
            "left outer join fetch ld.tbOrderDetail tod " +
            "left outer join fetch tod.tbOrderMaster tom " +
            "left outer join fetch tom.tbMember tm " +
            "left outer join fetch tom.tbMemberAddress tma " +
            "left outer join fetch ld.ititmm im " +
            "left outer join fetch im.itvari1 iv1 " +
            "left outer join fetch im.itvari2 iv2 " +
            "left outer join fetch im.itvari3 iv3 " +
            "join fetch im.itasrt ita " +
            "left outer join fetch ita.ifBrand ib " +
            "where lm.purchaseDt between :start and :end " +
            "and (:vendorId is null or trim(:vendorId)='' or lm.vendorId=:vendorId) "
            + "and (:storeCd is null or trim(:storeCd)='' or lm.storeCd=:storeCd) "
            + "and (:blNo is null or trim(:blNo)='' or lsm.blNo=:blNo) "
            + "and lm.purchaseStatus in :statusArr")
    List<Lsshpd> findPurchaseList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                  @Param("vendorId") String vendorId, @Param("storeCd") String storeCd,
                                  @Param("blNo") String blNo, @Param("statusArr") List<String> statusArr);
}
