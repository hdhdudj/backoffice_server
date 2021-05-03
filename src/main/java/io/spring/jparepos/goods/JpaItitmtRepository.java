package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItitmtRepository extends JpaRepository<Ititmt, ItitmtId> {
}
