package io.spring.service.purchase;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.jparepos.purchase.JpaLspchbRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.purchase.JpaLspchmRepository;
import io.spring.jparepos.purchase.JpaLspchsRepository;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.deposit.response.PurchaseListInDepositModalData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.move.request.GoodsMoveSaveData;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.model.purchase.entity.Lspchb;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import io.spring.model.purchase.response.PurchaseSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaPurchaseService {
    private final JpaLspchmRepository jpaLspchmRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaLspchbRepository jpaLspchbRepository;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaLspchsRepository jpaLspchsRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaCommonService jpaCommonService;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final EntityManager em;

    /**
     * 21-05-03 Pecan
     * 발주 insert 시퀀스 함수
     * @param purchaseInsertRequestData
     * @return String
     */
    @Transactional
    public String createPurchaseSquence(PurchaseInsertRequestData purchaseInsertRequestData) {
        String purchaseNo = this.getPurchaseNo();
        purchaseInsertRequestData.setPurchaseNo(purchaseNo);
        // lspchd (발주 디테일)
        List<Lspchd> lspchdList = this.saveLspchd(purchaseInsertRequestData);
        // lspchm (발주마스터)
        Lspchm lspchm = this.saveLspchm(purchaseInsertRequestData, lspchdList);
        // lspchb (발주디테일 이력)
        List<Lspchb> lspchbList = this.saveLspchb(lspchdList, purchaseInsertRequestData);
        // lspchs (발주마스터 이력)
        Lspchs lspchs = this.saveLspchs(lspchm, purchaseInsertRequestData);
        // lsdpsp (입고 예정)
        List<Lsdpsp> lsdpsp = this.saveLsdpsp(purchaseInsertRequestData);
        // ititmt (예정 재고)
        List<Ititmt> ititmt = this.saveItitmt(purchaseInsertRequestData);
        return lspchm.getPurchaseNo();
    }

    public void updatePurchaseSquence(String purchaseNo, PurchaseInsertRequestData purchaseInsertRequestData) {

    }

    private Lspchm saveLspchm(PurchaseInsertRequestData purchaseInsertRequestData, List<Lspchd> lspchdList) {
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseInsertRequestData.getPurchaseNo()).orElseGet(() -> null);
        if(lspchm == null){ // insert
            lspchm = new Lspchm(purchaseInsertRequestData);

			lspchm.setPurchaseStatus(StringFactory.getGbOne()); // 01 하드코딩

        }
        else { // update
            lspchm.setPurchaseDt(Utilities.dateToLocalDateTime(purchaseInsertRequestData.getPurchaseDt()));
            lspchm.setEffEndDt(new Date());
            lspchm.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());
            lspchm.setPurchaseRemark(purchaseInsertRequestData.getPurchaseRemark());
//            lspchm.setSiteGb(purchaseInsertRequest.getSiteGb());
//            lspchm.setVendorId(purchaseInsertRequest.getVendorId());
            lspchm.setSiteOrderNo(purchaseInsertRequestData.getSiteOrderNo());
//            lspchm.setSiteTrackno(purchaseInsertRequest.getSiteTrackno());
            lspchm.setNewLocalPrice(purchaseInsertRequestData.getLocalPrice());
            lspchm.setNewLocalTax(purchaseInsertRequestData.getLocalTax());
            lspchm.setNewDisPrice(purchaseInsertRequestData.getDisPrice());
//            lspchm.setPurchaseGb(purchaseInsertRequest.getPurchaseGb());
            lspchm.setStoreCd(purchaseInsertRequestData.getStorageId());
            lspchm.setTerms(purchaseInsertRequestData.getTerms());
            lspchm.setDelivery(purchaseInsertRequestData.getDelivery());
            lspchm.setPayment(purchaseInsertRequestData.getPayment());
            lspchm.setCarrier(purchaseInsertRequestData.getCarrier());

            lspchm.setDealtypeCd(purchaseInsertRequestData.getDealtypeCd());

            lspchm.setUpdId(purchaseInsertRequestData.getUpdId());
        }
        float localPriceSum = lspchdList.stream().map(x-> {
            {
                if (x.getItemAmt() == null) {
                    return 0f;
                } else {
                    return x.getItemAmt();
                }
            }
        }).reduce((a,b)->a+b).get();
        lspchm.setLocalPrice(localPriceSum);
        jpaLspchmRepository.save(lspchm);
        return lspchm;
    }

    private Lspchs saveLspchs(Lspchm lspchm, PurchaseInsertRequestData purchaseInsertRequestData) {
        Date effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)

        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(), Utilities.dateToLocalDateTime(effEndDt));
        if(lspchs == null){ // insert
            lspchs = new Lspchs(purchaseInsertRequestData);
            jpaLspchsRepository.save(lspchs);
        }
        else{ // update
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new Date());
//            cal.add(Calendar.SECOND, -1);
//            lspchs.setEffEndDt(cal.getTime());
//            // update 후 새 이력 insert
//            Lspchs newLspchs = new Lspchs(lspchs);
//            jpaLspchsRepository.save(newLspchs);
            lspchs = this.updateLspchs(lspchm.getPurchaseNo(), purchaseInsertRequestData.getPurchaseStatus());
        }
