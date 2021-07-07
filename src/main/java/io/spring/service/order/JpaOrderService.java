package io.spring.service.order;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOrderService {
    private final JpaItitmmRepository jpaItitmmRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaTbOrderMasterRepository jpaTbOrderMasterRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaTbOrderHistoryRepository jpaTbOrderHistoryRepository;
    private final EntityManager em;

    // orderId, orderSeq를 받아 주문 상태를 변경해주는 함수
    @Transactional
    public void changeOrderStatus(String orderId, String orderSeq) {
        // orderId, orderSeq로 해당하는 TbOrderDetail 찾아오기
        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        if(tbOrderDetail == null){
            log.debug("There is no TbOrderDetail of " + orderId + " and " + orderSeq);
            return;
        }
        String assortGb = tbOrderDetail.getAssortGb();
        if(assortGb.equals(StringFactory.getGbOne())){ // assortGb == '01' : 직구
            changeOrderStatusWhenDirect(tbOrderDetail);
        }
        else if(assortGb.equals(StringFactory.getGbTwo())){ // assortGb == '02' : 수입
            changeOrderStatusWhenImport(tbOrderDetail);
        }
    }
    
    /**
     * 직구(해외창고 -> 국내(현지) 주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenDirect(TbOrderDetail tbOrderDetail) {
        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4인 애들의 list
        // assortId로 itasrt 찾아오기
//        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
//        if(itasrt == null){
//            log.debug("There is no Itasrt of " + tbOrderDetail.getAssortId());
//            return;
//        }
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
        String storageId = tbOrderDetail.getStorageId();
        // ititmm 불러오기
        Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
        // ititmt 불러오기
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
        if(ititmc.getQty() - ititmc.getShipIndicateQty() - tbOrderDetailsC04.size() > 0){ // (총 들어온 애들) - (이미 팔기로 예약된 애들) - (입고 완료 상태인 애들) > 0
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrC04()); // 입고완료 : C04
        }
        else if(ititmt.getTempQty() - ititmt.getTempIndicateQty() > 0){
            ititmt.setTempQty(ititmt.getTempQty()-1);
            em.persist(ititmt);
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB02()); // 발주완료 : B02
        }
        else {
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB01()); // 발주대기 : B01
        }
    }
    
    /**
     * 수입(해외창고 -> 현지창고 -> 현지 주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail) {
        // 1.국내재고, 2.국내입고예정, 3.해외재고, 4.해외입고예정 확인 후 주문상태 변경.
        // 입고예정이면 입고예정 data 생성.

        // 1. 국내재고 확인 (tbOrderDetail의 storageId 확인)

        // 2. 국내입고예정 재고 확인

        // 3. 해외재고 확인 (itasrt의 storageId 확인)

        // 4. 해외입고예정 재고 확인

    }

    /**
     * JB
     * orderDetail의 orderStatusCd를 update할 때 orderDetail과 orderHistory를 한꺼번에 update 시켜주는 함수
     * @param orderId
     * @param orderSeq
     * @param statusCd
     */
	public void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);

		List<TbOrderHistory> tohs = jpaTbOrderHistoryRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq,
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

        jpaTbOrderDetailRepository.save(tod);

        jpaTbOrderHistoryRepository.saveAll(tohs);
	}

}
