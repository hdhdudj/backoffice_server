package io.spring.jparepos.purchase;

import io.spring.model.purchase.entity.Lspchs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLspchsRepository extends JpaRepository<Lspchs, Long> {
}
