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
import io.spring.model.move.request.MoveListSaveData;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.*;
import io.spring.model.order.entity.TbOrderDetail;
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
        List<TbOrderDetail> tbOrderDetailList = this.getTbOrderDetail(startDt, endDt, storageId, assortId, assortNm, itemId, deliMethod);
//        List<Lsdpsd> lsdpsdList = this.getLsdpsd(startDt, endDt, storageId, assortId, assortNm, itemId, deliMethod);
        List<OrderMoveListResponseData> orderMoveListDataListResponse = new ArrayList<>();
        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
            OrderMoveListResponseData orderMoveListResponseData = new OrderMoveListResponseData(tbOrderDetail);
            orderMoveListDataListResponse.add(orderMoveListResponseData);
            List<Itvari> itvariList = tbOrderDetail.getItasrt().getItvariList();
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
     * 주문 이동지시 화면에서 검색에 맞는 TbOrderDetail들을 가져오는 함수
     */
    private List<TbOrderDetail> getTbOrderDetail(LocalDate startDt, LocalDate endDt, String storageId, String assortId, String assortNm, String itemId, String deliMethod) {
        // lsdpsd, lsdpsm, tbOrderDetail, itasrt, itvari
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        TypedQuery<TbOrderDetail> query = em.createQuery("select to from TbOrderDetail to " +
                "join fetch to.lspchd pd " +
                "join fetch pd.lsdpsd sd " +
                "join fetch sd.lsdpsm sm " +
                "join fetch to.itasrt i " +
                "where " +
                "to.regDt between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or to.storageId=?3) " +
                "and (?4 is null or trim(?4)='' or to.assortId=?4) " +
                "and (?5 is null or trim(?5)='' or to.itemId=?5) " +
                "and (?6 is null or trim(?6)='' or to.deliMethod=?6) " +
                "and (?7 is null or trim(?7)='' or i.assortNm like concat('%',?7,'%'))"
        , TbOrderDetail.class);
        query.setParameter(1, start).setParameter(2, end).setParameter(3,storageId)
                .setParameter(4,assortId).setParameter(5,itemId).setParameter(6,deliMethod)
                .setParameter(7,assortNm);
        List<TbOrderDetail> tbOrderDetailList = query.getResultList();
        return tbOrderDetailList;
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
    public List<String> saveOrderMove(OrderMoveSaveData orderMoveSaveData) {
        List<OrderMoveSaveData.Move> moveList = orderMoveSaveData.getMoves();
        if(moveList.size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<String> newShipIdList = new ArrayList<>();
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        // 1. 출고 data 생성
        for(OrderMoveSaveData.Move move : moveList){
            List<String> shipIdList = this.saveOrderMoveSaveData(lsdpsdList, move);
            if(shipIdList.size() > 0){
                shipIdList.stream().forEach(x->newShipIdList.add(x));
            }
        }
        // 2. 발주 data 생성
        jpaPurchaseService.makePurchaseDataFromOrderMoveSave(lsdpsdList, moveList);
        return newShipIdList;
    }

    /**
     * OrderMoveSaveData객체로 lsshpm,s,d 생성
     * lsdpsm,d,s,b, lsdpsp, ititmt(발주데이터) 생성
     * tbOrderDetail를 변경
     */
    private List<String> saveOrderMoveSaveData(List<Lsdpsd> lsdpsdList, OrderMoveSaveData.Move move) {
        Lsdpsd lsdpsd = this.getLsdpsdByDepositNoAndDepositSeq(move);
        TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(move.getOrderId(),move.getOrderSeq());
        List<String> shipIdList = this.makeOrderShipData(lsdpsd, tbOrderDetail, move.getQty(), StringFactory.getGbOne());
        if(shipIdList.size() > 0){
            lsdpsdList.add(lsdpsd);
        }
//        this.updateQty(orderMoveSaveData);
        return shipIdList;
    }

    /**
     * depositNo와 depositSeq로 Lsdpsd를 가져오는 함수
     */
    private Lsdpsd getLsdpsdByDepositNoAndDepositSeq(OrderMoveSaveData.Move move) {
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
        query.setParameter(1, move.getDepositNo())
                .setParameter(2, move.getDepositSeq());
        Lsdpsd lsdpsd = query.getSingleResult();
        return lsdpsd;
    }

    /**
     * 주문이동 저장, 출고 관련 data 생성 함수 (lsshpm,d,s)
     */
    private List<String> makeOrderShipData(Lsdpsd lsdpsd, TbOrderDetail tbOrderDetail, long qty, String shipStatus) {
        List<String> shipIdList = new ArrayList<>();

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
        if(qty > ititmc.getQty() - ititmc.getShipIndicateQty()){
            log.debug("이동가능 재고량이 부족합니다.");
            return null;
        }
        ititmc.setShipIndicateQty(ititmc.getShipIndicateQty() + qty);
        jpaItitmcRepository.save(ititmc);
//        TbOrderMaster tbOrderMaster = lsdpsd.getLspchd().getLsdpsp().get(0).getTbOrderDetail().getTbOrderMaster();

        for (int i = 0; i < qty; i++) {
            String shipId = getShipId();
//            Lsdpsp lsdpsp = lsdpsd.getLspchd().getLsdpsp().get(i);
            // lsshpm 저장
            Lsshpm lsshpm = new Lsshpm(shipId, itasrt, tbOrderDetail);
            lsshpm.setShipStatus(shipStatus); // 01 : 이동지시, 04 : 출고
            // lsshps 저장
            Lsshps lsshps = new Lsshps(lsshpm);
            jpaLsshpsRepository.save(lsshps);
            jpaLsshpmRepository.save(lsshpm);
            // lsshpd 저장
            String shipSeq = StringFactory.getFourStartCd(); // 0001 하드코딩 //StringUtils.leftPad(Integer.toString(i + 1), 4,'0');
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
            lsshpd.setShipIndicateQty(1l);
            jpaLsshpdRepository.save(lsshpd);
            shipIdList.add(shipId);
        }
        return shipIdList;
    }

    /**
     * 상품 선택창 : 입고처리 상품추가 모달에서 상품을 선택하고 확인을 눌렀을 때 리스트를 반환
     */
    public GoodsModalListResponseData getGoodsList(String storageId, String purchaseVendorId, String assortId, String assortNm) {
        List<Ititmc> ititmcList = this.getItitmc(storageId, purchaseVendorId, assortId, assortNm);
        List<GoodsModalListResponseData.Goods> goodsList = new ArrayList<>();
        GoodsModalListResponseData goodsModalListResponseData = new GoodsModalListResponseData(storageId, purchaseVendorId, assortId, assortNm);
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
//            goods.setStoreCd(goodsModalListResponseData.getStoreCd());
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                goods.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                goods.setOptionNm2(itvari2.getOptionNm());
            }
            if(ifBrand != null){
                goods.setBrandNm(ifBrand.getBrandNm());
            }
            goodsList.add(goods);
        }
//        goodsList = this.removeDuplicate(goodsList); // goodsKey로 group by
        goodsModalListResponseData.setGoods(goodsList);
        return goodsModalListResponseData;
    }

