package io.spring.jparepos.ship;

import io.spring.model.ship.entity.Lsshpm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaLsshpmRepository extends JpaRepository<Lsshpm, String> {
    Lsshpm findByShipId(String shipId);



    @Query("select lsshpm from Lsshpm lsshpm " +
            "where lsshpm.shipId in :shipIdList")
    List<Lsshpm> findShipMasterListByShipIdList(@Param("shipIdList") List<String> shipIdList);
}
