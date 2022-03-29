package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.TbGoodsImage;

public interface JpaTbGoodsImageRepository extends JpaRepository<TbGoodsImage, Long> {

	List<TbGoodsImage> findByAssortId(String assortId);

	List<TbGoodsImage> findByAssortIdAndImageGb(String assortId, String imageGb);

}
