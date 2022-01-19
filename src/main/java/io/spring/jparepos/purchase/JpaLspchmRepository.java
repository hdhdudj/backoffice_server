package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaLspchmRepository extends JpaRepository<Lspchm, String> {
    Optional<Lspchm> findByPurchaseNo(String purchaseNo);

    @Query(value = "select nextval('seq_LSPCHM') as nextval", nativeQuery = true)
    String findMaxPurchaseNo();
}
