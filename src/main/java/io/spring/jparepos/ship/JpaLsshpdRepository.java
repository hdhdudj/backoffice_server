package io.spring.jparepos.ship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.idclass.LsshpdId;

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
			"join fetch td.ititmm itm " +
			"join fetch lsd.itasrt it "
			+
			"left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
			+ "left join fetch itm.itvari3 itv3 "
			+ 
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
			"join fetch lsshpd.ititmm itm "
			+
			"join fetch lsshpd.itasrt ita "
			+
            "join fetch itm.itvari1 iv1 " +
            "left outer join fetch itm.itvari2 iv2 " +
            "left outer join fetch itm.itvari3 iv3 " +
            "where lsshpd.excAppDt between :start and :end " +
            "and (:shipId is null or trim(:shipId)= '' or lsshpd.shipId = :shipId) " +
            "and (:shipSeq is null or trim(:shipSeq)= '' or lsshpd.shipSeq = :shipSeq) " +
            "and (:assortId is null or trim(:assortId)= '' or ita.assortId = :assortId) " +
            "and (:assortNm is null or trim(:assortNm)= '' or ita.assortNm like concat('%',:assortNm,'%')) " +
            "and (:vendorId is null or trim(:vendorId)= '' or ita.vendorId=:vendorId) " +
            "and (:storageId is null or trim(:storageId)= '' or lsm.storageId=:storageId) " +
            "and tod.statusCd=:statusCd " +
            "and lsshpd.shipGb='01' and lsm.shipStatus='04'")
    List<Lsshpd> findShipList(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("shipId") String shipId,
                              @Param("shipSeq") String shipSeq,
                              @Param("assortId") String assortId,
                              @Param("assortNm") String assortNm,
                              @Param("vendorId") String vendorId,
                              @Param("statusCd") String statusCd,
                              @Param("storageId") String storageId);

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
			"left outer join fetch ita.itbrnd ib "
			+
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
	@Query("select ld from Lsshpd ld " +
            "join fetch ld.lsshpm lm " +
            "left join fetch ld.tbOrderDetail td " +
            "join fetch ld.itasrt it " +
			"left join fetch it.itbrnd ib "
			+
			"join fetch ld.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
			+ "left join fetch itm.itvari3 itv3 "
			+
            "where case :shipStatus when '04' then lm.applyDay else lm.receiptDt end between :start and :end " +
            "and lm.masterShipGb in ('03', '04') " +
            "and lm.shipStatus=:shipStatus " +
            "and td.statusCd = :statusCd " +
            "and (:shipId is null or trim(:shipId)='' or ld.shipId=:shipId) " +
            "and (:assortId is null or trim(:assortId)='' or ld.assortId=:assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%',:assortNm,'%')) " +
            "and (:storageId is null or trim(:storageId)='' or lm.oStorageId=:storageId) " +
            "and (:blNo is null or trim(:blNo)='' or lm.blNo=:blNo) " +
            "and (lm.estiArrvDt between COALESCE(:staEstiArrvDt, '0000-01-01') and COALESCE(:endEstiArrvDt, '9999-12-31')) " +
            "and (:deliMethod is null or trim(:deliMethod)='' or lm.delMethod=:deliMethod)")
    List<Lsshpd> findLsshpdMoveList(@Param("start")LocalDateTime start,
                                    @Param("end")LocalDateTime end,
                                    @Param("shipId")String shipId,
                                    @Param("assortId")String assortId,
                                    @Param("assortNm")String assortNm,
                                    @Param("storageId")String storageId,
                                    @Param("deliMethod")String deliMethod,
                                    @Param("shipStatus")String shipStatus,
                                    @Param("statusCd")String statusCd,
                                    @Param("blNo")String blNo,
                                    @Param("staEstiArrvDt") LocalDate staEstiArrvDt,
                                    @Param("endEstiArrvDt") LocalDate endEstiArrvDt
    );

    List<Lsshpd> findByShipId(String shipId);

    @Query("select lsshpd from Lsshpd lsshpd " +
            "join fetch lsshpd.lsshpm lm " +
			"join fetch lsshpd.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
			+ "left join fetch itm.itvari3 itv3 " +
            "where lsshpd.shipId in :shipIdList")
    List<Lsshpd> findShipDetailListByShipIdList(@Param("shipIdList") List<String> shipIdList);

    @Query("select lsd from Lsshpd lsd " +
            "join fetch lsd.tbOrderDetail tod " +
            "where lsd.orderId in :orderIdList and tod.assortGb=:assortGb")
    List<Lsshpd> findAddGoodsByOrderIdList(@Param("orderIdList") List<String> orderIdList,
                                           @Param("assortGb") String assortGb);

    @Query("select lsd from Lsshpd lsd " +
            "join fetch lsd.lsshpm lsm " +
//            "join fetch lsd.ititmcList imc " +
            "where lsd.shipId = :shipId")
    List<Lsshpd> findByShipIdWithItitmc(@Param("shipId") String shipId);

    /**
     * 이동지시리스트 조회
     */
    @Query("select distinct ld from Lsshpd ld " +
            "join fetch ld.lsshpm lm " +
            "left join fetch ld.tbOrderDetail td " +
            "join fetch ld.itasrt it " +
			"join fetch ld.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
			+ "left join fetch itm.itvari3 itv3 " +
			// "join fetch it.itvariList ivs " +
            "where lm.receiptDt between :start and :end " +
            "and (:oStorageId is null or trim(:oStorageId)='' or lm.storageId=:oStorageId) " +
            "and (:storageId is null or trim(:storageId)='' or lm.oStorageId=:storageId) " +
            "and (:assortId is null or trim(:assortId)='' or it.assortId=:assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%',:assortNm,'%')) " +
            "and lm.shipStatus ='02' and ld.shipGb in ('03', '04') and lm.masterShipGb in ('03', '04')")
    List<Lsshpd> findMoveIndList(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("storageId") String storageId,
                                 @Param("oStorageId") String oStorageId,
                                 @Param("assortId") String assortId,
                                 @Param("assortNm") String assortNm
                                 );
}
