package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.TbGoodsAddInfo;

public interface JpaTbGoodsAddInfoRepository extends JpaRepository<TbGoodsAddInfo, Long> {

	List<TbGoodsAddInfo> findByAssortId(String assortId);
}
