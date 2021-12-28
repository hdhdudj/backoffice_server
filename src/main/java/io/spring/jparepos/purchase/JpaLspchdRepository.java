package io.spring.jparepos.purchase;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.idclass.LspchdId;

public interface JpaLspchdRepository extends JpaRepository<Lspchd, LspchdId> {
    List<Lspchd> findByPurchaseNo(String purchaseNo);
    @Query("select max(l.purchaseSeq) as maxVal from Lspchd as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    Lspchd findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);

	@Query(value = " " + "select * " + "from lspchd aa " + ",lspchb bb " + ",lspchm cc "
			+ "where aa.order_id =:orderId " + "and aa.order_seq =:orderSeq " + "and aa.purchase_no = bb.purchase_no "
			+ "and aa.purchase_seq =  bb.purchase_seq " + "and bb.eff_end_dt ='9999-12-31 23:59:59' "
			+ "and aa.purchase_no = cc.purchase_no " + "and cc.purchase_gb='01' " + "and cc.dealtype_cd in ('01','03') "
			+ "and bb.purchase_status ='01' ", nativeQuery = true)
	List<Lspchd> findItemByOrderIdAndOrderSeq(@Param("orderId") String orderId, @Param("orderSeq") String orderSeq);
    
}