//    /**
//     * goodsKey로 goodsList를 그루핑하는 함수 (이동가능수량과 해외입고완료 주문수량을 goodsKey별로 더함)
//     * @param goodsList 상품이동지시 화면에 보일 goodsKey로 나뉜 row의 리스트
//     * @return goodsKey로 그루핑 처리된 goodsList
//     */
//    private List<GoodsModalListResponseData.Goods> removeDuplicate(List<GoodsModalListResponseData.Goods> goodsList) {
//        List<GoodsModalListResponseData.Goods> newGoodsList = new ArrayList<>();
//        Map<String, GoodsModalListResponseData.Goods> goodsMap = new HashMap<>();
//        for(GoodsModalListResponseData.Goods goods : goodsList){
//            GoodsModalListResponseData.Goods goods1 = goodsMap.get(goods.getGoodsKey());
//            if(goods1 == null){
//                newGoodsList.add(goods);
//            }
//            else{
//                goods1.setAvailableQty(goods1.getAvailableQty() + goods.getAvailableQty());
//                goods1.setOrderQty(goods1.getOrderQty() + goods.getOrderQty());
//            }
//        }
//        return newGoodsList;
//    }

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
    private List<Ititmc> getItitmc(String storageId, String purchaseVendorId, String assortId, String assortNm) {
        Query query = em.createQuery("select ic from Ititmc ic " +
                "join fetch ic.itasrt it " +
                "where " +
                "(?1 is null or trim(?1)='' or ic.storageId=?1) " +
                "and (?2 is null or trim(?2)='' or it.vendorId=?2) " +
                "and (?3 is null or trim(?3)='' or ic.assortId=?3) " +
                "and (?4 is null or trim(?4)='' or it.assortNm like concat('%',?4,'%'))"
        );
        query.setParameter(1,storageId).setParameter(2,purchaseVendorId).setParameter(3,assortId)
        .setParameter(4,assortNm);
        List<Ititmc> ititmcList = query.getResultList();
        return ititmcList;
    }

    /**
     * 상품이동지시 저장 함수
     */
    @Transactional
    public List<String> saveGoodsMove(GoodsMoveSaveData goodsMoveSaveData) {
        List<String> shipIdList = new ArrayList<>();

        String regId = null;
        LocalDateTime purchaseDt = null;

        for (GoodsMoveSaveData.Goods goods : goodsMoveSaveData.getGoods()) {
            regId = goodsMoveSaveData.getUserId();

            long moveQty = goods.getMoveQty();
            // 1. 출고 data 생성
            Ititmc ititmc = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(goods.getAssortId(), goods.getItemId(),
                    goods.getStorageId(), StringFactory.getStrEleven(), Utilities.dateToLocalDateTime(goods.getDepositDt()));
            List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository.findByAssortIdAndItemId(ititmc.getAssortId(),ititmc.getItemId())
                .stream().filter(x->x.getStatusCd().equals(StringFactory.getStrC01())).collect(Collectors.toList());
            long qtyOfC01 = tbOrderDetailList.size();
            long qty = ititmc.getQty() == null? 0l:ititmc.getQty();
            long shipIndicateQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty();
            if(goods.getMoveQty() > qty - shipIndicateQty - qtyOfC01){
                log.debug("입력량이 이동가능량보다 큽니다.");
                continue;
            }
            if(goods.getMoveQty() == 0){
                log.debug("입력량이 0이어서 저장되지 않습니다.");
                continue;
            }
            // 1-0. Lsshpm 생성
            String shipId = this.getShipId();
            Lsshpm lsshpm = new Lsshpm(shipId, goodsMoveSaveData);
            purchaseDt = lsshpm.getReceiptDt();
            // 1-1. ititmc 값 변경
            if(lsshpm != null){
                ititmc.setShipIndicateQty(shipIndicateQty + moveQty);
                ititmc.setUpdId(goodsMoveSaveData.getUserId());
                jpaItitmcRepository.save(ititmc);
            }
            String shipSeq = StringFactory.getFourStartCd(); // 0001 하드코딩 //StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd 생성
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, ititmc, goods, regId);
            lsshpd.setOStorageId(goodsMoveSaveData.getOStorageId());
            lsshpd.setShipIndicateQty(moveQty);
            lsshpm.setChannelId(goods.getChannelId()); // vendorId는 바깥에서 set
            jpaLsshpdRepository.save(lsshpd);
            // 1-3. Lsshps 생성
            Lsshps lsshps = new Lsshps(lsshpm, regId);
            jpaLsshpmRepository.save(lsshpm);
            jpaLsshpsRepository.save(lsshps);

            shipIdList.add(shipId);

            // 2. 발주 data 생성
            jpaPurchaseService.makePurchaseDataFromGoodsMoveSave(regId, purchaseDt, lsshpm, lsshpd);
//            List<Lsdpsp> lsdpspList = new ArrayList<>();
//            lsdpspList.add(lsdpsp);

            // 3. purchaseStatus 변경
//            jpaPurchaseService.changePurchaseStatus(lsdpspList);
        }

        return shipIdList;
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
        ititmcList = this.calcItitmcQty(ititmcList, goods.getMoveQty());
        if(ititmcList.size() > 0){
            newGoodsList.add(goods);
        }
        else{
            log.debug("이동할 수 있는 재고가 존재하지 않습니다.");
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
                log.debug("출고 또는 이동이 불가합니다.");
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
        goodsRow.setMoveQty(goods.getMoveQty());

        return goodsRow;
    }

    /**
     * goods 정보를 받아 출고 data (lsshpd) 생성하는 함수
     */
    private long makeGoodsShipData(String shipId, GoodsMoveSaveData.Goods goods, GoodsMoveSaveData goodsMoveSaveData, List<Integer> indexStore) {
        if(goods ==  null){
            log.debug("this row don't save.");
            return 0l;
        }
        int index = indexStore.get(0);
        long moveQty = goods.getMoveQty();
        for (long i = 0; i < moveQty ; i++) {
            String shipSeq = StringUtils.leftPad(Integer.toString(index),4,'0');
            // 1-2. Lsshpd 생성
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, null, goods, goodsMoveSaveData.getUserId());
            jpaLsshpdRepository.save(lsshpd);
            index++;
            indexStore.remove(0);
            indexStore.add(index);
        }
        return moveQty;
    }

    /**
     * 이동처리(lsshpm.shipStatus를 01에서 04로 변경)
     */
    @Transactional
    public List<String> changeShipStatus(MoveListSaveData moveListSaveData) {
        List<String> newShipIdList = new ArrayList<>();
        List<String> shipIdList = new ArrayList<>();
        List<MoveListSaveData.Move> moveList = moveListSaveData.getMoves();
        moveList.stream().forEach(x->shipIdList.add(x.getShipId()));
        Set<String> shipNoSet = new HashSet(shipIdList);

        // lss- 변경
        for(String shipId : shipNoSet){
            Lsshpd lsshpd = jpaLsshpdRepository.findByShipId(shipId).get(0);
            Lsshpm lsshpm = lsshpd.getLsshpm();
            // ititmc.shipIndicateQty, ititmc.shipQty 차감
            List<Ititmc> ititmcList = jpaItitmcRepository.findByAssortIdAndItemIdAndEffEndDtOrderByEffEndDtAsc(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpd.getExcAppDt());
            if(this.subItitmcQties(ititmcList, lsshpd.getShipIndicateQty()).size() == 0){
                continue;
            }
            if(lsshpm == null){
                log.debug("there's no data(lsshpm) of shipId : " + shipId);
                continue;
            }
            else{
                lsshpd.setShipQty(lsshpd.getShipIndicateQty());
                lsshpm.setShipStatus(StringFactory.getGbFour()); // 04 하드코딩
                lsshpm.setApplyDay(LocalDateTime.now()); // 출고일자 now date
                newShipIdList.add(lsshpm.getShipId());
            }
            this.updateLssSeries(lsshpd);
        }
        return newShipIdList;
    }

    /**
     * 이동지시리스트를 가져오는 함수
     * @param startDt
     * @param endDt
     * @param storageId
     * @param assortId
     * @param assortNm
     * @return 이동지시리스트를 가진 DTO
     */
    public MoveIndicateListResponseData getMoveIndicateList(LocalDate startDt, LocalDate endDt, String storageId, String oStorageId, String assortId, String assortNm) {
        List<Lsshpd> lsshpdList = this.getLsshpdMoveIndList(startDt, endDt, storageId, oStorageId, assortId, assortNm);
        MoveIndicateListResponseData moveIndicateListResponseData = new MoveIndicateListResponseData(startDt, endDt, storageId, oStorageId, assortId, assortNm);
        List<MoveIndicateListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            moveIndicateListResponseData.setOStorageId(lsshpm.getOStorageId());
            // lsshpm의 shipStatus를 확인 (01 : 출고지시or이동지시, 04 : 출고)
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){
                continue;
            }
            MoveIndicateListResponseData.Move move = new MoveIndicateListResponseData.Move(lsshpd);
            List<Itvari> itvariList = lsshpd.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                move.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                move.setOptionNm2(itvari2.getOptionNm());
            }
            moveList.add(move);
        }
        moveIndicateListResponseData.setMoves(moveList);
        return moveIndicateListResponseData;
    }

    /**
     * 조건에 맞는 lsshpd 리스트를 반환하는 함수
    */
    private List<Lsshpd> getLsshpdMoveIndList(LocalDate startDt, LocalDate endDt, String storageId, String oStorageId, String assortId, String assortNm) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        TypedQuery<Lsshpd> query = em.createQuery("select ld from Lsshpd ld " +
                        "join fetch ld.lsshpm lm " +
                        "left join fetch ld.tbOrderDetail td " +
                        "join fetch ld.itasrt it " +
                        "where ld.regDt between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or lm.storageId=?3) " +
                        "and (?4 is null or trim(?4)='' or ld.assortId=?4) " +
                        "and (?5 is null or trim(?5)='' or it.assortNm like concat('%',?5,'%')) " +
                        "and (?6 is null or trim(?6)='' or ld.oStorageId=?6)"
                ,Lsshpd.class);
        query.setParameter(1,start).setParameter(2,end).setParameter(3,storageId)
        .setParameter(4,assortId).setParameter(5,assortNm).setParameter(6,oStorageId);
        List<Lsshpd> lsshpdList = query.getResultList();
        return lsshpdList;
    }

    /**
     * 이동지시번호로 이동지시내역 리스트를 가져오는 함수
     * @param shipId
     * @return 이동지시내역 DTO
     */
    public MoveIndicateDetailResponseData getMoveIndicateDetail(String shipId) {
        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findByShipId(shipId);
        // 03 : 주문이동지시, 04 : 상품이동지시인 애들만 남겨둠
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("해당 이동지시내역이 존재하지 않습니다.");
            return null;
        }
        Lsshpd lsshpdOne = lsshpdList.get(0);
        Lsshpm lsshpm = lsshpdOne.getLsshpm();
        MoveIndicateDetailResponseData moveIndicateDetailResponseData = new MoveIndicateDetailResponseData(lsshpm);
        moveIndicateDetailResponseData.setOStorageId(lsshpdOne.getOStorageId());
        moveIndicateDetailResponseData.setDealtypeCd(lsshpdOne.getShipGb()); // 이동지시구분

        List<MoveIndicateDetailResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            MoveIndicateDetailResponseData.Move move = new MoveIndicateDetailResponseData.Move(lsshpd);
            move.setDeliMethod(lsshpm.getDelMethod());
            Utilities.setOptionNames(move,lsshpd.getItasrt().getItvariList());
            List<Itvari> itvariList = lsshpd.getItasrt().getItvariList();
