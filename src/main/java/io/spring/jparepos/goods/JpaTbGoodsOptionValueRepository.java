package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.spring.model.goods.entity.TbGoodsOptionValue;
import io.spring.model.goods.idclass.TbGoodsOptionValueId;

public interface JpaTbGoodsOptionValueRepository extends JpaRepository<TbGoodsOptionValue, TbGoodsOptionValueId> {

	List<TbGoodsOptionValue> findByAssortId(String assortId);

	List<TbGoodsOptionValue> findByAssortIdAndDelYn(String assortId, String delYn);

	@Query("select max(i.seq) as maxVal from TbGoodsOptionValue as i where i.assortId = ?1")
	String findMaxSeqByAssortId(String assortId);

	TbGoodsOptionValue findByAssortIdAndSeq(String assortId, String seq);

//    @Query("select i.seq, i.optionGb from Itvari as i where i.assortId = ?1 and i.optionNm = ?2")

	TbGoodsOptionValue findByAssortIdAndOptionNm(String assortId, String optionNm);

	TbGoodsOptionValue findByAssortIdAndOptionNmAndVariationGb(String assortId, String optionNm, String variationGb);


}
