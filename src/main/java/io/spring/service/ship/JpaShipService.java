package io.spring.service.ship;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.model.ship.entity.Lsshps;
import io.spring.model.ship.request.ShipIndicateSaveListData;
import io.spring.model.ship.request.ShipSaveListData;
import io.spring.model.ship.response.ShipIndicateListData;
import io.spring.model.ship.response.ShipIndicateSaveListResponseData;
import io.spring.model.ship.response.ShipItemListData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.move.JpaMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaShipService {
    private final JpaCommonService jpaCommonService;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaMoveService jpaMoveService;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaItitmcRepository jpaItitmcRepository;

    private final EntityManager em;

    /**
     * 출고지시 화면에서 조건검색하면 리스트를 반환해주는 함수
     */
    public ShipIndicateSaveListResponseData getOrderSaveList(Date startDt, Date endDt, String assortId, String assortNm, String purchaseVendorId) {
        List<ShipIndicateSaveListResponseData.Ship> shipList = new ArrayList<>();
        List<TbOrderDetail> tbOrderDetailList = this.getOrdersByCondition(startDt, endDt, assortId, assortNm, purchaseVendorId);
        // tbOrderDetailList 중 statusCd가 C04인 애들만 남겨두기
        tbOrderDetailList = tbOrderDetailList.stream().filter(x->x.getStatusCd().equals(StringFactory.getStrC04())).collect(Collectors.toList());
        for(TbOrderDetail tbOrderDetail : tbOrderDetailList){
            List<Ititmc> ititmcList = jpaItitmcRepository.findByOrderIdAndOrderSeqOrderByEffEndDtAsc(tbOrderDetail.getAssortId(),tbOrderDetail.getItemId());
            long availableQty = this.calcMaxAvailableQty(ititmcList);
            ShipIndicateSaveListResponseData.Ship ship = new ShipIndicateSaveListResponseData.Ship(tbOrderDetail);
            ship.setAvailableQty(availableQty);
            shipList.add(ship);
            List<Itvari> itvariList = tbOrderDetail.getItasrt().getItvariList();
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                ship.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                ship.setOptionNm2(itvari2.getOptionNm());
            }
        }
        ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = new ShipIndicateSaveListResponseData(startDt, endDt, assortId, assortNm, purchaseVendorId);
        shipIndicateSaveListResponseData.setShips(shipList);
        return shipIndicateSaveListResponseData;
    }

    /**
     * 해당 ititmcList 중 shipIndicateQty의 최댓값을 반환하는 함수
     */
    private long calcMaxAvailableQty(List<Ititmc> ititmcList) {
        long maxShipIndicateQty = -1;
        for(Ititmc ititmc : ititmcList){
            long shipIndicateQty = ititmc.getShipIndicateQty() == null ? 0l : ititmc.getShipIndicateQty();
            if(shipIndicateQty > maxShipIndicateQty){
                maxShipIndicateQty = shipIndicateQty;
            }
        }
        return maxShipIndicateQty;
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
     * 출고지시 수량 입력 후 저장하는 함수
     */
    @Transactional
    public List<String> saveShipIndicate(ShipIndicateSaveListData shipIndicateSaveListData) {
        if(shipIndicateSaveListData.getShips().size() == 0){
            log.debug("input data is empty.");
            return null;
        }
        List<TbOrderDetail> tbOrderDetailList = this.makeTbOrderDetailByShipIndicateSaveListData(shipIndicateSaveListData);
        List<String> shipIdList = new ArrayList<>();
        for (int i = 0; i < tbOrderDetailList.size(); i++) {
            TbOrderDetail tbOrderDetail = tbOrderDetailList.get(i);
            ShipIndicateSaveListData.Ship ship = shipIndicateSaveListData.getShips().get(i);
            if(ship.getQty() > tbOrderDetailList.get(i).getQty()){
                log.debug("주문량보다 더 많이 출고할 수 없습니다.");
                continue;
            }

            List<Ititmc> ititmcList = jpaItitmcRepository.findByOrderIdAndOrderSeqOrderByEffEndDtAsc(tbOrderDetail.getAssortId(), tbOrderDetail.getItemId());
            // 1. 재고에서 출고 차감 계산
            ititmcList = this.calcItitmcQties(ititmcList, ship.getQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로 ititmcList에 값이 있다면 한 개만 들어있어야 함)
            if(ititmcList.size()==0){
                log.debug("출고지시량 이상의 출고가능량을 가진 재고 세트가 없습니다.");
                continue;
            }
            // 2. 출고 data 생성
            String shipId = this.makeShipData(ititmcList.get(0), ship, tbOrderDetail, StringFactory.getGbOne()); // 01 : 이동지시or출고지시, 04 : 출고
            if(shipId != null){shipIdList.add(shipId);}
            // 3. 주문 상태 변경 (C04 -> D01)
            tbOrderDetail.setStatusCd(StringFactory.getStrD01()); // D01 하드코딩
        }
        return shipIdList;
    }

    /**
     * ititmc의 qty 들에서 주문량만큼을 차감해주는 함수 
     */
    private List<Ititmc> calcItitmcQties(List<Ititmc> ititmcList, long shipIndQty) {
        long ititmcQty = jpaMoveService.getItitmcQtyByStream(ititmcList); // 해당 ititmc 리스트의 총 재고수량
        long ititmcShipIndQty = jpaMoveService.getItitmcShipIndQtyByStream(ititmcList); // 해당 ititmc 리스트의 총 출고예정수량
        long shipAvailQty = ititmcQty - ititmcShipIndQty;
        if(shipAvailQty < shipIndQty){ // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
            log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " + shipIndQty);
            return ititmcList;
        }
        ititmcList = jpaMoveService.calcItitmcQty(ititmcList, shipIndQty);
        return ititmcList;
    }

    /**
     * ShipIndicateSaveListData로부터 TbOrderDetail 리스트를 만들어 반환
     */
    private List<TbOrderDetail> makeTbOrderDetailByShipIndicateSaveListData(ShipIndicateSaveListData shipIndicateSaveData) {
        List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();
        for(ShipIndicateSaveListData.Ship ship:shipIndicateSaveData.getShips()){
            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(ship.getOrderId(),ship.getOrderSeq());
            tbOrderDetailList.add(tbOrderDetail);
        }
        return tbOrderDetailList;
    }

    /**
     * 출고 관련 값 update, 출고 관련 data 생성 함수 (lsshpm,d,s)
     * ShipIndicateSaveData 객체로 lsshpm,s,d 생성
     */
    private String makeShipData(Ititmc ititmc, ShipIndicateSaveListData.Ship ship, TbOrderDetail tbOrderDetail, String shipStatus) {
        String shipId = getShipId();

        for (int i = 0; i < ship.getQty(); i++) {
            Itasrt itasrt = tbOrderDetail.getItasrt();
            if(i==0){
                // lsshpm 저장
                Lsshpm lsshpm = new Lsshpm(shipId, itasrt, tbOrderDetail);
                lsshpm.setShipStatus(shipStatus); // 01 : 이동지시or출고지시, 04 : 출고
                lsshpm.setDeliId(tbOrderDetail.getTbOrderMaster().getDeliId());
                // lsshps 저장
                Lsshps lsshps = new Lsshps(lsshpm);
                jpaLsshpsRepository.save(lsshps);
                jpaLsshpmRepository.save(lsshpm);
            }
            // lsshpd 저장
            String shipSeq = StringUtils.leftPad(Integer.toString(i + 1), 4,'0');
            Lsshpd lsshpd = new Lsshpd(shipId, shipSeq, tbOrderDetail, ititmc, itasrt);
//            lsshpd.setLocalPrice(tbOrderDetail.getLspchd());
            lsshpd.setVendorDealCd(StringFactory.getGbOne()); // 01 : 주문, 02 : 상품, 03 : 입고예정
            lsshpd.setShipIndicateQty(1l);
            jpaLsshpdRepository.save(lsshpd);
        }
        return shipId;
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
     * 출고 : 출고지시리스트 화면, 출고처리 화면, 출고리스트 화면에서 list를 불러오는 함수
     * 출고지시리스트 화면인지 출고처리 화면인지 출고리스트 화면인지는 statusCd로 구분됨.
     * (C04 : 출고지시리스트 화면, D01 : 출고처리 화면, D02 : 출고리스트 화면)
     */
    public ShipIndicateListData getShipList(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                            String shipId, String assortId, String assortNm,
                                            String vendorId, String statusCd) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        ShipIndicateListData shipIndicateListData = new ShipIndicateListData(start.toLocalDate(),end.toLocalDate(),shipId,assortId,assortNm,vendorId);
//        start = startDt == null? Utilities.strToLocalDate(StringFactory.getStartDay()) : startDt;
//        end = endDt == null? Utilities.strToLocalDate(StringFactory.getStartDay()) : endDt.plusDays(1);
        TypedQuery<Lsshpd> query = em.createQuery("select lsd from Lsshpd lsd " +
                        "join fetch lsd.lsshpm lsm " +
                        "join fetch lsd.tbOrderDetail td " +
                        "join fetch td.itasrt it "+
                        "where lsm.receiptDt between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or td.assortId=?3) " +
                        "and (?4 is null or trim(?4)='' or lsd.shipId=?4) " +
                        "and (?5 is null or trim(?5)='' or it.assortNm like concat('%', ?5, '%')) " +
                        "and (?6 is null or trim(?6)='' or lsd.vendorId=?6)"
                ,Lsshpd.class);
        query.setParameter(1, start).setParameter(2, end)
                .setParameter(3,assortId).setParameter(4,shipId)
                .setParameter(5,assortNm).setParameter(6,vendorId);
        List<Lsshpd> lsshpdList = query.getResultList();
        // 출고지시리스트 : C04, 출고처리리스트 : D01, 출고리스트 statusCd = D01인 애들만 남기기
        lsshpdList = lsshpdList.stream().filter(x->x.getTbOrderDetail().getStatusCd().equals(statusCd)).collect(Collectors.toList());
        List<ShipIndicateListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd : lsshpdList){
            Lsshpm lsshpm = lsshpd.getLsshpm();
            ShipIndicateListData.Ship ship = new ShipIndicateListData.Ship(lsshpd.getTbOrderDetail(), lsshpm, lsshpd);
            // option set
            List<Itvari> itvariList = lsshpd.getTbOrderDetail().getItasrt().getItvariList();
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                ship.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                ship.setOptionNm2(itvari2.getOptionNm());
            }
            // 출고지시 qty 설정 == 1l
            ship.setQty(1l);
            shipList.add(ship);
        }
        shipIndicateListData.setShips(shipList);
        return shipIndicateListData;
    }

    /**
     * 출고 - 출고지시내역 : shipId를 받아 출고마스터와 출고디테일 내역을 반환
     */
    public ShipItemListData getShipIndicateDetailList(String shipId) {
        Lsshpm lsshpm = jpaLsshpmRepository.findByShipId(shipId);
        ShipItemListData shipItemListData = new ShipItemListData(lsshpm);
        TbOrderMaster tbOrderMaster = lsshpm.getTbOrderMaster();
        shipItemListData.setOrderDt(tbOrderMaster.getOrderDate());
        List<Lsshpd> lsshpdList = lsshpm.getLsshpdList();
        List<ShipItemListData.Ship> shipList = new ArrayList<>();
        for(Lsshpd lsshpd:lsshpdList){
            ShipItemListData.Ship ship = new ShipItemListData.Ship(lsshpd);
            // option
            List<Itvari> itvariList = lsshpd.getTbOrderDetail().getItasrt().getItvariList();
            if(itvariList.size() > 0){
                Itvari itvari1 = itvariList.get(0);
                ship.setOptionNm1(itvari1.getOptionNm());
            }
            if(itvariList.size() > 1){
                Itvari itvari2 = itvariList.get(1);
                ship.setOptionNm2(itvari2.getOptionNm());
            }
            shipList.add(ship);
        }
        shipItemListData.setShips(shipList);
        return shipItemListData;
    }

    /**
     * 출고처리 - 변한 값을 저장하는 함수
     */
    @Transactional
    public List<String> shipIndToShip(ShipSaveListData shipSaveListData) {
        List<String> shipIdList = new ArrayList<>();
        // 1. ititmc의 두 qty에서 처리된 양만큼 빼기
        List<Lsshpd> lsshpdList = new ArrayList<>();
        for(ShipSaveListData.Ship ship : shipSaveListData.getShips()){
            Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(ship.getShipId(), ship.getShipSeq());
            Lsshpm lsshpm = lsshpd.getLsshpm();
            if(lsshpm.getShipStatus().equals(StringFactory.getGbFour())){ // shipStatus가 이미 04(출고)면 패스
                log.debug("요청된 출고처리 " + Utilities.addDashInMiddle(lsshpd.getShipId(), lsshpd.getShipSeq()) + "는 이미 출고된 상태입니다.");
                continue;
            }
            lsshpm.setApplyDay(new Date());
            lsshpdList.add(lsshpd);
            // 2. 해당 tbOrderDetail statusCd 변경
            TbOrderDetail tbOrderDetail = lsshpd.getTbOrderDetail();
            List<Ititmc> ititmcList = jpaItitmcRepository.findByOrderIdAndOrderSeqOrderByEffEndDtAsc(tbOrderDetail.getAssortId(), tbOrderDetail.getItemId());
            // 재고에서 출고 차감 계산
            ititmcList = this.subItitmcQties(ititmcList, ship.getQty()); // 주문량만큼 출고차감 (하나의 ititmc에서 모두 차감하므로 ititmcList에 값이 있다면 한 개만 들어있어야 함)
            if(ititmcList.size()==0){
                log.debug("출고처리량 이상의 출고지시량을 가진 재고 세트가 없습니다.");
                continue;
            }
            else {
                tbOrderDetail.setStatusCd(StringFactory.getStrD02()); // D02 하드코딩
                jpaTbOrderDetailRepository.save(tbOrderDetail);
            }
        }
        // 3. lss- 시리즈 찾아서 수정하고 꺾기
        for(Lsshpd lsshpd : lsshpdList){
            shipIdList.add(this.updateLssSeries(lsshpd));
        }
        return shipIdList;
    }

    /**
     * lsshpd 수량 수정, lsshpm shipStatus 01->04 수정, lsshps 꺾어주는 함수
     */
    private String updateLssSeries(Lsshpd lsshpd){
        // 3-1. lsshpd 수량 수정
        lsshpd.setShipQty(1l);
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
     * 상품이동지시 저장시 ititmc의 qty 값을 차감해주는 함수
     */
    public List<Ititmc> subItitmcQties(List<Ititmc> ititmcList, long shipQty) {
        List<Ititmc> newItitmcList = new ArrayList<>();
        long ititmcShipIndQty = jpaMoveService.getItitmcShipIndQtyByStream(ititmcList);
        long ititmcQty = jpaMoveService.getItitmcQtyByStream(ititmcList);
        if(ititmcShipIndQty < shipQty){
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
        return newItitmcList;
    }

    /**
     * 출고 - 출고리스트 : 출고리스트 반환하는 함수
     */
    public ShipIndicateSaveListResponseData getShipDetailList(String shipId) {
        ShipIndicateSaveListResponseData shipIndicateSaveListResponseData = new ShipIndicateSaveListResponseData();

        return shipIndicateSaveListResponseData;
    }

    /**
     * shipId 채번 함수
     */
    public String getShipId(){
        return Utilities.getStringNo('L',jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsshpm()),9);
    }
}