//        lspchs.setPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
//        lspchs.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());
        return lspchs;
    }

    private List<Lspchd> saveLspchd(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Lspchd> lspchdList = new ArrayList<>();

        for(PurchaseInsertRequestData.Items item : purchaseInsertRequestData.getItems()){
            Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequestData.getPurchaseNo(), item.getPurchaseSeq() == null? null:item.getPurchaseSeq());
            if(lspchd == null){ // insert
                String purchaseSeq = jpaLspchdRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
                if(purchaseSeq == null){
                    purchaseSeq = StringFactory.getFourStartCd();
                }
                else {
                    purchaseSeq = Utilities.plusOne(purchaseSeq, 4);
                }
                lspchd = new Lspchd(purchaseInsertRequestData.getPurchaseNo(), purchaseSeq);
                lspchd.setRegId(purchaseInsertRequestData.getRegId());
            }

            lspchd.setPurchaseQty(item.getPurchaseQty());
            lspchd.setPurchaseUnitAmt(item.getPurchaseUnitAmt());
            lspchd.setPurchaseItemAmt(item.getPurchaseQty()*item.getPurchaseUnitAmt());
			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());;
            lspchd.setAssortId(item.getAssortId());
            lspchd.setItemId(item.getItemId());
			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());
			lspchd.setSiteGb(StringFactory.getGbOne()); // 01 하드코딩
			lspchd.setVendorId(StringUtils.leftPad(StringFactory.getStrOne(), 6, '0')); // 000001 하드코딩

            lspchd.setUpdId(purchaseInsertRequestData.getUpdId());

            jpaLspchdRepository.save(lspchd);
            lspchdList.add(lspchd);
        }
        return lspchdList;
    }

    private List<Lspchb> saveLspchb(List<Lspchd> lspchdList, PurchaseInsertRequestData purchaseInsertRequestData) {
        List<PurchaseInsertRequestData.Items> itemList = purchaseInsertRequestData.getItems();
        List<Lspchb> lspchbList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            PurchaseInsertRequestData.Items item = itemList.get(i);
            Lspchd lspchd = lspchdList.get(i);
            Date doomDate = Utilities.getStringToDate(StringFactory.getDoomDay());
            Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(lspchd.getPurchaseNo(), lspchd.getPurchaseSeq(), doomDate);
            if(lspchb == null){ // insert
//                lspchb = new Lspchb(purchaseInsertRequestData);
//                String purchaseSeq = jpaLspchbRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
//                if(purchaseSeq == null){ // 해당 purchaseNo에 seq가 없는 경우
//                    purchaseSeq = StringFactory.getFourStartCd(); // 0001
//                }
//                else{ // 해당 purchaseNo에 seq가 있는 경우
//                    purchaseSeq = Utilities.plusOne(purchaseSeq, 4);
//                }
//                lspchb.setPurchaseSeq(purchaseSeq);
                lspchb = new Lspchb(lspchd);
                lspchb.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());

                lspchb.setRegId(purchaseInsertRequestData.getRegId());

                jpaLspchbRepository.save(lspchb);
            }
            else{ // update (꺾기)
                lspchb = this.updateLspchbd(lspchd, 0l);
                lspchb.setUpdId(purchaseInsertRequestData.getUpdId());
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(new Date());
//                cal.add(Calendar.SECOND, -1);
//                lspchb.setEffEndDt(cal.getTime());
//                // update 후 새 이력 insert
//                Lspchb newLspchb = new Lspchb(lspchb);
//                jpaLspchbRepository.save(newLspchb);
            }
