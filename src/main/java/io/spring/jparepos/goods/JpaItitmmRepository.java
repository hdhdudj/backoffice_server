package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.idclass.ItitmmId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItitmmRepository extends JpaRepository<Ititmm, ItitmmId> {
    @Query("select max(i.itemId) as maxVal from Ititmm as i where i.assortId = ?1")
    String findMaxItemIdByAssortId(String assortId);
    List<Ititmm> findByAssortId(String assortId);

    Ititmm findByAssortIdAndItemId(String assortId, String itemId);
}
