package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface JpaItasrnRepository extends JpaRepository<Itasrn, Long> {
    Itasrn findByAssortIdAndEffEndDt(String assortId, LocalDateTime effEndDt);

    @Query("select max(i.seq) as maxVal from Itasrn as i where i.assortId = ?1")
    String findMaxSeqByAssortId(String assortId);
}
