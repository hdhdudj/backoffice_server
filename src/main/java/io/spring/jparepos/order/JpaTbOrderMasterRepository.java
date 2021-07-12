package io.spring.jparepos.order;

import io.spring.model.order.entity.TbOrderMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaTbOrderMasterRepository extends JpaRepository<TbOrderMaster, String> {
    @Query("select max(t.orderId) from TbOrderMaster t")
    String findMaxOrderId();

    TbOrderMaster findByChannelOrderNo(String ifNo);
}
