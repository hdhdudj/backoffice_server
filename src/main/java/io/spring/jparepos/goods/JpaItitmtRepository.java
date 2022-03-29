package io.spring.jparepos.goods;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;

public interface JpaItitmtRepository extends JpaRepository<Ititmt, ItitmtId> {
    @Query("select t from Ititmt t where t.assortId=?1 and t.itemId=?2 and t.storageId=?3 and t.itemGrade=?4 order by t.effEndDt desc")
    List<Ititmt> findByAssortIdAndItemIdAndStorageIdAndItemGrade(String assortId, String itemId, String domesticStorageId, String itemGrade);

    Ititmt findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(String assortId, String itemId, String storageId, String itemGrade, LocalDateTime purchaseDt);

	Ititmt findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(String assortId, String itemId, String storageId,
			String itemGrade, LocalDateTime purchaseDt);

//    Ititmt findByAssortIdAndItemIdAndStorageIdAndItemGradeAndUpdDt(String assortId, String itemId, String storageId, String itemGrade, LocalDateTime updDt);

//    Ititmt findByAssortIdAndItemIdAndStorageIdAndItemGradeAndRegDt(String assortId, String itemId, String oStorageId, String strEleven, LocalDateTime updDt);
}
