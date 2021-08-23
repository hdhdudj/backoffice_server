package io.spring.service.move;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.JpaIfBrandRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.IfBrand;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.move.request.GoodsMoveSaveData;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.request.ShipIdAndSeq;
import io.spring.model.move.response.GoodsModalListResponseData;
import io.spring.model.move.response.OrderMoveListResponseData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaMoveService {
//    private final JpaCommonService jpaCommonService;
    private final JpaIfBrandRepository jpaIfBrandRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;

    private final JpaPurchaseService jpaPurchaseService;

    private final EntityManager em;

    /**
     * 주문 이동지시 대상 리스트 가져오는 함수 
     */
    public List<OrderMoveListResponseData> getOrderMoveList(Map<String, Object> map) {
        LocalDate startDt = (LocalDate)map.get(StringFactory.getStrStartDt());
        LocalDate endDt = (LocalDate)map.get(StringFactory.getStrEndDt());
        String storageId = (String)map.get(StringFactory.getStrStorageId());
        String assortId = (String)map.get(StringFactory.getStrAssortId());
        String assortNm = (String)map.get(StringFactory.getStrAssortNm());
        String itemId = (String)map.get(StringFactory.getStrItemId());
        String deliMethod = (String)map.get(StringFactory.getStrDeliMethod());
        List<Lsdpsd> lsdpsdList = this.getLsdpsd(startDt, endDt, storageId, assortId, assortNm, itemId, deliMethod);
        List<OrderMoveListResponseData> orderMoveListDataListResponse = new ArrayList<>();
        for(Lsdpsd lsdpsd : lsdpsdList){
            OrderMoveListResponseData orderMoveListResponseData = new OrderMoveListResponseData(lsdpsd);
            orderMoveListDataListResponse.add(orderMoveListResponseData);
            List<Itvari> itvariList = lsdpsd.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                orderMoveListResponseData.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                orderMoveListResponseData.setOptionNm2(itvari2.getOptionNm());
            }
        }
        return orderMoveListDataListResponse;
    }

    /**
     * 주문 이동지시 화면에서 검색에 맞는 Lsdpsd들을 가져오는 함수
     */
    private List<Lsdpsd> getLsdpsd(LocalDate startDt, LocalDate endDt, String storageId, String assortId, String assortNm, String itemId, String deliMethod) {
        // lsdpsd, lsdpsm, tbOrderDetail, itasrt, itvari
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        Query query = em.createQuery("select d from Lsdpsd d " +
                "join fetch d.lsdpsm m " +
                "join fetch d.lspchd pd " +
                "join fetch pd.tbOrderDetail t " +
                "join fetch d.itasrt i " +
                "where " +
                "m.depositDt between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or m.storeCd=?3) " +
                "and (?4 is null or trim(?4)='' or d.assortId=?4) " +
                "and (?5 is null or trim(?5)='' or d.itemId=?5) " +
                "and (?6 is null or trim(?6)='' or t.deliMethod=?6) " +
                "and (?7 is null or trim(?7)='' or i.assortNm like concat('%',?7,'%'))"
        );
        query.setParameter(1, start).setParameter(2, end).setParameter(3,storageId)
        .setParameter(4,assortId).setParameter(5,itemId).setParameter(6,deliMethod)
                .setParameter(7,assortNm);
        List<Lsdpsd> lsdpsdList = query.getResultList();
        return lsdpsdList;
    }

    /**
     * 주문 이동지시 저장 함수
     */
    @Transactional
    public List<String> saveOrderMove(List<OrderMoveSaveData> orderMoveSaveDataList) {
        if(orderMoveSaveDataList.size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<String> shipIdList = new ArrayList<>();
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        // 1. 출고 data 생성
        for(OrderMoveSaveData orderMoveSaveData : orderMoveSaveDataList){
            String shipId = this.saveOrderMoveSaveData(lsdpsdList, orderMoveSaveData);
            shipIdList.add(shipId);
        }
        // 2. 발주 data 생성
        jpaPurchaseService.makePurchaseDataFromOrderMoveSave(lsdpsdList, orderMoveSaveDataList);
        return shipIdList;
    }

    /**
     * OrderMoveSaveData객체로 lsshpm,s,d 생성
     * lsdpsm,d,s,b, lsdpsp, ititmt(발주데이터) 생성
     * tbOrderDetail를 변경
     */
    private String saveOrderMoveSaveData(List<Lsdpsd> lsdpsdList, OrderMoveSaveData orderMoveSaveData) {
        Lsdpsd lsdpsd = this.getLsdpsdByDepositNoAndDepositSeq(orderMoveSaveData);
        String shipId = this.makeOrderShipData(lsdpsd, orderMoveSaveData.getQty(), StringFactory.getGbOne());
        lsdpsdList.add(lsdpsd);
//        this.updateQty(orderMoveSaveData);
        return shipId;
    }

    /**
     * depositNo와 depositSeq로 Lsdpsd를 가져오는 함수
     */
    private Lsdpsd getLsdpsdByDepositNoAndDepositSeq(OrderMoveSaveData orderMoveSaveData) {
        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d " +
//                "join fetch d.lsdpsp lp " +
//                "join fetch d.lsdpsm lm " +
//                "join fetch d.ititmm tm " +
//                "join fetch d.itasrt it " +
//                "join fetch tm.ititmc ic " +
//                "join fetch lp.tbOrderDetail t " +
                        "where " +
                        "d.depositNo=?1 and d.depositSeq=?2"
                , Lsdpsd.class);
        query.setParameter(1, orderMoveSaveData.getDepositNo())
                .setParameter(2, orderMoveSaveData.getDepositSeq());
        Lsdpsd lsdpsd = query.getSingleResult();
        return lsdpsd;
    }

    /**
     * 주문이동 저장, 출고 관련 data 생성 함수 (lsshpm,d,s)
     */
    private String makeOrderShipData(Lsdpsd lsdpsd, long qty, String shipStatus) {
        String shipId = getShipId();

        Itasrt itasrt = lsdpsd.getItasrt();
        List<Ititmc> ititmcList = lsdpsd.getItitmm().getItitmc();
        LocalDateTime depositDt = lsdpsd.getLsdpsm().getDepositDt();
        String storageId = lsdpsd.getLsdpsm().getStoreCd();
        String itemGrade = lsdpsd.getItemGrade();
        ititmcList = ititmcList.stream().filter(x -> x.getEffEndDt().equals(depositDt)
                && x.getStorageId().equals(storageId)
                && x.getItemGrade().equals(itemGrade)).collect(Collectors.toList());
        Ititmc ititmc = ititmcList.get(0);
        // ititmc에서 shipIndicateQty 변경해주기

        ititmc.setShipIndicateQty(ititmc.getShipIndicateQty() + qty);
        jpaItitmcRepository.save(ititmc);
        TbOrderMaster tbOrderMaster = lsdpsd.getLspchd().getLsdpsp().get(0).getTbOrderDetail().getTbOrderMaster();


        for (int i = 0; i < qty; i++) {
            TbOrderDetail tbOrderDetail = lsdpsd.getLspchd().getLsdpsp().get(i).getTbOrderDetail();
//            Lsdpsp lsdpsp = lsdpsd.getLspchd().getLsdpsp().get(i);
            if(i==0){
                // lsshpm 저장
                Lsshpm lsshpm = new Lsshpm(shipId, itasrt, tbOrderDetail);
                lsshpm.setShipStatus(shipStatus); // 01 : 이동지시, 04 : 출고
                // lsshps 저장
                Lsshps lsshps = new Lsshps(lsshpm);
                jpaLsshpsRepository.save(lsshps);
                jpaLsshpmRepository.save(lsshpm);
            }
            // lsshpd 저장
            String shipSeq = StringUtils.leftPad(Integer.toString(i + 1), 4,'0');
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
            lsshpd.setShipIndicateQty(1l);
            jpaLsshpdRepository.save(lsshpd);
        }
        return shipId;
    }

    /**
     * 상품 선택창 : 물류센터, 구매처, 상품, 상품명을 입력한 후 검색 버튼을 눌렀을 때 상품 목록을 반환
     */
    public GoodsModalListResponseData getGoodsList(String storeCd, String purchaseVendorId, String assortId, String assortNm) {
        List<Ititmc> ititmcList = this.getItitmc(storeCd, purchaseVendorId, assortId, assortNm);
        List<GoodsModalListResponseData.Goods> goodsList = new ArrayList<>();
        GoodsModalListResponseData goodsModalListResponseData = new GoodsModalListResponseData(storeCd, purchaseVendorId, assortId, assortNm);
        for(Ititmc ititmc : ititmcList){
            Itasrt itasrt = ititmc.getItasrt();
            GoodsModalListResponseData.Goods goods = new GoodsModalListResponseData.Goods(ititmc, itasrt);
            IfBrand ifBrand = jpaIfBrandRepository.findByChannelGbAndChannelBrandId(StringFactory.getGbOne(), itasrt.getBrandId()); // 채널은 01 하드코딩
            List<Itvari> itvariList = itasrt.getItvariList();
            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(),ititmc.getItemId())
                    .stream().filter(x->x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
            long qtyOfC01 = tbOrderDetailList.size();
            goods.setOrderQty(qtyOfC01);
            goods.setAvailableQty(goods.getAvailableQty() - qtyOfC01);
            goods.setStoreCd(goodsModalListResponseData.getStoreCd());
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                goods.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                goods.setOptionNm2(itvari2.getOptionNm());
            }
            goods.setBrandNm(ifBrand.getBrandNm());
            goodsList.add(goods);
        }
        goodsModalListResponseData.setGoods(goodsList);
        return goodsModalListResponseData;
    }

