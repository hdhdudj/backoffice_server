package io.spring.jparepos.deposit;

import io.spring.model.deposit.entity.Lsdpds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLsdpdsRepository extends JpaRepository<Lsdpds, String> {
}
