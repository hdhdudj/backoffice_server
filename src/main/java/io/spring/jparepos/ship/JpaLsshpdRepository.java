package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.idclass.LsshpdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLsshpdRepository extends JpaRepository<LsshpdId, Lsshpd> {
}