//            lspchb.setPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
//            lspchb.setPurchaseStatus(item.getPurchaseStatus());
//            jpaLspchbRepository.save(lspchb);
            lspchbList.add(lspchb);

			String purchaseGb = purchaseInsertRequestData.getPurchaseGb();  //purchaseGb 는 발주와 이동지시로 나뉘고 상품발주와 주문발주는 dealTypeCd로 나뉨

			String dealTypeCd = purchaseInsertRequestData.getDealtypeCd();

			System.out.println("dealTypeCd - : " + dealTypeCd);

			String purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();

			if (purchaseGb.equals("01")) {
				if (dealTypeCd != null && dealTypeCd.equals("01") && purchaseStatus.equals("01")) { // 주문발주면서 발주상태라면
					updateOrderStatusCd(item.getOrderId(), item.getOrderSeq(), StringFactory.getStrB01());
				}
			}

        }
        return lspchbList;
    }

    private List<Lsdpsp> saveLsdpsp(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(PurchaseInsertRequestData.Items items : purchaseInsertRequestData.getItems()){
            Lsdpsp lsdpsp = items.getPurchaseSeq() == null || items.getPurchaseSeq().equals("")? null : jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequestData.getPurchaseNo(), items.getPurchaseSeq());
            if(lsdpsp == null){ // insert
                String depositPlanId = jpaCommonService.getNumberId(purchaseInsertRequestData.getDepositPlanId(), StringFactory.getStrSeqLsdpsp(), StringFactory.getIntNine());
                purchaseInsertRequestData.setDepositPlanId(depositPlanId); // depositPlanId 채번
                String seq = jpaLsdpspRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
                if(seq == null){
                    seq = StringFactory.getFourStartCd();
                }
                else{
                    seq = Utilities.plusOne(seq, 4);
                }
                items.setPurchaseSeq(seq);
                lsdpsp = new Lsdpsp(purchaseInsertRequestData, items);
                purchaseInsertRequestData.setDepositPlanId(null);
            }
            else{ // update
                lsdpsp.setPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
                lsdpsp.setPurchaseSeq(items.getPurchaseSeq());
                lsdpsp.setPurchasePlanQty(items.getPurchaseQty() + lsdpsp.getPurchasePlanQty());
//                lsdpsp.setPurchaseTakeQty(purchaseInsertRequestData.getPurchaseTakeQty());
                lsdpsp.setAssortId(items.getAssortId());
                lsdpsp.setItemId(items.getItemId());
                lsdpsp.setPlanStatus(purchaseInsertRequestData.getPlanStatus());
                lsdpsp.setUpdId(purchaseInsertRequestData.getUpdId());
            }
            lsdpspList.add(lsdpsp);
            jpaLsdpspRepository.save(lsdpsp);
        }
        return lsdpspList;
    }

    private List<Ititmt> saveItitmt(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Ititmt> ititmtList = new ArrayList<>();

		String purchaseGb = purchaseInsertRequestData.getPurchaseGb();

        for(PurchaseInsertRequestData.Items items : purchaseInsertRequestData.getItems()){
            ItitmtId ititmtId = new ItitmtId(purchaseInsertRequestData, items);
            Ititmt ititmt = jpaItitmtRepository.findById(ititmtId).orElseGet(() -> null);
            if(ititmt == null) { // insert

                ititmt = new Ititmt(ititmtId);

                ititmt.setTempQty(items.getPurchaseQty());
                ititmt.setTempIndicateQty(0l);
            }

//            if (purchaseGb.equals("02")) { // 01 : 일반발주, 02 : 이동요청
//                ititmt.setTempIndicateQty(0L);
//            } else {
//                ititmt.setTempIndicateQty(items.getPurchaseQty());
//            }
//
//            }
            else { // update
                boolean x = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne()); // 주문발주인가?
                boolean y = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbThree()); // 입고예정 주문발주인가?
                if (x || y) { // 일반발주면서 주문발주거나 입고예정 주문발주일 때 (01: 주문발주 02:상품발주 03:입고예정 주문발주)
                    ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
                }

                ititmt.setTempQty(ititmt.getTempQty() + items.getPurchaseQty());
//            }
                ititmt.setStockGb(purchaseInsertRequestData.getStockGb());
                ititmt.setStockAmt(purchaseInsertRequestData.getStockAmt());
                ititmt.setVendorId(purchaseInsertRequestData.getVendorId());
                ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb());
            }
            jpaItitmtRepository.save(ititmt);
            ititmtList.add(ititmt);
        }
        return ititmtList;
    }

    public PurchaseSelectDetailResponseData getPurchaseDetailPage(String purchaseNo) {
        Lspchm lspchm = jpaLspchmRepository.findById(purchaseNo).orElseGet(() -> null);//.get();
        if(lspchm == null){
            return new PurchaseSelectDetailResponseData();
        }
        List<PurchaseSelectDetailResponseData.Items> itemsList = makeItemsList(lspchm.getLspchdList());
        PurchaseSelectDetailResponseData purchaseSelectDetailResponseData = new PurchaseSelectDetailResponseData(lspchm);
        purchaseSelectDetailResponseData.setItems(itemsList);
        return purchaseSelectDetailResponseData;
    }

    private List<PurchaseSelectDetailResponseData.Items> makeItemsList(List<Lspchd> lspchdList) {
        List<PurchaseSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        for(Lspchd lspchd : lspchdList){
            PurchaseSelectDetailResponseData.Items item = new PurchaseSelectDetailResponseData.Items();
            item.setAssortId(lspchd.getAssortId());
            item.setItemId(lspchd.getItemId());
            item.setPurchaseQty(lspchd.getPurchaseQty());
            item.setPurchaseUnitAmt(lspchd.getPurchaseUnitAmt());
            item.setPurchaseSeq(lspchd.getPurchaseSeq());
            List<Lspchb> lspchbList = lspchd.getLspchb();
            for(Lspchb lspchb : lspchbList){
                if(lspchb.getEffEndDt().compareTo(Utilities.getStringToDate(StringFactory.getDoomDay())) == 0){
                    item.setPurchaseStatus(lspchb.getPurchaseStatus());
                    break;
                }
            }
            itemsList.add(item);
        }
        return itemsList;
    }

    /**
     * 입고 - 발주선택창 : 조건을 넣고 조회했을 때 동작하는 함수 (Lspchm 기준의 list를 가져옴)
     */
    public PurchaseListInDepositModalData getPurchaseMasterList(LocalDate startDt, LocalDate endDt, String purchaseVendorId) {
        PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData(startDt, endDt, purchaseVendorId);
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        TypedQuery<Lspchm> query = em.createQuery("select m from Lspchm m" +
                " where m.purchaseDt between ?1 and ?2" +
                " and (?3 is null or trim(?3)='' or m.purchaseVendorId=?3)",Lspchm.class);
        query.setParameter(1,start).setParameter(2,end).setParameter(3,purchaseVendorId);
        List<Lspchm> lspchmList = query.getResultList();
        List<PurchaseListInDepositModalData.Purchase> purchaseList = new ArrayList<>();
        for(Lspchm lspchm : lspchmList){
           PurchaseListInDepositModalData.Purchase purchase = new PurchaseListInDepositModalData.Purchase(lspchm);
           purchaseList.add(purchase);
        }
        purchaseListInDepositModalData.setPurchases(purchaseList);
//        PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData();
//        return purchaseListInDepositModalData;
        return purchaseListInDepositModalData;
    }

    /**
     * 발주리스트 화면 기준 리스트 가져오는 함수 (Lspchd 기준의 list를 가져옴)
     */
    public PurchaseSelectListResponseData getPurchaseList(HashMap<String, Object> param) {
        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(param);

        String purchaseVendorId = (String)param.get(StringFactory.getStrPurchaseVendorId());
        String assortId = (String)param.get(StringFactory.getStrAssortId());
        String purchaseStatus = (String)param.get(StringFactory.getStrPurchaseStatus());
        String purchaseGb = (String)param.get(StringFactory.getStrPurchaseGb());
        LocalDateTime start = ((LocalDate)param.get(StringFactory.getStrStartDt())).atStartOfDay();
        LocalDateTime end = ((LocalDate)param.get(StringFactory.getStrEndDt())).atTime(23,59,59);
//        purchaseNo = purchaseNo == null || purchaseNo.equals("")? "":" and d.depositNo='"+purchaseNo+"'";

        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();
        TypedQuery<Lspchd> query =
                em.createQuery("select d from Lspchd d " +
                    "join fetch d.lspchm m " +
                    "left join fetch d.ititmm it " +
                    "join fetch it.itasrt " +
                    "left join fetch it.itvari1 " +
                    "left join fetch it.itvari2 " +
                    "where m.purchaseDt " +
                    "between ?1 " +
                    "and ?2 " +
                    "and (?3 is null or trim(?3)='' or m.purchaseVendorId=?3) "+
                    "and (?4 is null or trim(?4)='' or d.assortId=?4) "+
                    "and (?5 is null or trim(?5)='' or m.purchaseStatus=?5) "+
                    "and (?6 is null or trim(?6)='' or m.purchaseGb=?6) "
                        , Lspchd.class);
        query.setParameter(1, start).setParameter(2, end)
                .setParameter(3,purchaseVendorId).setParameter(4,assortId)
                .setParameter(5,purchaseStatus).setParameter(6,purchaseGb);
        List<Lspchd> lspchdList = query.getResultList();
        Lspchm lspchm = null;
        if(lspchdList.size() > 0){
            lspchm = lspchdList.get(0).getLspchm();
            purchaseSelectListResponseData.setPurchaseNo(lspchm.getPurchaseNo());
            purchaseSelectListResponseData.setPurchaseDt(Utilities.removeTAndTransToStr(lspchm.getPurchaseDt()));
        }

        for(Lspchd lspchd : lspchdList){
            Ititmm ititmm = lspchd.getItitmm();
//            Lsdpsd lsdpsd = lspchd.getLsdpsd();
            Itvari itvari1 = ititmm.getItvari1();
            Itvari itvari2 = ititmm.getItvari2();
            String optionNm1 = itvari1 == null? null : itvari1.getOptionNm();
            String optionNm2 = itvari2 == null? null : itvari2.getOptionNm();
            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchd.getLspchm(), lspchd);
//            purchase.setAssortNm(lspchd.getItitmm().getItasrt().getAssortNm());
            purchase.setOptionNm1(optionNm1);
            purchase.setOptionNm2(optionNm2);
            purchase.setItemNm(ititmm.getItemNm());
            purchase.setDepositQty(lspchd.getPurchaseQty());
            purchaseList.add(purchase);
        }
        purchaseSelectListResponseData.setPurchaseList(purchaseList);
        return purchaseSelectListResponseData;
    }

    /**
     * 입고처리 화면에서 발주번호로 검색 시 결과 리스트 가져오는 함수
     */
    public PurchaseSelectListResponseData getDepositPlanList(String purchaseNo) {
        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(purchaseNo);

        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();
        TypedQuery<Lsdpsp> query =
                em.createQuery("select p from Lsdpsp p " +
                                "left join fetch p.lspchd d " +
                                "where p.purchaseNo=?1"
                        , Lsdpsp.class);
        query.setParameter(1, purchaseNo);
        List<Lsdpsp> lsdpspList = query.getResultList();
        if(lsdpspList.size() == 0){ // 해당 purchaseNo에 해당하는 data가 없을 때
            log.debug("there's no purchase exist.");
            return null;
        }
        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();

        purchaseSelectListResponseData.setPurchaseNo(lspchm.getPurchaseNo());
        purchaseSelectListResponseData.setPurchaseDt(Utilities.removeTAndTransToStr(lspchm.getPurchaseDt()));
        purchaseSelectListResponseData.setDepositStoreId(lspchm.getStoreCd());
        purchaseSelectListResponseData.setPurchaseVendorId(lspchm.getPurchaseVendorId());
        purchaseSelectListResponseData.setPurchaseGb(lspchm.getPurchaseGb());

        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getPurchasePlanQty() == lsdpsp.getPurchaseTakeQty()){
                continue;
            }
            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchm);
            purchase.setPurchaseNo(lsdpsp.getPurchaseNo());
            purchase.setPurchaseSeq(lsdpsp.getPurchaseSeq());
            purchase.setAssortId(lsdpsp.getAssortId());
            purchase.setItemId(lsdpsp.getItemId());

            Itasrt itasrt = lsdpsp.getItasrt();//lsdpsp.getTbOrderDetail().getItasrt();//.getLsdpsd().getItasrt();
            purchase.setItemNm(itasrt.getAssortNm());
            int optionSize = itasrt.getItvariList().size();
            if(optionSize > 0){
                Itvari itvari1 = itasrt.getItvariList().get(0);
                purchase.setOptionNm1(itvari1.getOptionNm());
            }
            if(optionSize > 1){
                Itvari itvari2 = itasrt.getItvariList().get(1);
                purchase.setOptionNm2(itvari2.getOptionNm());
            }

            purchase.setDepositPlanId(lsdpsp.getDepositPlanId());
            long planQty = lsdpsp.getPurchasePlanQty() == null? 0l:lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty() == null? 0l:lsdpsp.getPurchaseTakeQty();
            purchase.setAvailableQty(planQty - takeQty);
            purchase.setDepositQty(0l);
            purchase.setPurchaseCost(lsdpsp.getLspchd().getPurchaseUnitAmt());

            purchaseList.add(purchase);
        }
        purchaseSelectListResponseData.setPurchaseList(purchaseList);
        return purchaseSelectListResponseData;
    }

	private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        Date date = Utilities.getStringToDate(StringFactory.getDoomDay());
        List<TbOrderHistory> tohs = tbOrderHistoryrRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq, date);

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

    /**
     * Table 초기화 함수
     */
    public void initTables(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById(StringFactory.getStrSeqLspchm());
        SequenceData seq = op.get();
        seq.setSequenceCurValue(StringFactory.getStrZero());
        jpaSequenceDataRepository.save(seq);
        op = jpaSequenceDataRepository.findById(StringFactory.getStrSeqLsdpsp());
        seq = op.get();
        seq.setSequenceCurValue(StringFactory.getStrZero());
        jpaSequenceDataRepository.save(seq);
        jpaLspchmRepository.deleteAll();
        jpaLspchsRepository.deleteAll();
        jpaLspchmRepository.deleteAll();
        jpaLspchbRepository.deleteAll();
        jpaLspchdRepository.deleteAll();
        jpaLsdpspRepository.deleteAll();
        jpaItitmtRepository.deleteAll();

    }

    /**
     * tbOrderDetail에서 발주 data가 만들어질 때 쓰는 함수 (lspchm, lspchd, lspchs, lspchb, lsdpsp)
     * @return
     */
    @Transactional
    public boolean makePurchaseData(TbOrderDetail tbOrderDetail) {
        // 1. lsdpsp 찾아오기 (d 딸려옴, d에 따라 b도 딸려옴)
        List<Lsdpsp> lsdpspList = jpaLsdpspRepository.findByAssortIdAndItemId(tbOrderDetail.getAssortId(), tbOrderDetail.getItemId());
        // 2. dealTypeCd = 02, purchaseGb = 01인 애들을 필터
        lsdpspList = lsdpspList.stream().filter(x->x.getDealtypeCd().equals(StringFactory.getGbTwo())&&x.getPurchaseGb().equals(StringFactory.getGbOne())).collect(Collectors.toList());
        // 3. lspchb 중 purchaseStatus가 01인 애들만 남기기
        List<Lsdpsp> lsdpspList1 = new ArrayList<>();
        for(Lsdpsp lsdpsp : lsdpspList){
            List<Lspchb> lspchbList = lsdpsp.getLspchd().getLspchb();
            Date date = Utilities.getStringToDate(StringFactory.getDoomDay());
            lspchbList = lspchbList.stream().filter(x->x.getPurchaseStatus().equals(StringFactory.getGbOne())&& x.getEffEndDt().compareTo(date)==0).collect(Collectors.toList());
            int num = lspchbList.size(); //.forEach(x -> System.out.println("ㅡㅡㅡㅡㅡ compare : "+date.compareTo(x.getEffEndDt())));
            if(num > 0){
                lsdpspList1.add(lsdpsp);
            }
        }
        lsdpspList = lsdpspList1;
        // 4. d, b 저장
        Lsdpsp lsdpsp = null;
        for(Lsdpsp item : lsdpspList){
            // lsdpsp의 purchasePlanQty - purchaseTakeQty 값이 tbOrderDetail의 수량 이상일 때
            if(item.getPurchasePlanQty() - item.getPurchaseTakeQty() >= tbOrderDetail.getQty()){
                lsdpsp = item;
                break;
            }
        }
        if(lsdpsp == null){ // 해당하는 psp가 없을 때 -> 발주대기
            return false;
        }
        // 기존 lsdpsp update하고 새로운 lsdpsp 추가
        this.updateLsdpsp(lsdpsp, tbOrderDetail.getQty());
        // lspchb, lspchd update 및 새 row 추가
        this.updateLspchbd(lsdpsp.getLspchd(), tbOrderDetail.getQty());
        // lspchs 저장
        this.updateLspchs(lsdpsp.getPurchaseNo(), StringFactory.getGbOne()); // 01 하드코딩

        return true;
    }

    /**
     * lspchs 업뎃 (꺾고 새 row 추가)
     * @return
     */
    private Lspchs updateLspchs(String purchaseNo, String purchaseStatus) {
        Date doomDay = Utilities.getStringToDate(StringFactory.getDoomDay());
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(purchaseNo, Utilities.dateToLocalDateTime(doomDay));
        lspchs.setEffEndDt(LocalDateTime.now());
        Lspchs newLspchs = new Lspchs(lspchs);
        newLspchs.setPurchaseNo(this.getPurchaseNo());
        newLspchs.setPurchaseStatus(purchaseStatus);
        jpaLspchsRepository.save(lspchs);
        jpaLspchsRepository.save(newLspchs);

        return lspchs;
    }

    /**
     * lspchb,d 업뎃 (b는 꺾고 새 row 추가, d는 qty 값 변경)
     * @return
     */
    private Lspchb updateLspchbd(Lspchd lspchd, long qty) {
        Lspchb lspchb = lspchd.getLspchb().get(0);
        lspchb.setEffEndDt(new Date());
        Lspchb newLspchb = new Lspchb(lspchb);
        long newQty = lspchd.getPurchaseQty() + qty;
        newLspchb.setPurchaseQty(newQty);
        lspchd.setPurchaseQty(newQty);
        jpaLspchbRepository.save(lspchb);
        jpaLspchbRepository.save(newLspchb);
        jpaLspchdRepository.save(lspchd);

        return lspchb;
    }

    /**
     * lsdpsp 업뎃, 새 row 추가 함수
     * @return
     */
    private void updateLsdpsp(Lsdpsp lsdpsp, long qty){
        long oldPlanQty = lsdpsp.getPurchasePlanQty();
        long oldTakeQty = lsdpsp.getPurchaseTakeQty();
        long newPurchasePlanQty = oldPlanQty - oldTakeQty;
//        lsdpsp.setPurchasePlanQty(oldTakeQty);
        String depositPlanId = StringUtils.leftPad(jpaLsdpspRepository.findMaxDepositPlanId(),9,'0');
        Lsdpsp newLsdpsp = new Lsdpsp(depositPlanId,lsdpsp);
        if(newPurchasePlanQty > 0){
            newLsdpsp.setPurchasePlanQty(newPurchasePlanQty);
            newLsdpsp.setPurchaseTakeQty(qty);
            jpaLsdpspRepository.save(newLsdpsp);
        }
        jpaLsdpspRepository.save(lsdpsp);
    }

    /**
     * lsdpsd와 b를 한꺼번에 업데이트하는 함수
     * @return
     */
    private void updateLspchdAndLspchb(Lspchd lspchd, Lspchd newLspchd){
        Lspchb lspchb = lspchd.getLspchb().get(0);
        lspchb.setEffEndDt(new Date());
        Lspchb newLspchb = new Lspchb(lspchd);
        jpaLspchbRepository.save(lspchb);
        jpaLspchbRepository.save(newLspchb);
        jpaLspchdRepository.save(newLspchd);
    }

    /**
     *  depositService에서 이용하는 함수로, 입고 데이터 생성 후 부분입고/완전입고 여부를 따져 lsdchm,b,s의 purchaseStatus를 변경해줌.
     *  (01 : 기본, 03 : 부분입고, 04 : 완전입고)
     */
	public Lspchm changePurchaseStatus(List<Lsdpsp> lsdpspList) {
        List<Lspchb> lspchbList = new ArrayList<>();
        for(Lsdpsp lsdpsp : lsdpspList){
            long planQty = lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty();
            Lspchd lspchd = lsdpsp.getLspchd();
//            long newQty = lspchd.getPurchaseQty() - lsdpsp.getPurchaseTakeQty();
//            lspchd.setPurchaseQty(lspchd.getPurchaseQty() - newQty);
            Date doomDay = Utilities.getStringToDate(StringFactory.getDoomDay());
            List<Lspchb> lspchbList1 = lspchd.getLspchb();
            lspchbList1 = lspchbList1.stream().filter(x->x.getEffEndDt().compareTo(doomDay)==0).collect(Collectors.toList());
            Lspchb lspchb = lspchbList1.get(0);
            if(planQty - takeQty > 0){ // 부분입고 : 03
//                lsdpsp.setPlanStatus(StringFactory.getGbThree()); // planStatus : 03으로 설정
                lspchb = this.updateLspchbdStatus(lspchb,StringFactory.getGbThree()); // planStatus : 03으로 설정
//                lspchb.setEffEndDt(new Date());
//                newLspchb.setPurchaseStatus(StringFactory.getGbThree()); // purchaseStatus : 03으로 설정
//                jpaLspchbRepository.save(newLspchb);
            }
            else if(planQty - takeQty == 0){ // 완전입고 : 04
                lsdpsp.setPlanStatus(StringFactory.getGbFour()); // planStatus : 04로 설정
                lspchb = this.updateLspchbdStatus(lspchb,StringFactory.getGbFour()); // planStatus : 04로 설정
//                lspchb.setEffEndDt(new Date());
//                newLspchb.setPurchaseStatus(StringFactory.getGbFour()); // purchaseStatus : 04로 설정
//                jpaLspchbRepository.save(newLspchb);
            }
            else if(lspchd.getPurchaseQty() < 0){
			//	throw new Exception("");
			    throw new IllegalArgumentException("purchaseQty must bigger than 0..");
            }
            lspchbList.add(lspchb);
//            jpaLspchbRepository.save(lspchb);
            jpaLspchdRepository.save(lspchd);
            jpaLsdpspRepository.save(lsdpsp);
        }
        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();
        Lspchm newLspchm = this.changePurchaseStatusOfLspchm(lspchm, lspchbList);
        this.updateLspchsStatus(lspchm, newLspchm.getPurchaseStatus());
        return lspchm;
    }

    /**
     * lspchb의 status를 이력 꺾기 업데이트 해주는 함수
     */
    private Lspchb updateLspchbdStatus(Lspchb lspchb, String status){
        Lspchb newLspchb = new Lspchb(lspchb);
        lspchb.setEffEndDt(new Date());
        newLspchb.setPurchaseStatus(status);
        jpaLspchbRepository.save(newLspchb);

        return newLspchb;
    }

    /**
     * lspchs의 status를 이력 꺾기 업데이트 해주는 함수
     */
    private Lspchs updateLspchsStatus(Lspchm lspchm, String status){
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(),Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));
        Lspchs newLspchs = new Lspchs(lspchs);
        lspchs.setEffEndDt(LocalDateTime.now());
        newLspchs.setPurchaseStatus(status);
        jpaLspchsRepository.save(lspchs);
        jpaLspchsRepository.save(newLspchs);

        return newLspchs;
    }
    

    /**
     * lspchb 목록을 받아 해당하는 lspchm의 purchaseStatus를 변경해주는 함수
     * 해당 purchaseNo의 b가 모두 완전입고면 m도 완전입고, 하나라도 부분입고면 m은 부분입고.
     */
    private Lspchm changePurchaseStatusOfLspchm(Lspchm lspchm, List<Lspchb> lspchbList) {
        Lspchm newLspchm = new Lspchm(lspchm);
        newLspchm.setPurchaseStatus(StringFactory.getGbFour());
        for(Lspchb lspchb : lspchbList){
            if(lspchb.getPurchaseStatus().equals(StringFactory.getGbThree())){
                newLspchm.setPurchaseStatus(StringFactory.getGbThree());
                jpaLspchmRepository.save(newLspchm);
                break;
            }
        }
        return newLspchm;
    }

    /**
     * 주문이동 저장시 생성되는 발주 data를 만드는 함수
     */
    public void makePurchaseDataFromOrderMoveSave(List<Lsdpsd> lsdpsdList, List<OrderMoveSaveData.Move> moveList) {
//        Lspchm receiveLsdpsm = lsdpsdList.get(0).getLspchd().getLsdpsp().get(0).getLspchd().getLspchm();
        // lspchm,s,d,b insert
        for (int i = 0; i < moveList.size() ; i++) {
            OrderMoveSaveData.Move move = moveList.get(i);
            String purchaseNo = this.getPurchaseNo();
            Lsdpsd itemLsdpsd = lsdpsdList.get(i);
            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(move.getOrderId(),move.getOrderSeq());//itemLsdpsd.getLspchd().getTbOrderDetail();
            TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();

            // lspchm insert
            Lspchm lspchm = new Lspchm(purchaseNo);
            lspchm.setDealtypeCd(StringFactory.getGbOne()); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (01 하드코딩)
            lspchm.setSiteOrderNo(tbOrderMaster.getChannelOrderNo());
            // lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기
//            lspchm.setPurchaseRemark(receiveLsdpsm.getRegId());

            Lspchs lspchs = new Lspchs(lspchm);

            String purchaseSeq = StringUtils.leftPad(Integer.toString(i+1),4,'0');
            if(jpaLspchdRepository.findByOrderIdAndOrderSeq(tbOrderDetail.getOrderId(),tbOrderDetail.getOrderSeq()) != null){
                log.debug("이미 해당 주문에 대한 발주 데이터가 존재합니다!");
                return;
            }
            Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq,
                    itemLsdpsd, tbOrderDetail);
            Lspchb lspchb = new Lspchb(lspchd);

            jpaLspchmRepository.save(lspchm);
            jpaLspchsRepository.save(lspchs);
            jpaLspchdRepository.save(lspchd);
            jpaLspchbRepository.save(lspchb);
        }
    }

