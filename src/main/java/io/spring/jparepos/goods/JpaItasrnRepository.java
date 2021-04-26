package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrn;
import io.spring.model.goods.idclass.ItasrdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItasrnRepository extends JpaRepository<Itasrn, ItasrdId> {
}
