package io.spring.jparepos.goods;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.idclass.ItitmcId;

public interface JpaItitmcRepository extends JpaRepository<Ititmc, ItitmcId> {
    @Query("select c from Ititmc c where c.assortId=?1 and c.itemId=?2 and c.storageId=?3 and c.itemGrade=?4 order by c.effEndDt desc")
    List<Ititmc> findByAssortIdAndItemIdAndStorageIdAndItemGrade(String assortId, String itemId, String storageId, String itemGrade);

//    Ititmc findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(String assortId, String itemId, String storageId, String itemGrade, Date effEndDt);


    @Query("select i from Ititmc i where i.assortId=?1 and i.itemId=?2 order by i.effEndDt asc")
    List<Ititmc> findByAssortIdAndItemIdOrderByEffEndDtAsc(String assortId, String itemId);

	@Query("select i from Ititmc i where i.assortId=?1 and i.itemId=?2 and i.storageId=?3 order by i.effEndDt asc")
	List<Ititmc> findByAssortIdAndItemIdAndStorageIdOrderByEffEndDtAsc(String assortId, String itemId,
			String storageId);

    Ititmc findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(String assortId, String itemId, String storageId, String itemGrade, LocalDateTime effStaDt);


    List<Ititmc> findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(String assortId, String itemId, LocalDateTime excAppDt);

	List<Ititmc> findByAssortIdAndItemIdAndEffEndDtAndStorageIdOrderByEffEndDtAsc(String assortId, String itemId,
			LocalDateTime excAppDt, String storageId);

    Ititmc findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(String assortId, String itemId, String storageId, String itemGrade, LocalDateTime dateToLocalDateTime);
}
