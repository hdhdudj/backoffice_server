package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.idclass.LsshpdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaLsshpdRepository extends JpaRepository<Lsshpd, LsshpdId> {
    @Query("select max(d.shipSeq) from Lsshpd d where d.shipId=?1")
    String findMaxSeq(String shipId);

    @Query("select ld from Lsshpd ld join fetch ld.tbOrderDetail to where ld.shipId=:shipId and ld.shipSeq=:shipSeq")
    Lsshpd findByShipIdAndShipSeq(@Param("shipId") String shipId, @Param("shipSeq") String shipSeq);

    /**
     * 출고내역 가져오는 쿼리
     */
    @Query("select distinct(lsshpd) from Lsshpd lsshpd " +
            "join fetch lsshpd.tbOrderDetail tod " +
            "join fetch lsshpd.lsshpm lsm " +
            "join fetch tod.tbOrderMaster tom " +
            "join fetch tom.tbMemberAddress tma " +
            "join fetch tod.ititmm itm " +
            "join fetch itm.itasrt ita " +
            "join fetch itm.itvari1 iv1 " +
            "left outer join fetch itm.itvari2 iv2 " +
            "left outer join fetch itm.itvari3 iv3 " +
            "where lsshpd.shipId = :shipId")
    List<Lsshpd> findShipListByShipId(@Param("shipId") String shipId);

    /**
     * 출고처리 화면 조회 리스트
     */
    @Query("select lsd from Lsshpd lsd " +
            "join fetch lsd.lsshpm lsm " +
            "join fetch lsd.tbOrderDetail td " +
            "join fetch td.ititmm im "+
            "join fetch im.itasrt it "+
            "where lsm.instructDt between :start and :end " +
            "and (:assortId is null or trim(:assortId)='' or td.assortId=:assortId) " +
            "and (:shipId is null or trim(:shipId)='' or lsd.shipId=:shipId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%', :assortNm, '%')) " +
            "and (:vendorId is null or trim(:vendorId)='' or it.vendorId=:vendorId)" +
            "and lsm.shipStatus=:shipStatus " +
            "and (:orderId is null or trim(:orderId)='' or lsd.orderId=:orderId) " +
            "and (:orderSeq is null or trim(:orderSeq)='' or lsd.orderSeq=:orderSeq)")
    List<Lsshpd> findShipIndicateList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                      @Param("assortId") String assortId,
                                      @Param("shipId") String shipId,
                                      @Param("assortNm") String assortNm,
                                      @Param("vendorId") String vendorId,
                                      @Param("shipStatus") String shipStatus,
                                      @Param("orderId") String orderId,
                                      @Param("orderSeq") String orderSeq
                                      );

    /**
     * 출고리스트 가져오는 쿼리
     */
    @Query("select distinct(lsshpd) from Lsshpd lsshpd " +
            "join fetch lsshpd.tbOrderDetail tod " +
            "join fetch lsshpd.lsshpm lsm " +
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
            "and tod.statusCd=:statusCd " +
            "and lsshpd.shipGb='01' and lsm.shipStatus='04'")
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

    /**
     * 이동지시리스트, 이동리스트
     */
    @Query("select distinct ld from Lsshpd ld " +
            "join fetch ld.lsshpm lm " +
            "left join fetch ld.tbOrderDetail td " +
            "join fetch ld.itasrt it " +
            "left join fetch it.ifBrand ib " +
            "left join fetch it.itvariList iv " +
            "where lm.receiptDt between :start and :end " +
            "and lm.shipStatus=:shipStatus " +
            "and (:shipId is null or trim(:shipId)='' or ld.shipId=:shipId) " +
            "and (:assortId is null or trim(:assortId)='' or ld.assortId=:assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%',:assortNm,'%')) " +
            "and (:storageId is null or trim(:storageId)='' or lm.oStorageId=:storageId) " +
            "and (:deliMethod is null or trim(:deliMethod)='' or lm.delMethod=:deliMethod)")
    List<Lsshpd> findLsshpdMoveList(@Param("start")LocalDateTime start,
                                    @Param("end")LocalDateTime end,
                                    @Param("shipId")String shipId,
                                    @Param("assortId")String assortId,
                                    @Param("assortNm")String assortNm,
                                    @Param("storageId")String storageId,
                                    @Param("deliMethod")String deliMethod,
                                    @Param("shipStatus")String shipStatus);

    List<Lsshpd> findByShipId(String shipId);

    @Query("select lsshpd from Lsshpd lsshpd " +
            "join fetch lsshpd.lsshpm lm " +
            "where lsshpd.shipId in :shipIdList")
    List<Lsshpd> findShipDetailListByShipIdList(@Param("shipIdList") List<String> shipIdList);

    @Query("select lsd from Lsshpd lsd " +
            "join fetch lsd.tbOrderDetail tod " +
            "where lsd.orderId in :orderIdList and tod.assortGb=:assortGb")
    List<Lsshpd> findAddGoodsByOrderIdList(@Param("orderIdList") List<String> orderIdList,
                                           @Param("assortGb") String assortGb);
}
