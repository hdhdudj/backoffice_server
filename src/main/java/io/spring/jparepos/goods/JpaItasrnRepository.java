package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrn;
import io.spring.model.goods.idclass.ItasrnId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaItasrnRepository extends JpaRepository<Itasrn, ItasrnId> {
    Itasrn findByAssortIdAndEffEndDt(String assortId, Date effEndDt);
}
