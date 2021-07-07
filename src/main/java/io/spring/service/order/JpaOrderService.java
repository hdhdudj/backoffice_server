package io.spring.service.order;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOrderService {

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;
	private final EntityManager em;


	public void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);

		List<TbOrderHistory> tohs = tbOrderHistoryrRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq,
				Utilities.getStringToDate(StringFactory.getDoomDay()));
		

		tod.setStatusCd(statusCd);
		 
		 
		Date newEffEndDate = new Date();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
		 }

			TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.getStringToDate(StringFactory.getDoomDay()));

		tohs.add(toh);

			tbOrderDetailRepository.save(tod);

			tbOrderHistoryrRepository.saveAll(tohs);
		 



	}

}
