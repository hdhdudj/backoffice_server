package io.spring.jparepos.order;

import io.spring.model.order.entity.TbOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface JpaTbOrderHistoryRepository extends JpaRepository<TbOrderHistory, String> {
    TbOrderHistory findByOrderIdAndEffEndDt(String orderId, Date effEndDate);

    TbOrderHistory findByOrderIdAndOrderSeqAndEffEndDt(String orderId, String orderSeq,Date stringToDate);
}
