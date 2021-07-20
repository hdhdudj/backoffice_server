package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaLsdpspRepository extends JpaRepository<Lsdpsp, String> {
    Lsdpsp findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

    @Query("select max(l.purchaseSeq) as maxVal from Lsdpsp as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    @Query(value = "select nextval('seq_LSDPSP') as nextval", nativeQuery = true)
    String findMaxDepositPlanId();

    @Query("select p from Lsdpsp p join fetch p.lspchd d join fetch d.lspchb b where p.assortId=?1 and p.itemId=?2")
    List<Lsdpsp> findByAssortIdAndItemId(String assortId, String itemId);

    List<Lsdpsp> findByPurchaseNo(String purchaseNo);

    @Query("select p from Lsdpsp p join fetch p.lspchd d join fetch d.lspchb b where p.depositPlanId=?1")
    Lsdpsp findByDepositPlanId(String depositPlanId);
}
