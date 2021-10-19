package io.spring.jparepos.goods;

import io.spring.model.goods.entity.IfGoodsMaster;
import io.spring.model.goods.idclass.IfGoodsMasterId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIfGoodsMasterRepository extends JpaRepository<IfGoodsMaster, IfGoodsMasterId> {
}
