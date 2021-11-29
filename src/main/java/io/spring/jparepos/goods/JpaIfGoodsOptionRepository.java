package io.spring.jparepos.goods;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.goods.entity.IfGoodsOption;

public interface JpaIfGoodsOptionRepository extends JpaRepository<IfGoodsOption, String> {
    List<IfGoodsOption> findByGoodsNo(String goodsNo);

	List<IfGoodsOption> findBySnoAndGoodsNo(String sno, String goodsNo);

}
