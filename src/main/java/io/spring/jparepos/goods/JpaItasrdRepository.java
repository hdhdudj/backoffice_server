package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Itasrd;
import io.spring.model.goods.idclass.ItasrdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItasrdRepository  extends JpaRepository<Itasrd, ItasrdId> {
    @Query("select max(i.seq) as maxVal from Itasrd as i where i.assortId = ?1")
    String findMaxSeqByAssortId(String assortId);

//    Itasrd findByAssortIdAndSeq(String assortId, String seq);

    List<Itasrd> findByAssortId(String assortId);

    Itasrd findByAssortIdAndSeq(String assortId, String seq);
}
