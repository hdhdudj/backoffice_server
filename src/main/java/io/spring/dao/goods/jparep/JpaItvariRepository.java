package io.spring.dao.goods.jparep;

import io.spring.model.goods.Itvari;
import io.spring.model.goods.ItvariId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItvariRepository extends JpaRepository<Itvari, ItvariId> {
}
