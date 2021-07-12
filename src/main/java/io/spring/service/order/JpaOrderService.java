package io.spring.service.order;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmmRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


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
    private final JpaPurchaseService jpaPurchaseService;
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
     * 직구(해외창고 -> 국내(현지)주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenDirect(TbOrderDetail tbOrderDetail) {
        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4인 애들의 list
        long sumOfTbOrderDetailsC04 = tbOrderDetailsC04.stream().map(x -> x.getQty()).reduce((a,b) -> a+b).get(); // C04인 애들의 sum(qty)
        // assortId로 itasrt 찾아오기
        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);
        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
        String storageId = itasrt.getStorageId();
        // ititmm 불러오기
        Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
        // ititmt 불러오기
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, storageId);
        if(ititmc.getQty() - ititmc.getShipIndicateQty() - sumOfTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){ // (총 들어온 애들) - (이미 팔기로 예약된 애들) - (국내입고 완료 상태인 애들) > 0
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrC04()); // 입고완료 : C04
        }
        else if(ititmt.getTempQty() - ititmt.getTempIndicateQty() - tbOrderDetail.getQty() >= 0){
            ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + tbOrderDetail.getQty());
            em.persist(ititmt);
            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
            jpaPurchaseService.makePurchaseData(itasrt, ititmc, ititmt);
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB02()); // 발주완료 : B02
        }
        else {
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB01()); // 발주대기 : B01
        }
    }
    
    /**
     * 수입(해외창고 -> 국내창고 -> 국내주문자)일 때 주문상태 처리 함수
     * Ititmc : 상품재고
     * Ititmt : 상품입고예정재고
     * @param tbOrderDetail
     */
    private void changeOrderStatusWhenImport(TbOrderDetail tbOrderDetail) {
        Itasrt itasrt = jpaItasrtRepository.findById(tbOrderDetail.getAssortId()).orElseGet(() -> null);

        String assortId = tbOrderDetail.getAssortId();
        String itemId = tbOrderDetail.getItemId();
        String domesticStorageId = tbOrderDetail.getStorageId(); // 주문자 현지(국내?) 창고 id (국내창고)
        String overseaStorageId = itasrt.getStorageId(); // 물건의 산지(?) 창고 id (해외창고)

        List<TbOrderDetail> tbOrderDetailsC01 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList()); // 주문코드가 CO1(해외입고완료)인 애들의 list
        List<TbOrderDetail> tbOrderDetailsC04 = jpaTbOrderDetailRepository.findAll().stream()
                .filter((x) -> x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList()); // 주문코드가 CO4(국내입고완료)인 애들의 list
//        List<TbOrderDetail> ovrsTbOrderDetailsC01 = tbOrderDetailsC01.stream().filter(x -> x.getStorageId().equals(overseaStorageId)).collect(Collectors.toList());
        List<TbOrderDetail> domTbOrderDetailsC04 = tbOrderDetailsC04.stream().filter(x -> x.getStorageId().equals(domesticStorageId)).collect(Collectors.toList());
        long sumOfTbOrderDetailsC01 = tbOrderDetailsC01.stream().map(x -> x.getQty()).reduce((a,b) -> a+b).get(); // C01인 애들의 sum(qty) (창고 id는 불요)
        long sumOfDomTbOrderDetailsC04 = domTbOrderDetailsC04.stream().map(x -> x.getQty()).reduce((a,b) -> a+b).get(); // C04고 국내창고인 애들의 sum(qty)
        log.debug("----- sumOfDomTbOrderDetailsC04 : " + sumOfDomTbOrderDetailsC04);
        // 국내창고 ititmc 불러오기
        Ititmc domItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, domesticStorageId);
        // 국내창고 ititmt 불러오기
        Ititmt domItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, domesticStorageId);
        // 해외창고 ititmc 불러오기
        Ititmc ovrsItitmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, overseaStorageId);
        // 해외창고 ititmt 불러오기
        Ititmt ovrsItitmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageId(assortId, itemId, overseaStorageId);

        // 1.국내재고, 2.국내입고예정, 3.해외재고, 4.해외입고예정 확인 후 주문상태 변경.
        // 입고예정이면 입고예정 data 생성.

        // 1. 국내재고 확인 (tbOrderDetail의 storageId 확인)
        if(domItitmc.getQty() - domItitmc.getShipIndicateQty() - sumOfDomTbOrderDetailsC04 - tbOrderDetail.getQty() >= 0){
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrC04()); // 국내(현지)입고완료 : C04
        }
        // 2. 국내입고예정 재고 확인
        else if(domItitmt.getTempQty() - domItitmt.getTempIndicateQty() - tbOrderDetail.getQty() >= 0){
            // ititmt 주문발주에 물량만큼 더하기
            domItitmt.setTempIndicateQty(domItitmt.getTempIndicateQty() + tbOrderDetail.getQty());
            em.persist(domItitmt);
            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
            jpaPurchaseService.makePurchaseData(itasrt, domItitmc, domItitmt);
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrC03()); // 이동지시완료 : C03
        }
        // 3. 해외재고 확인 (itasrt의 storageId 확인)
        else if(ovrsItitmc.getQty() - ovrsItitmc.getShipIndicateQty() - sumOfTbOrderDetailsC01 - tbOrderDetail.getQty() >= 0){
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrC01()); // 해외입고완료 : C01
        }
        // 4. 해외입고예정 재고 확인
        else if(ovrsItitmt.getTempQty() - ovrsItitmt.getTempIndicateQty() - tbOrderDetail.getQty() >= 0){
            // ititmt 주문발주에 물량만큼 더하기
            ovrsItitmt.setTempIndicateQty(ovrsItitmt.getTempIndicateQty() + tbOrderDetail.getQty());
            em.persist(ovrsItitmt);
            // 발주 data 변경하기 (lspchm,lspchd,lspchs,lspchb,lsdpsp)
            jpaPurchaseService.makePurchaseData(itasrt, ovrsItitmc, ovrsItitmt);
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB02()); // 발주완료 : B02
        }
        else {
            updateOrderStatusCd(tbOrderDetail.getOrderId(), tbOrderDetail.getOrderSeq(), StringFactory.getStrB01()); // 발주대기 : B01
        }
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
