package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lsdpsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaLsdpspRepository extends JpaRepository<Lsdpsp, String> {
    Lsdpsp findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

    @Query("select max(l.purchaseSeq) as maxVal from Lsdpsp as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);
}
