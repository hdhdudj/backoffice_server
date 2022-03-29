package io.spring.jparepos.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.order.entity.IfOrderCancel;

public interface JpaIfOrderCancelRepository extends JpaRepository<IfOrderCancel, String> {

	List<IfOrderCancel> findByChannelOrderNoAndChannelOrderSeqAndChannelOrderStatusAndGoodsCnt(String channelOrderNo,
			String channelOrderSeq, String channelOrderStatus, Long goodsCnt);
}
