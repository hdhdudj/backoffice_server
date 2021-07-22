package io.spring.service.move;

import io.spring.infrastructure.util.StringFactory;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.OrderMoveListData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaMoveService {
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;
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
        Query query = em.createQuery("select d from Lsdpsd d " +
                "join fetch d.lsdpsm m " +
                "join fetch d.lsdpsp p " +
                "join fetch p.tbOrderDetail t " +
                "join fetch t.itasrt i " +
                "where m.storeCd=?1 and " +
                "m.depositDt between ?2 and ?3 and " +
                "d.assortId=?4 and d.itemId=?5 and t.deliMethod=?6");
        query.setParameter(1, startDt)
                .setParameter(2, endDt)
                .setParameter(3, storageId)
                .setParameter(4,assortId)
                .setParameter(5,itemId)
                .setParameter(6,deliMethod);
        List<Lsdpsd> lsdpsdList = query.getResultList();
        return lsdpsdList;
    }

    /**
     * 주문 이동지시 저장 함수
     */
    @Transactional
    public String saveOrderMove(OrderMoveSaveData orderMoveSaveData) {
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
