package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JpaLspchmRepository extends JpaRepository<Lspchm, String> {
    Optional<Lspchm> findByPurchaseNo(String purchaseNo);

    @Query("select m from Lspchm m left join fetch m.lspchdList")
    List<Lspchm> findPurchaseList(String purchaseVendorId, String assortId, String purchaseStatus, Date startDt, Date endDt);
}
