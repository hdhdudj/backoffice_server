package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.idclass.LsdpsdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaLsdpsdRepository extends JpaRepository<Lsdpsd, LsdpsdId> {
    @Query("select max(d.depositSeq) from Lsdpsd d where d.depositNo = ?1")
    String findMaxDepositSeqByDepositNo(String depositNo);

    Lsdpsd findByDepositNoAndDepositSeq(String depositNo, String depositSeq);

    List<Lsdpsd> findByDepositNo(String depositNo);

    /**
     * 입고리스트 가져오는 쿼리
     */
    @Query("select distinct (ld) from Lsdpsd ld " +
            "left join fetch ld.lsdpsm lm " +
            "left join fetch ld.lspchd lcd " +
            "left join fetch lcd.lspchm lcm " +
//                        "left join fetch ld.lsdpsp lp " +
//                        "left join fetch ld.lsdpds ls " +
            "left join fetch lm.cmvdmr cm " +
            "left join fetch ld.ititmm im " +
            "left join fetch im.itasrt it " +
            "left join fetch it.ifBrand ib " +
            "left join fetch it.itvariList iv " +
//                        "left join fetch im.itvari1 iv1 " +
//                        "left join fetch im.itvari2 iv2 " +
            "where lm.depositDt between :start and :end " +
            "and (:assortId is null or trim(:assortId)='' or it.assortId=:assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%', :assortNm, '%')) " +
            "and (:vendorId is null or trim(:vendorId)='' or lm.vendorId=:vendorId) " +
            "and (:storageId is null or trim(:storageId)='' or lm.storeCd=:storageId) " +
            "and (:storageId is null or trim(:storageId)='' or lm.storeCd=:storageId) " +
            "order by ld.depositNo asc, ld.depositSeq asc")
    List<Lsdpsd> findDepositList(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("assortId") String assortId,
                                 @Param("assortNm") String assortNm,
                                 @Param("vendorId") String vendorId,
                                 @Param("storageId") String storageId
                                 );

    /**
     * 출고지시 대상 리스트 가져오는 쿼리
     */
    @Query("select distinct (ld) from Lsdpsd ld " +
            "left join fetch ld.lsdpsm lm " +
            "left join fetch ld.lspchd lcd " +
            "left join fetch lcd.tbOrderDetail tod " +
            "left join fetch tod.tbOrderMaster tom " +
            "left join fetch lcd.lspchm lcm " +
//                        "left join fetch ld.lsdpsp lp " +
//                        "left join fetch ld.lsdpds ls " +
            "left join fetch lm.cmvdmr cm " +
            "left join fetch ld.ititmm im " +
            "left join fetch im.itasrt it " +
//            "left join fetch it.ifBrand ib " +
//            "left join fetch it.itvariList iv " +
            "left join fetch im.itvari1 iv1 " +
            "left join fetch im.itvari2 iv2 " +
            "left join fetch im.itvari3 iv3 " +
            "where lm.depositDt between :start and :end " +
            "and (:assortId is null or trim(:assortId)='' or it.assortId=:assortId) " +
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%', :assortNm, '%')) " +
            "and (:vendorId is null or trim(:vendorId)='' or lm.vendorId=:vendorId) " +
            "and (:storageId is null or trim(:storageId)='' or lm.storeCd=:storageId) " +
            "and (:orderId is null or trim(:orderId)='' or tod.orderId=:orderId) " +
            "and (:orderSeq is null or trim(:orderSeq)='' or tod.orderSeq=:orderSeq) " +
            "order by ld.depositNo asc, ld.depositSeq asc")
    List<Lsdpsd> findShipCandidateList(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("assortId") String assortId,
                                 @Param("assortNm") String assortNm,
                                 @Param("vendorId") String vendorId,
                                 @Param("orderId") String orderId,
                                 @Param("orderSeq") String orderSeq,
                                 @Param("storageId") String storageId
                                 );
}
