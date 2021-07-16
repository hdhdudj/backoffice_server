package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.idclass.ItitmcId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItitmcRepository extends JpaRepository<Ititmc, ItitmcId> {
    @Query("select c from Ititmc c where c.assortId=?1 and c.itemId=?2 and c.storageId=?3 and c.itemGrade=?4 order by c.effEndDt asc")
    List<Ititmc> findByAssortIdAndItemIdAndStorageIdAndItemGrade(String assortId, String itemId, String storageId, String itemGrade);
}
