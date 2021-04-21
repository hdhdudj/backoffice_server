package io.spring.dao.goods.jparep;

import io.spring.model.goods.Itasrd;
import io.spring.model.goods.ItasrdId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItasrdRepository  extends JpaRepository<Itasrd, ItasrdId> {
}
