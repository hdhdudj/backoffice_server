package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaItasrnRepository extends JpaRepository<Itasrn, Long> {
    Itasrn findByAssortIdAndEffEndDt(String assortId, Date effEndDt);
}
