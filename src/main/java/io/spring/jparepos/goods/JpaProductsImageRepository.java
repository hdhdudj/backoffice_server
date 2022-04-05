package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.ProductsImage;

public interface JpaProductsImageRepository extends JpaRepository<ProductsImage, Long> {

	List<ProductsImage> findByProductId(Long productId);

	List<ProductsImage> findByProductIdAndImageGb(Long productId, String imageGb);

}
