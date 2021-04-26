package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmd;
import io.spring.model.goods.idclass.ItitmdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItitmdRepository extends JpaRepository<Ititmd, ItitmdId> {
}
