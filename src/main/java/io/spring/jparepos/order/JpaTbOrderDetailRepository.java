package io.spring.jparepos.order;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.idclass.TbOrderDetailId;

public interface JpaTbOrderDetailRepository extends JpaRepository<TbOrderDetail, TbOrderDetailId> {

	TbOrderDetail findByOrderIdAndOrderSeq(String orderId, String orderSeq);

}