//            if(itvariList.size() > 0){
//                Itvari itvari1 = itvariList.get(0);
//                move.setOptionNm1(itvari1.getOptionNm());
//            }
//            if(itvariList.size() > 1){
//                Itvari itvari2 = itvariList.get(1);
//                move.setOptionNm2(itvari2.getOptionNm());
//            }
            moveList.add(move);
        }
        moveIndicateDetailResponseData.setMoves(moveList);
        return moveIndicateDetailResponseData;
    }

    /**
     * 이동처리 화면 조회시 이동지시 목록을 반환해주는 함수
     * @return 이동지시 목록 반환 DTO
     */
    public MoveListResponseData getMoveList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod) {
        List<Lsshpd> lsshpdList = this.getLsshpdMoveList(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod);
        // 03 : 주문이동지시, 04 : 상품이동지시인 애들만 남겨둠
        lsshpdList = lsshpdList.stream().filter(x->x.getShipGb().equals(StringFactory.getGbThree())||x.getShipGb().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size()==0){
            log.debug("조건에 맞는 이동지시 데이터가 존재하지 않습니다.");
            return null;
        }
        MoveListResponseData moveListResponseData = new MoveListResponseData(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod);
        List<MoveListResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // 출고지시or이동지시(01)가 아니고 출고(04)면 패스
                continue;
            }
            MoveListResponseData.Move move = new MoveListResponseData.Move(lsshpm, lsshpd);
            List<Itvari> itvariList = lsshpd.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                move.setOptionNm1(itvariList.get(0).getOptionNm());
            }
            if(itvariList.size() > 1){
                move.setOptionNm2(itvariList.get(1).getOptionNm());
            }
            moveList.add(move);
        }
        moveListResponseData.setMoves(moveList);
        return moveListResponseData;
    }

    /**
     * 조건에 맞는 lsshpd의 리스트를 반환
     * @param startDt
     * @param endDt
     * @param shipId
     * @param assortId
     * @param assortNm
     * @param storageId
     * @param deliMethod
     * @return
     */
    private List<Lsshpd> getLsshpdMoveList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId, String deliMethod) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        TypedQuery<Lsshpd> query = em.createQuery("select ld from Lsshpd ld " +
                        "join fetch ld.lsshpm lm " +
                        "left join fetch ld.tbOrderDetail td " +
                        "join fetch ld.itasrt it " +
                        "where lm.receiptDt between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or ld.shipId=?3) " +
                        "and (?4 is null or trim(?4)='' or ld.assortId=?4) " +
                        "and (?5 is null or trim(?5)='' or it.assortNm like concat('%',?5,'%')) " +
                        "and (?6 is null or trim(?6)='' or ld.oStorageId=?6) " +
                        "and (?7 is null or trim(?7)='' or lm.delMethod=?7)"
                ,Lsshpd.class);
        query.setParameter(1,start).setParameter(2,end).setParameter(3,shipId)
                .setParameter(4,assortId).setParameter(5,assortNm)
                .setParameter(6,storageId).setParameter(7,deliMethod);
        List<Lsshpd> lsshpdList = query.getResultList();
        return lsshpdList;
    }

    /**
     * 이동리스트 조회
     * @param startDt
     * @param endDt
     * @param shipId
     * @param assortId
     * @param assortNm
     * @param storageId
     * @return 이동리스트 조회 리스트 DTO 반환
     */
    public MoveCompletedLIstReponseData getMovedList(LocalDate startDt, LocalDate endDt, String shipId, String assortId, String assortNm, String storageId) {
        MoveCompletedLIstReponseData moveCompletedLIstReponseData = new MoveCompletedLIstReponseData(startDt, endDt, shipId, assortId, assortNm, storageId);
        List<Lsshpd> lsshpdList = this.getLsshpdMoveList(startDt, endDt, shipId, assortId, assortNm, storageId, null);
        // lsshpm의 shipStatus가 04(출고)인 놈만 남기기
        lsshpdList = lsshpdList.stream().filter(x->x.getLsshpm().getShipStatus().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        List<MoveCompletedLIstReponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            MoveCompletedLIstReponseData.Move move = new MoveCompletedLIstReponseData.Move(lsshpd.getLsshpm(), lsshpd);
            List<Itvari> itvariList = lsshpd.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                move.setOptionNm1(itvariList.get(0).getOptionNm());
            }
            if(itvariList.size() > 1){
                move.setOptionNm2(itvariList.get(1).getOptionNm());
            }
            moveList.add(move);
        }
        moveCompletedLIstReponseData.setMoves(moveList);
        return moveCompletedLIstReponseData;
    }

    /**
     * 이동내역 조회
     * @return 이동내역 DTO 반환
     */
    public MovedDetailResponseData getMovedDetail(String shipId) {
        List<Lsshpd> lsshpdList = jpaLsshpdRepository.findByShipId(shipId);
        // lsshpm의 shipStatus가 04(출고)인 놈만 남기기
        lsshpdList = lsshpdList.stream().filter(x->x.getLsshpm().getShipStatus().equals(StringFactory.getGbFour())).collect(Collectors.toList());
        if(lsshpdList.size() == 0){
            log.debug("조건에 맞는 데이터가 없습니다.");
            return null;
        }
        MovedDetailResponseData movedDetailResponseData = new MovedDetailResponseData(shipId);
        List<MovedDetailResponseData.Move> moveList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            MovedDetailResponseData.Move move = new MovedDetailResponseData.Move(lsshpd.getLsshpm(), lsshpd);
            List<Itvari> itvariList = lsshpd.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                move.setOptionNm1(itvariList.get(0).getOptionNm());
            }
            if(itvariList.size() > 1){
                move.setOptionNm2(itvariList.get(1).getOptionNm());
            }
            moveList.add(move);
        }
        movedDetailResponseData.setMoves(moveList);
        return movedDetailResponseData;
    }


    /**
     * 상품이동지시 저장시 ititmc의 qty 값을 차감해주는 함수
     */
    public List<Ititmc> subItitmcQties(List<Ititmc> ititmcList, long shipQty) {
        List<Ititmc> newItitmcList = new ArrayList<>();
        long ititmcShipIndQty = this.getItitmcShipIndQtyByStream(ititmcList);
//        long ititmcQty = this.getItitmcQtyByStream(ititmcList);
        if(ititmcShipIndQty < shipQty){
            log.debug("재고량이 맞지 않아 출고가 불가합니다.");
            return newItitmcList;
        }
        for(Ititmc ititmc : ititmcList){
            long qty = ititmc.getQty() == null? 0l:ititmc.getQty(); // ititmc 재고량
            long shipIndQty = ititmc.getShipIndicateQty() == null? 0l:ititmc.getShipIndicateQty(); // ititmc 출고예정량
//            long canShipQty = qty - shipIndQty; // 출고가능량
            if(shipIndQty < shipQty){ // 출고 불가
                continue;
            }
            else { // 이 차례에서 출고 완료 가능
                ititmc.setShipIndicateQty(shipIndQty - shipQty);
                ititmc.setQty(qty - shipQty);
                jpaItitmcRepository.save(ititmc);
                newItitmcList.add(ititmc);
                break;
            }
        }
        if(newItitmcList.size() == 0){
           log.debug("재고량이 맞지 않아 출고가 불가합니다.");
        }
        return newItitmcList;
    }


    /**
     * lsshpd 수량 수정, lsshpm shipStatus 01->04 수정, lsshps 꺾어주는 함수
     */
    public String updateLssSeries(Lsshpd lsshpd){
//         3-1. lsshpd 수량 수정
//        lsshpd.setShipQty(1l);
        jpaLsshpdRepository.save(lsshpd);
        // 3-2. lsshpm shipStatus 01 -> 04
        Lsshpm lsshpm = lsshpd.getLsshpm();
        lsshpm.setShipStatus(StringFactory.getGbFour()); // 01 : 출고지시or이동지시, 04 : 출고. 04 하드코딩
        jpaLsshpmRepository.save(lsshpm);
        // 2-3. lsshps 꺾어주기
        Lsshps lsshps = new Lsshps(lsshpm);
        this.updateLsshps(lsshps);
        return lsshpd.getShipSeq();
    }

    /**
     * Lsshps를 꺾어주는 함수
     */
    private void updateLsshps(Lsshps newLsshps) {
        Lsshps lsshps = jpaLsshpsRepository.findByShipIdAndEffEndDt(newLsshps.getShipId(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        lsshps.setEffEndDt(new Date());
        jpaLsshpsRepository.save(lsshps);
        jpaLsshpsRepository.save(newLsshps);
    }


    /**
     * --------------------------------- 해당 서비스의 유틸성 함수들 ---------------------------
     */
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
     * shipId 채번 함수
     */
    public String getShipId(){
        String shipId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm());
        shipId = Utilities.getStringNo('L',shipId,9);
        return shipId;
    }
}
