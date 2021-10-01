package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.idclass.LspchdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaLspchdRepository extends JpaRepository<Lspchd, LspchdId> {
    List<Lspchs> findByPurchaseNo(String purchaseNo);
    @Query("select max(l.purchaseSeq) as maxVal from Lspchd as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    Lspchd findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

    @Query("select d from Lspchd d join fetch d.lspchm m join fetch d.lspchb b where d.assortId=?1 and d.itemId=?2 order by d.purchaseNo asc")
    List<Lspchd> findByAssortIdAndItemId(String assortId, String itemId);

    Lspchd findByOrderIdAndOrderSeq(String orderId, String orderSeq);
}
