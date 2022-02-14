package io.spring.jparepos.purchase;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.idclass.LspchdId;

public interface JpaLspchdRepository extends JpaRepository<Lspchd, LspchdId> {
    @Query("select ld from Lspchd ld join fetch ld.lspchm lm where ld.purchaseNo=:purchaseNo")
    List<Lspchd> findByPurchaseNo(@Param("purchaseNo") String purchaseNo);

    @Query("select max(l.purchaseSeq) as maxVal from Lspchd as l where l.purchaseNo = ?1")
    String findMaxPurchaseSeqByPurchaseNo(String purchaseNo);

    Lspchd findByPurchaseNoAndPurchaseSeq(String purchaseNo, String purchaseSeq);


    /**
     * 해외입고처리 - 발주선택창 조회 쿼리
     */
    @Query("select distinct(ld) from Lspchd ld " +
            "join fetch ld.lspchm lm " +
            "join fetch ld.lspchb lb " +
            "left outer join fetch ld.tbOrderDetail tod " +
            "left outer join fetch tod.tbOrderMaster tom " +
            "left outer join fetch tom.tbMember tm " +
            "left outer join fetch tom.tbMemberAddress tma " +
            "left outer join fetch ld.ititmm im " +
            "left outer join fetch im.itvari1 iv1 " +
            "left outer join fetch im.itvari2 iv2 " +
            "left outer join fetch im.itvari3 iv3 " +
            "join fetch im.itasrt ita " +
			"left outer join fetch ita.itbrnd ib "
			+
            "where lm.purchaseDt between :start and :end " +
            "and (:vendorId is null or trim(:vendorId)='' or lm.vendorId=:vendorId) "
            + "and (:storeCd is null or trim(:storeCd)='' or lm.storeCd=:storeCd) "
            + "and (:piNo is null or trim(:piNo)='' or lm.piNo=:piNo) "
            + "and (:siteOrderNo is null or trim(:siteOrderNo)='' or lm.siteOrderNo=:siteOrderNo) "
            + "and lm.purchaseStatus in :statusArr")
    List<Lspchd> findPurchaseList(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                  @Param("vendorId") String vendorId, @Param("storeCd") String storeCd,
                                  @Param("piNo") String piNo, @Param("siteOrderNo") String siteOrderNo,
                                  @Param("statusArr") List<String> statusArr);

	@Query(value = " " + "select * " + "from lspchd aa " + ",lspchb bb " + ",lspchm cc "
			+ "where aa.order_id =:orderId " + "and aa.order_seq =:orderSeq " + "and aa.purchase_no = bb.purchase_no "
			+ "and aa.purchase_seq =  bb.purchase_seq " + "and bb.eff_end_dt ='9999-12-31 23:59:59' "
			+ "and aa.purchase_no = cc.purchase_no " + "and cc.purchase_gb='01' " + "and cc.dealtype_cd in ('01','03') "
			+ "and bb.purchase_status ='01' ", nativeQuery = true)
	List<Lspchd> findItemByOrderIdAndOrderSeq(@Param("orderId") String orderId, @Param("orderSeq") String orderSeq);


    /**
     * lspchd 조건 검색 쿼리로 lspchd의 리스트를 가져오는 함수
     */
    @Query("select distinct(ld) from Lspchd ld " +
            "join fetch ld.lspchm lm " +
            "left outer join fetch ld.tbOrderDetail tod " +
            "left outer join fetch tod.tbOrderMaster tom " +
            "left outer join fetch tom.tbMember tm " +
            "left outer join fetch ld.ititmm itm " +
            "left outer join fetch itm.itasrt ita " +
			"left outer join fetch ita.itbrnd ib "
			+
            "left outer join fetch ita.itvariList iv " +
            "where lm.purchaseDt between :start and :end " +
//                "and (tod.statusCd in ('B01','C03') or lm.dealtypeCd='02') " +
            "and (:vendorId is null or trim(:vendorId)='' or ld.lspchm.vendorId=:vendorId) " +
            "and (:vendorId is null or trim(:vendorId)='' or ld.lspchm.vendorId=:vendorId) " +
            "and (:assortId is null or trim(:assortId)='' or ld.assortId=:assortId) "+
            "and (:purchaseStatus is null or trim(:purchaseStatus)='' or ld.lspchm.purchaseStatus=:purchaseStatus) "+
            "and (:purchaseGb is null or trim(:purchaseGb)='' or ld.lspchm.purchaseGb=:purchaseGb) " +
            "and (:dealtypeCd is null or trim(:dealtypeCd)='' or ld.lspchm.dealtypeCd=:dealtypeCd) " +
            "and (:purchaseNo is null or trim(:purchaseNo)='' or ld.purchaseNo=:purchaseNo) " +
            "and (:siteOrderNo is null or trim(:siteOrderNo)='' or lm.siteOrderNo=:siteOrderNo) " +
            "and (:custNm is null or trim(:custNm)='' or tm.custNm like concat('%',:custNm,'%')) " +
            "and (:brandId is null or trim(:brandId)='' or ib.brandId=:brandId) " +
            "and (:itemNm is null or trim(:itemNm)='' or itm.itemNm like concat('%',:itemNm,'%')) " +
            "and (:unifiedOrderNo is null or trim(:unifiedOrderNo)='' or tom.channelOrderNo=:unifiedOrderNo or tom.orderId=:unifiedOrderNo) " +
            "and (:orderName is null or trim(:orderName)='' or tom.orderName=:orderName)")
    List<Lspchd> getLspchdList(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("vendorId") String vendorId,
                               @Param("assortId") String assortId,
                               @Param("purchaseStatus") String purchaseStatus,
                               @Param("purchaseGb") String purchaseGb,
                               @Param("dealtypeCd") String dealtypeCd,
                               @Param("purchaseNo") String purchaseNo,
                               @Param("siteOrderNo") String siteOrderNo,
                               @Param("custNm") String custNm,
                               @Param("brandId") String brandId,
                               @Param("itemNm") String itemNm,
                               @Param("unifiedOrderNo") String unifiedOrderNo,
                               @Param("orderName") String orderName
    );

    /**
     * 발주사후 가져올 때 쿼리
     */
    @Query("select distinct (ld) from Lspchd ld " +
            "left outer join fetch ld.lspchm lm " +
            "left outer join fetch ld.lspchb lb " +
            "left outer join fetch ld.tbOrderDetail tod " +
            "left outer join fetch tod.tbOrderMaster tom " +
            "left outer join fetch tom.tbMember tm " +
            "left outer join fetch tom.tbMemberAddress tma " +
            "left outer join fetch ld.ititmm im " +
            "left outer join fetch im.itvari1 iv1 " +
            "left outer join fetch im.itvari2 iv2 " +
            "left outer join fetch im.itvari3 iv3 " +
            "left outer join fetch im.itasrt ita " +
			"left outer join fetch ita.itbrnd ib "
			+
            "where ld.purchaseNo=:purchaseNo " +
            "and lb.effEndDt='9999-12-31T23:59:59' and lb.purchaseStatus <> '05'"
            )
    List<Lspchd> findLspchdByPurchaseNo(@Param("purchaseNo") String purchaseNo);
}