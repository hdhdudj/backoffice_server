package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.idclass.LspchdId;
import jdk.vm.ci.meta.Local;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaLspchdRepository extends JpaRepository<Lspchd, LspchdId> {
    List<Lspchs> findByPurchaseNo(String purchaseNo);
    @Query("select max(l.purchaseSeq) as maxVal from Lspchd as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    Lspchd findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

    /**
     * 해외입고처리 - 발주선택창 조회 쿼리
     */
    @Query("select distinct(ld) from Lspchd ld " +
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
            + "and (:piNo is null or trim(:piNo)='' or lm.piNo=:piNo) "
            + "and (:siteOrderNo is null or trim(:siteOrderNo)='' or lm.siteOrderNo=:siteOrderNo) "
            + "and lm.purchaseStatus in :statusArr")
    List<Lspchd> findPurchaseList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                  @Param("vendorId") String vendorId, @Param("storeCd") String storeCd,
                                  @Param("piNo") String piNo, @Param("siteOrderNo") String siteOrderNo,
                                  @Param("statusArr") List<String> statusArr);
}