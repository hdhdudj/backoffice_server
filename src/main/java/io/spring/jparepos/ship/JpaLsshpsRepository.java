package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface JpaLsshpsRepository extends JpaRepository<Lsshps,String> {
    Lsshps findByShipIdAndEffEndDt(String shipId, LocalDateTime effEndDt);
}
