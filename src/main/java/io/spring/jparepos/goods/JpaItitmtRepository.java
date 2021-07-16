package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaItitmtRepository extends JpaRepository<Ititmt, ItitmtId> {
    @Query("select t from Ititmt t where t.assortId=?1 and t.itemId=?2 and t.storageId=?3 and t.itemGrade=?4 order by t.effEndDt asc")
    List<Ititmt> findByAssortIdAndItemIdAndStorageIdAndItemGrade(String assortId, String itemId, String domesticStorageId, String strEleven);
}
