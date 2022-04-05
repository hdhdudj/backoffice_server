package io.spring.jparepos.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.ProductsMaster;

public interface JpaProductsMasterRepository extends JpaRepository<ProductsMaster, Long> {

}
