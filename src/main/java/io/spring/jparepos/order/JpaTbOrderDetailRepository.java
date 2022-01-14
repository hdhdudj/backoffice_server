package io.spring.jparepos.order;

import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.idclass.TbOrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface JpaTbOrderDetailRepository extends JpaRepository<TbOrderDetail, TbOrderDetailId> {
    @Query("select t from TbOrderDetail t left join fetch t.ititmm where t.orderId=?1 and t.goodsNm=?2")
    TbOrderDetail findByOrderIdAndGoodsNm(String orderId, String goodsNm);

    @Query("select max(t.orderSeq) from TbOrderDetail t where t.orderId=?1")
    String findMaxOrderSeqWhereOrderId(String orderId);

    TbOrderDetail findByOrderIdAndOrderSeq(String orderId, String orderSeq);

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
}
