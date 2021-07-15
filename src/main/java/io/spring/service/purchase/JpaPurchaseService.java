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
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
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
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final EntityManager em;

    /**
     * 21-05-03 Pecan
     * 발주 insert/update 시퀀스 함수
     * @param purchaseInsertRequestData
     * @return String
     */

    @Transactional
    public String savePurchaseSquence(PurchaseInsertRequestData purchaseInsertRequestData) {
        // lspchd (발주 디테일)
        List<Lspchd> lspchdList = this.saveLspchd(purchaseInsertRequestData);
        // lspchm (발주마스터)
        Lspchm lspchm = this.saveLspchm(purchaseInsertRequestData, lspchdList);
        // lspchb (발주 디테일 이력)
        List<Lspchb> lspchbList = this.saveLspchb(purchaseInsertRequestData);
        // lspchs (발주 상태 이력)
        Lspchs lspchs = this.saveLspchs(purchaseInsertRequestData);
        // lsdpsp (입고 예정)
        List<Lsdpsp> lsdpsp = this.saveLsdpsp(purchaseInsertRequestData);
        // ititmt (예정 재고)
        List<Ititmt> ititmt = this.saveItitmt(purchaseInsertRequestData);
        return lspchm.getPurchaseNo();
    }

    private Lspchm saveLspchm(PurchaseInsertRequestData purchaseInsertRequestData, List<Lspchd> lspchdList) {
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseInsertRequestData.getPurchaseNo()).orElseGet(() -> null);
        if(lspchm == null){ // insert
            lspchm = new Lspchm(purchaseInsertRequestData);
            
			lspchm.setPurchaseStatus(StringFactory.getGbOne()); // 01 하드코딩

        }
        else { // update
            lspchm.setPurchaseDt(purchaseInsertRequestData.getPurchaseDt());
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
            lspchm.setStoreCd(purchaseInsertRequestData.getStoreCd());
            lspchm.setTerms(purchaseInsertRequestData.getTerms());
            lspchm.setDelivery(purchaseInsertRequestData.getDelivery());
            lspchm.setPayment(purchaseInsertRequestData.getPayment());
            lspchm.setCarrier(purchaseInsertRequestData.getCarrier());
            
            lspchm.setDealtypeCd(purchaseInsertRequestData.getDealtypeCd());
            
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

    private Lspchs saveLspchs(PurchaseInsertRequestData purchaseInsertRequestData) {
        Date effEndDt = null;
        try
        {
            effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            log.debug(e.getMessage());
        }
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(purchaseInsertRequestData.getPurchaseNo(), effEndDt);
        if(lspchs == null){ // insert
            lspchs = new Lspchs(purchaseInsertRequestData);
        }
        else{ // update
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, -1);
            lspchs.setEffEndDt(cal.getTime());
            // update 후 새 이력 insert
            Lspchs newLspchs = new Lspchs(lspchs);
            jpaLspchsRepository.save(newLspchs);
        }
        lspchs.setPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
        lspchs.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());
        jpaLspchsRepository.save(lspchs);
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
            }

            lspchd.setPurchaseQty(item.getPurchaseQty());
            lspchd.setPurchaseUnitAmt(item.getPurchaseUnitAmt());

			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());;
            lspchd.setAssortId(item.getAssortId());
            lspchd.setItemId(item.getItemId());
			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());
			lspchd.setSiteGb(StringFactory.getGbOne()); // 01 하드코딩
			lspchd.setVendorId(StringUtils.leftPad(StringFactory.getStrOne(), 6, '0')); // 000001 하드코딩
            jpaLspchdRepository.save(lspchd);
            lspchdList.add(lspchd);
        }
        return lspchdList;
    }

    private List<Lspchb> saveLspchb(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Lspchb> lspchbList = new ArrayList<>();
        Date effEndDt = null;
        for(PurchaseInsertRequestData.Items items: purchaseInsertRequestData.getItems()){
            Date doomDate = Utilities.getStringToDate(StringFactory.getDoomDay());
            Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(purchaseInsertRequestData.getPurchaseNo(), items.getPurchaseSeq(), doomDate);
            if(lspchb == null){ // insert
                lspchb = new Lspchb(purchaseInsertRequestData);
                String purchaseSeq = jpaLspchbRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
                if(purchaseSeq == null){ // 해당 purchaseNo에 seq가 없는 경우
                    purchaseSeq = StringFactory.getFourStartCd(); // 0001
                }
                else{ // 해당 purchaseNo에 seq가 있는 경우
                    purchaseSeq = Utilities.plusOne(purchaseSeq, 4);
                }
                lspchb.setPurchaseSeq(purchaseSeq);
            }
            else{ // update
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, -1);
                lspchb.setEffEndDt(cal.getTime());
                // update 후 새 이력 insert
                Lspchb newLspchb = new Lspchb(lspchb);
                jpaLspchbRepository.save(newLspchb);
            }
            lspchb.setPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
            lspchb.setPurchaseStatus(items.getPurchaseStatus());
            lspchb.setCancelGb(StringFactory.getNinetyNine()); // 추후 수정
            jpaLspchbRepository.save(lspchb);
            lspchbList.add(lspchb);

			String purchaseGb = purchaseInsertRequestData.getPurchaseGb();
			String purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();

			if (purchaseGb.equals("01")) {
				if (purchaseStatus.equals("01")) {
					updateOrderStatusCd(items.getOrderId(), items.getOrderSeq(), StringFactory.getStrB01());
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
                String depositPlanId = jpaCommonService.getNumberId(purchaseInsertRequestData.getDepositPlanId(), StringFactory.getDepositPlanId(), StringFactory.getIntNine());
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
            }

//				if (purchaseGb.equals("02")) { // 01 : 일반발주, 02 : 이동요청
//					ititmt.setTempIndicateQty(0L);
//				} else {
//					ititmt.setTempIndicateQty(items.getPurchaseQty());
//				}
//
//            }
//            else{ // update

            if (purchaseGb.equals("02")) {
                ititmt.setTempIndicateQty(ititmt.getTempIndicateQty());
            } else if(purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne())) { // 일반발주면서 주문발주일 때 (01: 주문발주 02:상품발주 03:입고예정 주문발주)
                ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
            }

            ititmt.setTempQty(ititmt.getTempQty() + items.getPurchaseQty());
//            }
            ititmt.setStockGb(purchaseInsertRequestData.getStockGb());
            ititmt.setStockAmt(purchaseInsertRequestData.getStockAmt());
            ititmt.setVendorId(purchaseInsertRequestData.getVendorId());
            ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb());
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
    // 발주 list 가져오는 함수
    public PurchaseSelectListResponseData getPurchaseList(HashMap<String, Object> param) {
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
                    "and m.purchaseVendorId = ?3 " +
                    "and m.purchaseStatus = ?4 " +
                    "and d.assortId = ?5"
                        , Lspchd.class);
        query.setParameter(1, Utilities.getStringToDate(param.get(StringFactory.getStrStartDt()).toString()))
                .setParameter(2, Utilities.getStringToDate(param.get(StringFactory.getStrEndDt()).toString()))
                .setParameter(3, param.get(StringFactory.getStrPurchaseVendorId()))
                .setParameter(4, param.get(StringFactory.getStrPurchaseStatus()))
                .setParameter(5, param.get(StringFactory.getStrAssortId()));
        List<Lspchd> lspchdList = query.getResultList();
        for(Lspchd lspchd : lspchdList){
            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchd.getLspchm());
            purchase.setPurchaseSeq(lspchd.getPurchaseSeq());
            purchase.setPurchaseQty(lspchd.getPurchaseQty());
            purchase.setPurchaseUnitAmt(lspchd.getPurchaseUnitAmt());
            purchase.setAssortId(lspchd.getAssortId());
            purchase.setItemId(lspchd.getItemId());
            purchase.setSiteOrderNo(lspchd.getSiteOrderNo());
            purchase.setAssortNm(lspchd.getItitmm().getItasrt().getAssortNm());
            purchase.setOptionNm1(lspchd.getItitmm().getItvari1().getOptionNm());
            purchase.setOptionNm2(lspchd.getItitmm().getItvari2().getOptionNm());
            purchaseList.add(purchase);
        }
        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(purchaseList);
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
        Optional<SequenceData> op = jpaSequenceDataRepository.findById(StringFactory.getPurchaseSeqStr());
        SequenceData seq = op.get();
        seq.setSequenceCurValue(StringFactory.getStrZero());
        jpaSequenceDataRepository.save(seq);
        op = jpaSequenceDataRepository.findById(StringFactory.getDepositPlanId());
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
    public boolean makePurchaseData(TbOrderDetail tbOrderDetail, Itasrt itasrt, Ititmc ititmc, Ititmt ititmt, String purchaseGb) {
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
        this.updateLspchs(lsdpsp.getPurchaseNo());

        return true;
    }

    /**
     * lspchs 업뎃 (꺾고 새 row 추가)
     * @return
     */
    private void updateLspchs(String purchaseNo) {
        Date doomDay = Utilities.getStringToDate(StringFactory.getDoomDay());
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(purchaseNo, doomDay);
        lspchs.setEffEndDt(new Date());
        Lspchs newLspchs = new Lspchs(lspchs);
        jpaLspchsRepository.save(lspchs);
        jpaLspchsRepository.save(newLspchs);
    }

    /**
     * lspchb,d 업뎃 (b는 꺾고 새 row 추가, d는 qty 값 변경)
     * @return
     */
    private void updateLspchbd(Lspchd lspchd, long qty) {
        Lspchb lspchb = lspchd.getLspchb().get(0);
        lspchb.setEffEndDt(new Date());
        Lspchb newLspchb = new Lspchb(lspchb);
        long newQty = lspchd.getPurchaseQty() + qty;
        newLspchb.setPurchaseQty(newQty);
        lspchd.setPurchaseQty(newQty);
        jpaLspchbRepository.save(lspchb);
        jpaLspchbRepository.save(newLspchb);
        jpaLspchdRepository.save(lspchd);
    }

    /**
     * lsdpsp 업뎃, 새 row 추가 함수
     * @return
     */
    private void updateLsdpsp(Lsdpsp lsdpsp, long qty){
        long oldPlanQty = lsdpsp.getPurchasePlanQty();
        long oldTakeQty = lsdpsp.getPurchaseTakeQty();
        long newPurchasePlanQty = oldPlanQty - oldTakeQty;
        lsdpsp.setPurchasePlanQty(oldTakeQty);
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
}
