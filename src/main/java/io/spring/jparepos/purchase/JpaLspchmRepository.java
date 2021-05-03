package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaLspchmRepository extends JpaRepository<Lspchm, String> {
    Optional<Lspchm> findByPurchaseNo(String purchaseNo);
}
