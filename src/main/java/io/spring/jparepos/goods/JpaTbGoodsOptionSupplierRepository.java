package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.TbGoodsOptionSupplier;

public interface JpaTbGoodsOptionSupplierRepository extends JpaRepository<TbGoodsOptionSupplier, Long> {

	List<TbGoodsOptionSupplier> findByAssortId(String assortId);
}
