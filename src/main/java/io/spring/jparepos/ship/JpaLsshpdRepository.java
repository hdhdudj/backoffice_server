package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.idclass.LsshpdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaLsshpdRepository extends JpaRepository<Lsshpd, LsshpdId> {
    @Query("select max(d.shipSeq) from Lsshpd d where d.shipId=?1")
    String findMaxSeq(String shipId);

}
