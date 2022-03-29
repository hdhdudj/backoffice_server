package io.spring.jparepos.order;

import io.spring.model.order.entity.TbOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface JpaTbOrderHistoryRepository extends JpaRepository<TbOrderHistory, Long> {
	List<TbOrderHistory> findByOrderIdAndOrderSeqAndEffEndDt(String orderId, String orderSeq, LocalDateTime effEndDt);
}
