package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrd;
import io.spring.model.goods.idclass.ItasrdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItasrdRepository  extends JpaRepository<Itasrd, ItasrdId> {
}
