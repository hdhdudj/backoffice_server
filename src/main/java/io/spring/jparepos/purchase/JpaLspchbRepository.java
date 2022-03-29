package io.spring.jparepos.purchase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.spring.model.purchase.entity.Lspchb;

public interface JpaLspchbRepository extends JpaRepository<Lspchb, Long> {
    Lspchb findByPurchaseNoAndPurchaseSeqAndEffEndDt(String purchaseNo, String purchaseSeq, LocalDateTime effEndDt);

    @Query("select max(l.purchaseSeq) as maxVal from Lspchb as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    List<Lspchb> findByPurchaseNo(String purchaseNo);

	List<Lspchb> findByPurchaseNoAndEffEndDt(String purchaseNo, LocalDateTime effEndDt);

}
