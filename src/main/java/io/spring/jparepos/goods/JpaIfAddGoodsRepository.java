package io.spring.jparepos.goods;

import io.spring.model.goods.entity.IfAddGoods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIfAddGoodsRepository extends JpaRepository<IfAddGoods, String> {
}
