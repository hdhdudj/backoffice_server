package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Tmitem;
import io.spring.model.goods.idclass.TmitemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTmitemRepository extends JpaRepository<Tmitem, TmitemId> {
    Optional<Tmitem> findByChannelGbAndAssortIdAndItemId(String channelGb, String assortId, String itemId);
}
