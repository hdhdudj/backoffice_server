package io.spring.core.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.data.goods.GoodsRequestData;
import io.spring.data.goods.Itasrt;

public interface GoodsJpaRepository extends JpaRepository<Itasrt, Long>{

}
