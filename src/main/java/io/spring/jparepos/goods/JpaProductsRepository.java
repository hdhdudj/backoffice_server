package io.spring.jparepos.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.Products;

public interface JpaProductsRepository extends JpaRepository<Products, Long> {


}
