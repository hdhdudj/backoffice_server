package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.idclass.ItitmcId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItitmcRepository extends JpaRepository<Ititmc, ItitmcId> {
    Ititmc findByAssortIdAndItemIdAndStorageId(String assortId, String itemId, String storageId);
}
