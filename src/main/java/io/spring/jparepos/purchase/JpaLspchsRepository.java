package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;

public interface JpaLspchsRepository extends JpaRepository<Lspchs, Long> {
    Lspchs findByPurchaseNoAndEffEndDt(String purchaseNo, LocalDateTime effEndDt);
}
