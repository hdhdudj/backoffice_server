package io.spring.jparepos.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.order.entity.TbOrderHistory;

public interface JpaTbOrderHistoryRepository extends JpaRepository<TbOrderHistory, Long> {

	List<TbOrderHistory> findByOrderIdAndOrderSeqAndEffEndDt(String orderId, String orderSeq, Date effEndDt);

}
