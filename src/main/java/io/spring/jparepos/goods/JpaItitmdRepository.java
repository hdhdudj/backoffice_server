package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaItitmdRepository extends JpaRepository<Ititmd, Long> {
    Ititmd findByItemId(String itemId);
    Ititmd findByAssortIdAndItemIdAndEffEndDt(String assortId, String itemId, Date effEndDt);
}
