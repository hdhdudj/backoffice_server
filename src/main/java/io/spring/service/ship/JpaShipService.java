package io.spring.service.ship;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.request.ShipIndicateListData;
import io.spring.model.ship.request.ShipIndicateSaveData;
import io.spring.service.move.JpaMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaShipService {
    private final JpaMoveService jpaMoveService;

    private final EntityManager em;

    /**
     * 출고지시 화면에서 조건검색하면 리스트를 반환해주는 함수
     */
    public List<ShipIndicateListData> getOrderSaveList(Date startDt, Date endDt, String assortId, String assortNm, String vendorId) {
        List<ShipIndicateListData> shipIndicateListDataList = new ArrayList<>();
        List<TbOrderDetail> tbOrderDetailList = this.getOrdersByCondition(startDt, endDt, assortId, assortNm, vendorId);
        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
            ShipIndicateListData shipIndicateListData = new ShipIndicateListData(tbOrderDetail);
            shipIndicateListDataList.add(shipIndicateListData);
            List<Itvari> itvariList = tbOrderDetail.getItasrt().getItvariList();
            if(itvariList.size() == 1){
                Itvari itvari1 = itvariList.get(0);
                shipIndicateListData.setOptionNm1(itvari1.getOptionNm());
            }
            else if(itvariList.size() == 2){
                Itvari itvari2 = itvariList.get(1);
                shipIndicateListData.setOptionNm2(itvari2.getOptionNm());
            }
        }
        return shipIndicateListDataList;
    }

    /**
     * 출고지시 화면에서 검색 조건에 따른 tbOrderDetail 객체를 가져오는 쿼리를 실행해 결과를 반환하는 함수
     */
    private List<TbOrderDetail> getOrdersByCondition(Date startDt, Date endDt, String assortId, String assortNm, String vendorId) {
        startDt = startDt == null? Utilities.getStringToDate(StringFactory.getStartDay()) : startDt;
        endDt = endDt == null? Utilities.getStringToDate(StringFactory.getDoomDay()) : endDt;
        assortId = assortId == null || assortId.trim().equals("")? "":" and td.assortId='"+assortId+"'";
        vendorId = vendorId == null || vendorId.trim().equals("")? "":" and it.vendorId='"+vendorId+"'";
        TypedQuery<TbOrderDetail> query = em.createQuery("select td from TbOrderDetail td " +
                "join fetch td.tbOrderMaster to " +
                "join fetch td.itasrt it " +
                "where to.orderDate between ?1 and ?2" +
                assortId + vendorId
                , TbOrderDetail.class);
        query.setParameter(1,startDt).setParameter(2,endDt);
        List<TbOrderDetail> tbOrderDetailList = query.getResultList();

        return tbOrderDetailList;
    }

    /**
     * 출고지시 저장 함수
     */
    @Transactional
    public List<String> saveShipIndicate(List<ShipIndicateSaveData> shipIndicateSaveDataList) {
        if(shipIndicateSaveDataList.size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<String> shipIdList = new ArrayList<>();
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        // 1. 출고 data 생성
        for(ShipIndicateSaveData shipIndicateSaveData : shipIndicateSaveDataList){
            String shipId = this.saveShipIndicateSaveData(lsdpsdList, shipIndicateSaveData);
            shipIdList.add(shipId);
        }
        // 2. tbOrderDetail
        return shipIdList;
    }

    /**
     * ShipIndicateSaveData 객체로 lsshpm,s,d 생성
     * tbOrderDetail를 변경
     */
    private String saveShipIndicateSaveData(List<Lsdpsd> lsdpsdList, ShipIndicateSaveData shipIndicateSaveData) {
//        Lsdpsd lsdpsd = this.getLsdpsdByOrderIdAndOrderSeq(shipIndicateSaveData);
//        String shipId = jpaMoveService.makeOrderShipData(lsdpsd, shipIndicateSaveData.getQty(), StringFactory.getGbFour());
//        lsdpsdList.add(lsdpsd);
        return null;
    }

//    /**
//     * orderId와 orderSeq로 를 가져오는 함수
//     */
//    private Lsdpsd getLsdpsdByOrderIdAndOrderSeq(ShipIndicateSaveData shipIndicateSaveData) {
//        TypedQuery<Lsdpsp> query = em.createQuery("select p from Lsdpsp p " +
////                "join fetch d.lsdpsp lp " +
////                "join fetch d.lsdpsm lm " +
////                "join fetch d.ititmm tm " +
////                "join fetch d.itasrt it " +
////                "join fetch tm.ititmc ic " +
////                "join fetch lp.tbOrderDetail t " +
//                        "where " +
//                        "p.orderId=?1 and p.orderSeq=?2"
//                , Lsdpsp.class);
//        query.setParameter(1, shipIndicateSaveData.getOrderId())
//        .setParameter(2,shipIndicateSaveData.getOrderSeq());
//        Lsdpsp lsdpsp = query.getSingleResult();
//
//        return lsdpsp.getLsdpsd();
//    }
}
