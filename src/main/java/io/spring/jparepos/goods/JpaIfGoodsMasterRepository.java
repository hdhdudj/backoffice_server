package io.spring.jparepos.goods;

import io.spring.model.goods.entity.IfGoodsMaster;
import io.spring.model.goods.idclass.IfGoodsMasterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface JpaIfGoodsMasterRepository extends JpaRepository<IfGoodsMaster, IfGoodsMasterId> {
    @Query("select i from IfGoodsMaster i where i.assortId in :assortIdSet")
    List<IfGoodsMaster> findByAssortIdSet(@Param("assortIdSet") Set<String> assortIdSet);
}
