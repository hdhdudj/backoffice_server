package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.idclass.ItvariId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItvariRepository extends JpaRepository<Itvari, ItvariId> {
}
