package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface JpaLspchbRepository extends JpaRepository<Lspchb, Long> {
    Lspchb findByPurchaseNoAndEffEndDt(String purchaseNo, Date effEndDt);

    @Query("select max(l.purchaseSeq) as maxVal from Lspchb as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);
}
