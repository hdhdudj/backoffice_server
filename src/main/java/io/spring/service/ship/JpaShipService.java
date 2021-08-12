package io.spring.service.ship;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
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
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

    private final EntityManager em;

    /**
     * 출고지시 화면에서 조건검색하면 리스트를 반환해주는 함수
     */
    public ShipIndicateSaveListResponseData getOrderSaveList(Date startDt, Date endDt, String assortId, String assortNm, String purchaseVendorId) {
        List<ShipIndicateSaveListResponseData.Ship> shipList = new ArrayList<>();
        List<TbOrderDetail> tbOrderDetailList = this.getOrdersByCondition(startDt, endDt, assortId, assortNm, purchaseVendorId);
        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
            ShipIndicateSaveListResponseData.Ship ship = new ShipIndicateSaveListResponseData.Ship(tbOrderDetail);
            shipList.add(ship);
            List<Itvari> itvariList = tbOrderDetail.getItasrt().getItvariList();
            if(itvariList.size() == 1){
                Itvari itvari1 = itvariList.get(0);
                ship.setOptionNm1(itvari1.getOptionNm());
            }
            else if(itvariList.size() == 2){
                Itvari itvari2 = itvariList.get(1);
                ship.setOptionNm2(itvari2.getOptionNm());
            }
        }
        ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = new ShipIndicateSaveListResponseData(startDt, endDt, assortId, assortNm, purchaseVendorId);
        shipIndicateSaveListResponseData.setShips(shipList);
        return shipIndicateSaveListResponseData;
    }

    /**
     * 출고지시 화면에서 검색 조건에 따른 tbOrderDetail 객체를 가져오는 쿼리를 실행해 결과를 반환하는 함수
     */
    private List<TbOrderDetail> getOrdersByCondition(Date startDt, Date endDt, String assortId, String assortNm, String vendorId) {
        startDt = startDt == null? Utilities.getStringToDate(StringFactory.getStartDay()) : Utilities.addHoursToJavaUtilDate(startDt,0);
        endDt = endDt == null? Utilities.getStringToDate(StringFactory.getDoomDay()) : Utilities.addHoursToJavaUtilDate(startDt,24);
        TypedQuery<TbOrderDetail> query = em.createQuery("select td from TbOrderDetail td " +
                "join fetch td.tbOrderMaster to " +
                "join fetch td.itasrt it " +
                "where to.orderDate between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or td.assortId=?3) "+
                "and (?4 is null or trim(?4)='' or it.vendorId=?4) "+
                "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%'))"
                , TbOrderDetail.class);
        query.setParameter(1,startDt).setParameter(2,endDt)
        .setParameter(3,assortId).setParameter(4,vendorId)
        .setParameter(5,assortNm);
        List<TbOrderDetail> tbOrderDetailList = query.getResultList();

        return tbOrderDetailList;
    }

    /**
     * 출고지시 저장 함수
     */
    @Transactional
    public List<String> saveShipIndicate(ShipIndicateSaveListData shipIndicateSaveDataList) {
        if(shipIndicateSaveDataList.getShips().size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<String> shipIdList = new ArrayList<>();
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        // 1. 입고 data 수량계산
//        this.updateShipQty(shipIndicateSaveDataList);
        // 2. 출고 data 생성
        // 2. tbOrderDetail
        return shipIdList;
    }

    /**
     * Lsshpm, s 저장 함수
     */
    private Lsshpm insertShipData(ShipIndicateSaveListData shipIndicateSaveDataList) {
        String shipId = this.getShipId();
        Lsshpm lsshpm = new Lsshpm(shipId, shipIndicateSaveDataList);

        return lsshpm;
    }

    /**
     * ShipIndicateSaveData 객체로 lsshpm,s,d 생성
     * tbOrderDetail를 변경
     */
    private String saveShipIndicateSaveData(List<Lsdpsd> lsdpsdList, ShipIndicateSaveListData.Ship ship) {
//        Lsdpsd lsdpsd = this.getLsdpsdByOrderIdAndOrderSeq(shipIndicateSaveData);
//        String shipId = jpaMoveService.makeOrderShipData(lsdpsd, shipIndicateSaveData.getQty(), StringFactory.getGbFour());
//        lsdpsdList.add(lsdpsd);
        return null;
    }

    /**
     * 출고지시리스트 화면에서 list를 불러오는 함수
     */
    public ShipIndicateListData getShipList(Date startDt, Date endDt, String shipId, String assortId, String assortNm, String vendorId) {
        ShipIndicateListData shipIndicateListData = new ShipIndicateListData(startDt,endDt,shipId,assortId,assortNm,vendorId);
        TypedQuery<TbOrderDetail> query = em.createQuery("select td from TbOrderDetail td " +
                        "join fetch td.tbOrderMaster tm " +
                        "join fetch tm.itasrt it "+
                        "where tm.orderDate between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or td.assortId=?3) " +
                        "and (?4 is null or trim(?4)='' or it.assortNm is like concat('%', ?4, '%')))"
                ,TbOrderDetail.class);
        List<TbOrderDetail> tbOrderDetailList = query.getResultList();
        List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
        for(TbOrderDetail tbOrderDetail:tbOrderDetailList){
            ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(tbOrderDetail);
            shipList.add(ship);
        }
        shipIndicateListData.setShips(shipList);
        return shipIndicateListData;
    }

    /**
     * shipId 채번 함수
     */
    public String getShipId(){
        return jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm());
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
