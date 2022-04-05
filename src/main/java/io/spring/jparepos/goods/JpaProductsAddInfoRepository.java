package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.ProductsAddInfo;

public interface JpaProductsAddInfoRepository extends JpaRepository<ProductsAddInfo, Long> {

	List<ProductsAddInfo> findByProductId(Long productId);
}

