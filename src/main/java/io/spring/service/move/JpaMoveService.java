package io.spring.service.move;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.OrderMoveListData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaMoveService {
    private final JpaSequenceDataRepository jpaSequenceDateRepository;
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;

    private final JpaPurchaseService jpaPurchaseService;

    private final EntityManager em;

    /**
     * 주문 이동지시 대상 리스트 가져오는 함수 
     */
    public List<OrderMoveListData> getOrderMoveList(Map<String, Object> map) {
        Date startDt = (Date)map.get(StringFactory.getStrStartDt());
        Date endDt = (Date)map.get(StringFactory.getStrEndDt());
        String storageId = (String)map.get(StringFactory.getStrStorageId());
        String assortId = (String)map.get(StringFactory.getStrAssortId());
        String itemId = (String)map.get(StringFactory.getStrItemId());
        String deliMethod = (String)map.get(StringFactory.getStrDeliMethod());
        List<Lsdpsd> lsdpsdList = this.getLsdpsd(startDt, endDt, storageId, assortId, itemId, deliMethod);
        List<OrderMoveListData> orderMoveListDataList = new ArrayList<>();
        for(Lsdpsd lsdpsd : lsdpsdList){
            OrderMoveListData orderMoveListData = new OrderMoveListData(lsdpsd);
            orderMoveListDataList.add(orderMoveListData);
        }
        return orderMoveListDataList;
    }

    /**
     * 주문 이동지시 화면에서 검색에 맞는 Lsdpsd들을 가져오는 함수
     */
    private List<Lsdpsd> getLsdpsd(Date startDt, Date endDt, String storageId, String assortId, String itemId, String deliMethod) {
        // lsdpsd, lsdpsm, tbOrderDetail, itasrt, itvari
        startDt = startDt == null? Utilities.getStringToDate(StringFactory.getStartDay()) : startDt;
        endDt = endDt == null? Utilities.getStringToDate(StringFactory.getDoomDay()) : endDt;
        storageId = storageId == null || storageId.equals("")? "" : " and m.storeCd='" + storageId + "'";
        assortId = assortId == null || assortId.equals("")? "" : " and d.assortId='" + assortId + "'";
        itemId = itemId == null || itemId.equals("")? "" : " and d.itemId='" + itemId + "'";
        deliMethod = deliMethod == null || deliMethod.equals("")? "" : " and t.deliMethod='" + deliMethod + "'";
        Query query = em.createQuery("select d from Lsdpsd d " +
                "join fetch d.lsdpsm m " +
                "join fetch d.lsdpsp p " +
                "join fetch p.tbOrderDetail t " +
                "join fetch t.itasrt i " +
                "where " +
                "m.depositDt between ?1 and ?2" +
                storageId + assortId + itemId + deliMethod
        );
        query.setParameter(1, startDt)
                .setParameter(2, endDt);
        List<Lsdpsd> lsdpsdList = query.getResultList();
        return lsdpsdList;
    }

    /**
     * 주문 이동지시 저장 함수
     */
    @Transactional
    public List<String> saveOrderMove(List<OrderMoveSaveData> orderMoveSaveDataList) {
        List<String> shipIdList = new ArrayList<>();
        for(OrderMoveSaveData orderMoveSaveData : orderMoveSaveDataList){
            String shipId = this.saveOrderMoveSaveData(orderMoveSaveData);
            shipIdList.add(shipId);
        }
        return shipIdList;
    }

    /**
     * OrderMoveSaveData객체로 lsshpm,s,d 생성
     * lsdpsm,d,s,b, lsdpsp, ititmt(발주데이터) 생성
     * tbOrderDetail를 변경
     */
    private String saveOrderMoveSaveData(OrderMoveSaveData orderMoveSaveData) {
        // 1. 출고 data 생성
        String shipId = this.makeShipDate(orderMoveSaveData);
        // 2. 수량 수정
        this.updateQty(orderMoveSaveData);
        // 3. 발주 data 생성
        jpaPurchaseService.makePurchaseDataFromMoveSave(orderMoveSaveData);
//        shipId = Utilities.getStringNo('L',shipId,9);
        return shipId;
    }

    /**
     * 이동 관련 data 생성 함수 (lsshpm,d,s)
     */
    private String makeShipDate(OrderMoveSaveData orderMoveSaveData) {
        String shipId = jpaSequenceDateRepository.nextVal(StringFactory.getStrSeqLsshpm());
        shipId = Utilities.getStringNo('L',shipId,9);
        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d " +
//                "join fetch d.lsdpsp p " +
//                "join fetch d.lsdpsm m " +
//                "join fetch d.ititmc c " +
//                "join fetch p.tbOrderDetail t " +
//                "join fetch t.itasrt i " +
                "where " +
                "d.depositNo=?1 and d.depositSeq=?2"
        , Lsdpsd.class);
        query.setParameter(1, orderMoveSaveData.getDepositNo())
                .setParameter(2, orderMoveSaveData.getDepositSeq());
        Lsdpsd lsdpsd = query.getSingleResult();
        Itasrt itasrt = lsdpsd.getItasrt();
        List<Ititmc> ititmcList = lsdpsd.getItitmm().getItitmc();
        Date depositDt = lsdpsd.getLsdpsm().getDepositDt();
        String storageId = lsdpsd.getLsdpsm().getStoreCd();
        String itemGrade = lsdpsd.getItemGrade();
        ititmcList = ititmcList.stream().filter(x -> x.getEffEndDt().equals(depositDt)
                && x.getStorageId().equals(storageId)
                && x.getItemGrade().equals(itemGrade)).collect(Collectors.toList());
        Ititmc ititmc = ititmcList.get(0);
        TbOrderDetail tbOrderDetail = lsdpsd.getLsdpsp().getTbOrderDetail();
        Lsshpm lsshpm = new Lsshpm(shipId, itasrt, tbOrderDetail, ititmc);
        jpaLsshpmRepository.save(lsshpm);
        return shipId;
    }

    /**
     * 주문이동지시 저장 누른 후 발생하는 qty 변경 처리 함수
     */
    private void updateQty(OrderMoveSaveData orderMoveSaveData) {

    }

    /**
     * 상품 이동지시 대상 리스트 가져오는 함수
     */
//    public String getGoodsMoveList(String dUpperStr, String depositNo, String strDepositNo, int intEight) {
//    }

    /**
     * 상품 이동지시 저장 함수
     */
//    @Transactional
//    public String saveGoodsMove(GoodsMoveSaveData goodsMoveSaveData) {
//    }
}
