package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaLsshpmRepository extends JpaRepository<Lsshpm, String> {
    Lsshpm findByShipId(String shipId);
}
