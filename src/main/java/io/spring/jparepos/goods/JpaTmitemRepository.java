package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Tmitem;
import io.spring.model.goods.idclass.TmitemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTmitemRepository extends JpaRepository<Tmitem, TmitemId> {
}
