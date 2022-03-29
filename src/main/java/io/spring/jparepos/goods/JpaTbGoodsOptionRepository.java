package io.spring.jparepos.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.goods.entity.TbGoodsOption;
import io.spring.model.goods.idclass.TbGoodsOptionId;

public interface JpaTbGoodsOptionRepository extends JpaRepository<TbGoodsOption, TbGoodsOptionId> {
	@Query("select max(i.itemId) as maxVal from TbGoodsOption as i where i.assortId = ?1")
	String findMaxItemIdByAssortId(String assortId);

	List<TbGoodsOption> findByAssortId(String assortId);

	List<TbGoodsOption> findByAssortIdAndDelYn(String assortId, String delYn);

	TbGoodsOption findByAssortIdAndItemId(String assortId, String itemId);

	TbGoodsOption findByAssortIdAndVariationSeq1AndDelYn(String assortId, String itemId, String delYn);

	@Query("select tgo from TbGoodsOption tgo "
			+
	            "where 1=1 " +
	            "and (:assortId is null or trim(:assortId)='' or tgo.assortId=:assortId) " +
	            "and (:seq1 is null or trim(:seq1)='' or tgo.variationSeq1=:seq1) " +
	            "and (:seq2 is null or trim(:seq2)='' or tgo.variationSeq2=:seq2) " +
	            "and (:seq3 is null or trim(:seq3)='' or tgo.variationSeq3=:seq3) " )
	    List<TbGoodsOption> findOptionList(
	    @Param("assortId") String assortId,
	    @Param("seq1") String seq1,
	@Param("seq2") String seq2,
	@Param("seq3") String seq3
	                                      );


}
