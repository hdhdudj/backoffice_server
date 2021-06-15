package io.spring.jparepos.goods;

import io.spring.model.goods.entity.Tmmapi;
import io.spring.model.goods.idclass.TmmapiId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTmmapiRepository extends JpaRepository<Tmmapi, TmmapiId> {
    Optional<Tmmapi> findByChannelGbAndAssortId(String gbOne, String assortId);
}
