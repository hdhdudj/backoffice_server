package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpsm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLsdpsmRepository extends JpaRepository<Lsdpsm, String> {
    Lsdpsm findByDepositNo(String depositNo);
}
