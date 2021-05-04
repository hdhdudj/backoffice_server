package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaLspchbRepository extends JpaRepository<Lspchb, Long> {
    Lspchb findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

    @Query("select max(l.purchaseSeq) as maxVal from Lspchb as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);
}
