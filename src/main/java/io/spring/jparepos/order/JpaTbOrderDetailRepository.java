package io.spring.jparepos.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.idclass.TbOrderDetailId;


public interface JpaTbOrderDetailRepository extends JpaRepository<TbOrderDetail, TbOrderDetailId> {
    @Query("select t from TbOrderDetail t left join fetch t.ititmm where t.orderId=?1 and t.goodsNm=?2")
    TbOrderDetail findByOrderIdAndGoodsNm(String orderId, String goodsNm);

    @Query("select max(t.orderSeq) from TbOrderDetail t where t.orderId=?1")
    String findMaxOrderSeqWhereOrderId(String orderId);

    TbOrderDetail findByOrderIdAndOrderSeq(String orderId, String orderSeq);

    /**
     * changeOrderStatus에서 쓰는 쿼리
     */
    @Query("select td from TbOrderDetail td " +
            "join fetch td.tbOrderMaster tm " +
            "left join fetch td.ititmm it " +
            "left join fetch it.itasrt itasrt " +
            "where td.orderId = :orderId " +
            "and td.orderSeq = :orderSeq")
    TbOrderDetail findByOrderIdAndOrderSeq2(@Param("orderId") String orderId, @Param("orderSeq") String orderSeq);

    List<TbOrderDetail> findByAssortIdAndItemId(String assortId, String itemId);

    List<TbOrderDetail> findByAssortIdAndItemIdAndQtyAndStatusCd(String assortId, String itemId, Long purchasePlanQty, String strC03);

    /**
     * 출고지시리스트 가져오는 쿼리
     */
    @Query("select td from TbOrderDetail td " +
            "join fetch td.tbOrderMaster to " +
            "join fetch td.ititmm itm " +
            "join fetch itm.itasrt it " +
            "where to.orderDate between :start and :end " +
            "and (:assortId is null or trim(:assortId)='' or td.assortId=:assortId) "+
            "and (:vendorId is null or trim(:vendorId)='' or it.vendorId=:vendorId) "+
            "and (:assortNm is null or trim(:assortNm)='' or it.assortNm like concat('%', :assortNm, '%')) " +
            "and td.statusCd=:statusCd")
    List<TbOrderDetail> findIndicateShipList(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("assortId") String assortId,
                                             @Param("vendorId") String vendorId,
                                             @Param("assortNm") String assortNm,
                                             @Param("statusCd") String statusCd
                                             );

												@Query("select td from TbOrderDetail td "
														+ "join fetch td.tbOrderMaster to "
														+ "join fetch td.ititmm itm " + "join fetch itm.itasrt it "
														+ "left join fetch it.itbrnd ib "
														+ "left join fetch itm.itvari1 itv1 "
														+ "left join fetch itm.itvari2 itv2 "
														+ "left join fetch itm.itvari3 itv3 "
														+ "left join fetch it.cmvdmr cm "
														+ "where td.statusCd=:statusCd "
														+ "and datediff(current_timestamp,to.payDt) > :waitCnt "

												)
												List<TbOrderDetail> findOrderStatusWatingDay(
														@Param("statusCd") String statusCd,
														@Param("waitCnt") int waitCnt);

    @Query("select tod from TbOrderDetail tod " +
            "where (tod.orderId = :orderId and tod.orderSeq = :orderSeq) or (tod.orderId = :orderId and tod.parentOrderSeq = :orderSeq)")
    List<TbOrderDetail> findByTbOrderDetailWithAddGoods(@Param("orderId") String orderId, @Param("orderSeq") String orderSeq);
}
