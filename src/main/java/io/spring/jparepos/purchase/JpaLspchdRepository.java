package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.idclass.LspchdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLspchdRepository extends JpaRepository<Lspchd, LspchdId> {
}
