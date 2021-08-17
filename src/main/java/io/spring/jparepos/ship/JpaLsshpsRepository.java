package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaLsshpsRepository extends JpaRepository<Lsshps,String> {
    Lsshps findByShipIdAndEffEndDt(String shipId, Date effEndDt);
}