//    /**
//     * 상품이동 저장시 생성되는 발주 data를 만드는 함수
//     */
//    public void makePurchaseDataFromGoodsMoveSave(String regId, GoodsMoveSaveData goodsMoveSaveData, List<GoodsMoveSaveData.Goods> newGoodsList) {
//        String purchaseNo = this.getPurchaseNo();
//        List<GoodsMoveSaveData.Goods> goodsList = goodsMoveSaveData.getGoods();
//
//        // lspchm insert
//        Lspchm lspchm = new Lspchm(purchaseNo);
//        lspchm.setDealtypeCd(StringFactory.getGbTwo()); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (02 하드코딩)
//        // lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기
//        lspchm.setPurchaseRemark(regId);
//
//        Lspchs lspchs = new Lspchs(lspchm);
//        jpaLspchmRepository.save(lspchm);
//        jpaLspchsRepository.save(lspchs);
//
//        // lspchd insert
//        int length = newGoodsList.size();
//        for (int i = 0; i < length ; i++) {
//            String purchaseSeq = StringUtils.leftPad(Integer.toString(i+1),4,'0');
//            Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, newGoodsList.get(i));
//            Lspchb lspchb = new Lspchb(lspchd);
//            jpaLspchdRepository.save(lspchd);
//            jpaLspchbRepository.save(lspchb);
//        }
//    }

    /**
     * 상품이동 저장시 생성되는 발주 data를 만드는 함수
     */
    public void makePurchaseDataFromGoodsMoveSave(String regId, GoodsMoveSaveData goodsMoveSaveData, List<GoodsMoveSaveData.Goods> newGoodsList) {
        String purchaseNo = this.getPurchaseNo();
        List<GoodsMoveSaveData.Goods> goodsList = goodsMoveSaveData.getGoods();

        // lspchm insert
        Lspchm lspchm = new Lspchm(purchaseNo);
        lspchm.setDealtypeCd(StringFactory.getGbTwo()); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (02 하드코딩)
        // lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기
        lspchm.setPurchaseRemark(regId);

        Lspchs lspchs = new Lspchs(lspchm);
        jpaLspchmRepository.save(lspchm);
        jpaLspchsRepository.save(lspchs);

        // lspchd insert
        int length = newGoodsList.size();
        for (int i = 0; i < length ; i++) {
            String purchaseSeq = StringUtils.leftPad(Integer.toString(i+1),4,'0');
            Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, newGoodsList.get(i));
            Lspchb lspchb = new Lspchb(lspchd);
            jpaLspchdRepository.save(lspchd);
            jpaLspchbRepository.save(lspchb);
        }
    }

    /**
     * purchaseNo 채번 함수
     */
    private String getPurchaseNo(){
        String purchaseNo = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLspchm());
        purchaseNo = Utilities.getStringNo('C',purchaseNo,9);
        return purchaseNo;
    }
}