//    /**
//     * 상품 이동지시 : 이동지시일자, 출고센터, 입고센터, 이동방법을 입력한 후 상품추가 버튼을 눌렀을 때 상품 목록을 반환
//     */
//    public List<Ititmc> getItitmcList(String storeCd, String purchaseVendorId, String assortId, String assortNm) {
//        List<Ititmc> ititmcList = this.getItitmc(storeCd, purchaseVendorId, assortId, assortNm);
//        List<GoodsMoveListResponseData> goodsMoveListDataListResponse = new ArrayList<>();
//        for(Ititmc ititmc : ititmcList){
//            GoodsMoveListResponseData goodsMoveListResponseData = new GoodsMoveListResponseData(ititmc);
//            goodsMoveListDataListResponse.add(goodsMoveListResponseData);
//        }
//        return goodsMoveListDataListResponse;
//    }

//    /**
//     * 상품 이동지시 : 이동지시일자, 출고센터, 입고센터, 이동방법을 입력한 후 상품추가 버튼을 눌렀을 때 상품 목록을 반환
//     */
//    public List<GoodsMoveListResponseData> getGoodsMoveList(LocalDate shipIndDt, String storeCd, String oStoreCd, String deliMethod) {
//        List<Ititmc> ititmcList = this.getItitmc(shipIndDt, oStoreCd, deliMethod);
//        List<GoodsMoveListResponseData> goodsMoveListDataListResponse = new ArrayList<>();
//        for(Ititmc ititmc : ititmcList){
//            GoodsMoveListResponseData goodsMoveListResponseData = new GoodsMoveListResponseData(ititmc);
//            goodsMoveListDataListResponse.add(goodsMoveListResponseData);
//        }
//        return goodsMoveListDataListResponse;
//    }

    /**
     * 상품이동지시 화면에서 검색에 맞는 Ititmc들을 가져오는 함수
     */
    private List<Ititmc> getItitmc(String storeCd, String purchaseVendorId, String assortId, String assortNm) {
        Query query = em.createQuery("select ic from Ititmc ic " +
                "join fetch ic.itasrt it " +
                "where " +
                "(?1 is null or trim(?1)='' or ic.storageId=?1) " +
                "and (?2 is null or trim(?2)='' or ic.vendorId=?2) " +
                "and (?3 is null or trim(?3)='' or ic.assortId=?3) " +
                "and (?4 is null or trim(?4)='' or it.assortNm like concat('%',?4,'%'))"
        );
        query.setParameter(1,storeCd).setParameter(2,purchaseVendorId).setParameter(3,assortId)
        .setParameter(4,assortNm);
        List<Ititmc> ititmcList = query.getResultList();
        return ititmcList;
    }

    /**
     * 상품 이동지시 저장 함수
     */
    @Transactional
    public String saveGoodsMove(GoodsMoveSaveData goodsMoveSaveData) {
        List<GoodsMoveSaveData.Goods> goodsList = goodsMoveSaveData.getGoods();
        List<GoodsMoveSaveData.Goods> newGoodsList = new ArrayList<>();
        // shipSeq 순서 저장용 list
        List<Integer> indexStore = new ArrayList<>();
        indexStore.add(1);
        // 1. 출고 data 생성
        // 1-1. Lsshpm 생성
        String shipId = this.getShipId();
        Lsshpm lsshpm = new Lsshpm(shipId);
        lsshpm.setStorageId(goodsMoveSaveData.getStoreCd());
        lsshpm.setDelMethod(goodsMoveSaveData.getDeliMethod());
        lsshpm.setReceiptDt(goodsMoveSaveData.getShipIndDt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        long lsshpdNum = 0l;
        for (int i = 0; i < goodsList.size() ; i++) {
            lsshpdNum = this.saveGoodsMoveSaveData(shipId, goodsMoveSaveData, goodsList.get(i), indexStore, newGoodsList);
        }
        // 1-3. Lsshps 생성
        Lsshps lsshps = new Lsshps(lsshpm);
        if(lsshpdNum <= 0){
            return null;
        }
        jpaLsshpmRepository.save(lsshpm);
        jpaLsshpsRepository.save(lsshps);

        // 3. 발주 data 생성
        jpaPurchaseService.makePurchaseDataFromGoodsMoveSave(lsshpm.getRegId(), goodsMoveSaveData, newGoodsList);

        return shipId;
    }

    /**
     * 출고 data 생성 함수
     */
    private long saveGoodsMoveSaveData(String shipId, GoodsMoveSaveData goodsMoveSaveData, GoodsMoveSaveData.Goods goods, List<Integer> indexStore, List<GoodsMoveSaveData.Goods> newGoodsList) {
        GoodsMoveSaveData.Goods rowGoods = this.getItitmcByCondition(goods, newGoodsList);
        long lsshpdNum = this.makeGoodsShipData(shipId, rowGoods, goodsMoveSaveData, indexStore);
//        ititmcList.add(ititmc);
//        this.updateQty(goods);
        return lsshpdNum;
    }

    /**
     * 조건에 맞는 ititmc를 찾아서 반환.
     * 조건 : assortId, itemId
     */
    private GoodsMoveSaveData.Goods getItitmcByCondition(GoodsMoveSaveData.Goods goods, List<GoodsMoveSaveData.Goods> newGoodsList) {
        TypedQuery<Ititmc> query = em.createQuery("select ic from Ititmc ic " +
//                "join fetch d.lsdpsp lp " +
//                "join fetch d.lsdpsm lm " +
//                "join fetch d.ititmm tm " +
//                "join fetch d.itasrt it " +
//                "join fetch tm.ititmc ic " +
//                "join fetch lp.tbOrderDetail t " +
                        "where " +
                        "ic.assortId=?1 and ic.itemId=?2 order by ic.effEndDt asc"
                , Ititmc.class);
        query.setParameter(1, goods.getAssortId())
                .setParameter(2, goods.getItemId());
        List<Ititmc> ititmcList = query.getResultList();

        // 2. ititmc qty값 변경
        ititmcList = this.calcItitmcQty(ititmcList, goods.getShipQty());
        if(ititmcList.size() > 0){
            newGoodsList.add(goods);
        }

        GoodsMoveSaveData.Goods rowGoods = this.makeItitmcsToOneRow(ititmcList, goods);
        return rowGoods;
    }

    /**
     * 상품이동지시 저장시 ititmc의 qty 값을 변경 (출고에서도 사용)
     */
    public List<Ititmc> calcItitmcQty(List<Ititmc> ititmcList, long shipQty) {
        List<Ititmc> newItitmcList = new ArrayList<>();
        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
        if(ititmcQty - ititmcShipIndQty < shipQty){
            return newItitmcList;
        }
        for(Ititmc ititmc : ititmcList){
            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc 재고량
            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc 출고예정량
            long canShipQty = qty - shipIndQty; // 출고가능량
            if(canShipQty <= 0){ // 출고 불가
                continue;
            }
            if(shipQty <= canShipQty){ // 이 차례에서 출고 완료 가능
                ititmc.setShipIndicateQty(shipIndQty + shipQty);
                jpaItitmcRepository.save(ititmc);
                newItitmcList.add(ititmc);
                break;
            }
//            else{ // 이 차례에선 출고 풀로 했는데 아직도 출고해야 할 양이 남음
//                shipQty -= canShipQty;
//                ititmc.setShipIndicateQty(qty);
//                jpaItitmcRepository.save(ititmc);
//            }
        }
        return newItitmcList;
    }

    /**
     * ititmc 리스트를 받아 상품이동지시 화면의 한 줄에 해당하는 객체로 만드는 함수
     */
    private GoodsMoveSaveData.Goods makeItitmcsToOneRow(List<Ititmc> ititmcList, GoodsMoveSaveData.Goods goods) {
        if(ititmcList.size() == 0){
            log.debug("입력 이동지시수량이 유효값보다 큽니다.");
            return null;
        }
        Ititmc ititmc = ititmcList.get(0);
        GoodsMoveSaveData.Goods goodsRow = new GoodsMoveSaveData.Goods(ititmc);
        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
        long orderQty = 0l;
        for(Ititmc item : ititmcList){
            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(), ititmc.getItemId());
            orderQty += tbOrderDetailList.stream().map(x-> {
                if (x.getQty() == null) {
                    return 0l;
                } else {
                    return x.getQty();
                }
            }).reduce((a,b)->a+b).get();

            Itasrt itasrt = item.getItasrt();
            goodsRow.setAssortNm(itasrt.getAssortNm());
            goodsRow.setOptionNm(itasrt.getItvariList().get(0).getOptionNm());
        }
        goodsRow.setAvailableQty(ititmcQty - ititmcShipIndQty);
        goodsRow.setOrderQty(orderQty);
        goodsRow.setShipQty(goods.getShipQty());

        return goodsRow;
    }

    /**
     * ititmc list의 shipIndQty를 다 더해서 반환하는 함수 (move와 ship에서 사용)
     */
    public long getItitmcShipIndQtyByStream(List<Ititmc> ititmcList){
        return ititmcList.stream().map(x-> {
            if (x.getShipIndicateQty() == null) {
                return 0l;
            } else {
                return x.getShipIndicateQty();
            }}).reduce((a,b)->a+b).get();
    }

    /**
     * ititmc list의 qty를 다 더해서 반환하는 함수 (move와 ship에서 사용)
     */
    public long getItitmcQtyByStream(List<Ititmc> ititmcList){
        return ititmcList.stream().map(x-> {
            if (x.getQty() == null) {
                return 0l;
            } else {
                return x.getQty();
            }}).reduce((a,b)->a+b).get();
    }
    /**
     * goods 정보를 받아 입고 data (lsshpd) 생성하는 함수
     */
    private long makeGoodsShipData(String shipId, GoodsMoveSaveData.Goods goods, GoodsMoveSaveData goodsMoveSaveData, List<Integer> indexStore) {
        if(goods ==  null){
            log.debug("this row don't save.");
            return 0l;
        }
        int index = indexStore.get(0);
        long shipQty = goods.getShipQty();
        for (long i = 0; i < shipQty ; i++) {
            String shipSeq = StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd 생성
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, goodsMoveSaveData, goods);
            jpaLsshpdRepository.save(lsshpd);
            index++;
            indexStore.remove(0);
            indexStore.add(index);
        }
        return shipQty;
    }

    /**
     * 이동처리(lsshpm.shipStats를 01에서 04로 변경)
     */
    public List<String> changeShipStatus(List<ShipIdAndSeq> shipIdAndSeqList) {
        List<String> newShipIdList = new ArrayList<>();
        List<String> shipIdList = new ArrayList<>();
        shipIdAndSeqList.stream().forEach(x->shipIdList.add(x.getShipId()));
        Set<String> shipNoSet = new HashSet(shipIdList);

        for(String shipId : shipNoSet){
            Lsshpm lsshpm = jpaLsshpmRepository.findByShipId(shipId);
            if(lsshpm == null){
                log.debug("there's no data(lsshpm) of shipId - " + shipId);
                continue;
            }
            else{
                lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 하드코딩

                newShipIdList.add(lsshpm.getShipId());
                jpaLsshpmRepository.save(lsshpm);
            }
        }
        return newShipIdList;
    }

    /**
     * shipId 채번 함수
     */
    public String getShipId(){
        String shipId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm());
        shipId = Utilities.getStringNo('L',shipId,9);
        return shipId;
    }
}
