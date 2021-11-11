package io.spring.service.purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.enums.DirectOrImport;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.purchase.JpaLspchbRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.purchase.JpaLspchmRepository;
import io.spring.jparepos.purchase.JpaLspchsRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.deposit.response.PurchaseListInDepositModalData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.entity.Itvari;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.purchase.entity.Lspchb;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.request.PurchaseUpdateRequestData;
import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import io.spring.model.purchase.response.PurchaseSelectListResponseData;
import io.spring.model.ship.entity.Lsshpd;
import io.spring.model.ship.entity.Lsshpm;
import io.spring.service.common.JpaCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaPurchaseService {
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaLspchmRepository jpaLspchmRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaLspchbRepository jpaLspchbRepository;
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaLspchsRepository jpaLspchsRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaCommonService jpaCommonService;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

	private final JpaLsshpmRepository jpaLsshpmRepository;

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

		System.out.println(purchaseInsertRequestData);

        String purchaseNo = null;
        if(purchaseInsertRequestData.getPurchaseId() == null){
            purchaseNo = this.getPurchaseNo();
            purchaseInsertRequestData.setPurchaseId(purchaseNo);
        }
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
        List<Ititmt> ititmt = this.saveItitmt(purchaseInsertRequestData, lspchm);
        // tbOrderDetail 상태변경
        this.changeStatusCdOfTbOrderDetail(lspchdList);

        return lspchm.getPurchaseNo();
    }

    /**
     * 발주등록(주문) 저장 후 tbOrderDetail의 statusCd를 B01에서 B02로 변경해주는 함수
     */
    private void changeStatusCdOfTbOrderDetail(List<Lspchd> lspchdList) {
        for(Lspchd lspchd : lspchdList){
//            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
//            if(tbOrderDetail != null){ // 01 : 주문이동, 02 : 상품이동
//                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
                this.updateOrderStatusCd(lspchd.getOrderId(),lspchd.getOrderSeq(), StringFactory.getStrB02());
//                jpaTbOrderDetailRepository.save(tbOrderDetail);
//            }
        }
    }

    public String updatePurchaseSquence(String purchaseNo, PurchaseUpdateRequestData purchaseUpdateRequestData) {
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseNo).orElseGet(()->null);
        if(lspchm == null){
            log.debug("update할 lspchm이 존재하지 않습니다. purcahseNo : " + purchaseNo);
            return null;
        }
        lspchm.setPurchaseStatus(purchaseUpdateRequestData.getPurchaseStatus());
        lspchm.setVendorId(purchaseUpdateRequestData.getVendorId());
        lspchm.setPurchaseDt(Utilities.dateToLocalDateTime(purchaseUpdateRequestData.getPurchaseDt()));
        lspchm.setStoreCd(purchaseUpdateRequestData.getStorageId());
        lspchm.setSiteOrderNo(purchaseUpdateRequestData.getSiteOrderNo());
        lspchm.setTerms(purchaseUpdateRequestData.getTerms());
        lspchm.setDelivery(purchaseUpdateRequestData.getDelivery());
        lspchm.setPayment(purchaseUpdateRequestData.getPayment());
        lspchm.setCarrier(purchaseUpdateRequestData.getCarrier());
        lspchm.setUpdId(purchaseUpdateRequestData.getUserId());
        jpaLspchmRepository.save(lspchm);
        return purchaseNo;
    }

    /**
     * printDt update 함수
     * * printDt가 이미 존재하는 발주면 저장돼있는 printDt를 반환
     * * printDt가 없던 발주면 저장하고 반환
     */
    public String savePrintDt(String purchaseNo, Date printDt){
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseNo).orElseGet(() -> null);
        if(lspchm == null){
            log.debug("해당하는 발주번호의 발주데이터가 존재하지 않습니다.");
            return null;
        }
        if(lspchm.getPrintDt() != null){
            return Utilities.removeTAndTransToStr(lspchm.getPrintDt());
        }
        lspchm.setPrintDt(Utilities.dateToLocalDateTime(printDt));
        return Utilities.dateToString(printDt);
    }

    private Lspchm saveLspchm(PurchaseInsertRequestData purchaseInsertRequestData, List<Lspchd> lspchdList) {
        if(lspchdList.size() == 0){
            log.debug("저장할 발주 목록이 존재하지 않습니다.");
            return null;
        }
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseInsertRequestData.getPurchaseId()).orElseGet(() -> null);
        if(lspchm == null){ // insert
            lspchm = new Lspchm(purchaseInsertRequestData);
            /// 임시
            Lspchd lspchd = lspchdList.get(0);
            Itasrt itasrt = jpaItasrtRepository.findByAssortId(lspchd.getAssortId());
            lspchm.setStoreCd(itasrt.getStorageId());
            ///
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

            lspchm.setUpdId(purchaseInsertRequestData.getUserId());
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
            lspchs = this.updateLspchs(lspchm.getPurchaseNo(), purchaseInsertRequestData.getPurchaseStatus());
            lspchs.setUpdId(purchaseInsertRequestData.getUserId());
        }
        return lspchs;
    }

    private List<Lspchd> saveLspchd(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Lspchd> lspchdList = new ArrayList<>();

        for(PurchaseInsertRequestData.Items item : purchaseInsertRequestData.getItems()){
            Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequestData.getPurchaseId(), item.getPurchaseSeq() == null? null:item.getPurchaseSeq());
            if(lspchd == null){ // insert
                String purchaseSeq = jpaLspchdRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseId());
                if(purchaseSeq == null){
                    purchaseSeq = StringFactory.getFourStartCd();
                }
                else {
                    purchaseSeq = Utilities.plusOne(purchaseSeq, 4);
                }
                lspchd = new Lspchd(purchaseInsertRequestData.getPurchaseId(), purchaseSeq);
                lspchd.setRegId(purchaseInsertRequestData.getUserId());
                lspchd.setUpdId(purchaseInsertRequestData.getUserId());
            }

            lspchd.setPurchaseQty(item.getPurchaseQty());
            lspchd.setPurchaseUnitAmt(item.getPurchaseUnitAmt());
            if(item.getPurchaseQty()== null || item.getPurchaseUnitAmt() == null){
                log.debug("purchaseQty 또는 purchaseUnitAmt가 null 입니다.");
                lspchd.setPurchaseItemAmt(null);
            }
            else{
                lspchd.setPurchaseItemAmt(item.getPurchaseQty()*item.getPurchaseUnitAmt());
            }
			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());
            lspchd.setAssortId(item.getAssortId());
            lspchd.setItemId(item.getItemId());
			lspchd.setOrderId(item.getOrderId());
			lspchd.setOrderSeq(item.getOrderSeq());
			lspchd.setSiteGb(StringFactory.getGbOne()); // 01 하드코딩
			lspchd.setOwnerId(StringUtils.leftPad(StringFactory.getStrOne(), 6, '0')); // 000001 하드코딩

            lspchd.setUpdId(purchaseInsertRequestData.getUserId());

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
            LocalDateTime doomDate = Utilities.dateToLocalDateTime(Utilities.getStringToDate(StringFactory.getDoomDay()));
            Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(lspchd.getPurchaseNo(), lspchd.getPurchaseSeq(), doomDate);
            if(lspchb == null){ // insert
                lspchb = new Lspchb(lspchd, null);
                lspchb.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());

                lspchb.setRegId(purchaseInsertRequestData.getUserId());
                lspchb.setUpdId(purchaseInsertRequestData.getUserId());

                jpaLspchbRepository.save(lspchb);
            }
            else{ // update (꺾기)
                lspchb = this.updateLspchbd(lspchd, 0l);
                lspchb.setUpdId(purchaseInsertRequestData.getUserId());
            }
            lspchbList.add(lspchb);

			String purchaseGb = purchaseInsertRequestData.getPurchaseGb();  //purchaseGb 는 발주와 이동지시로 나뉘고 상품발주와 주문발주는 dealTypeCd로 나뉨

			String dealTypeCd = purchaseInsertRequestData.getDealtypeCd();

			System.out.println("dealTypeCd - : " + dealTypeCd);

			String purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();

//			if (purchaseGb.equals("01")) {
//				if (dealTypeCd != null && dealTypeCd.equals("01") && purchaseStatus.equals("01")) { // 주문발주면서 발주상태라면
//					updateOrderStatusCd(item.getOrderId(), item.getOrderSeq(), StringFactory.getStrB01());
//				}
//			}

        }
        return lspchbList;
    }

    private List<Lsdpsp> saveLsdpsp(PurchaseInsertRequestData purchaseInsertRequestData) {
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(PurchaseInsertRequestData.Items items : purchaseInsertRequestData.getItems()){
            Lsdpsp lsdpsp = items.getPurchaseSeq() == null || items.getPurchaseSeq().equals("")? null : jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequestData.getPurchaseId(), items.getPurchaseSeq());
            if(lsdpsp == null){ // insert
                String depositPlanId = jpaCommonService.getNumberId(purchaseInsertRequestData.getDepositPlanId(), StringFactory.getStrSeqLsdpsp(), StringFactory.getIntNine());
                purchaseInsertRequestData.setDepositPlanId(depositPlanId); // depositPlanId 채번
                String seq = jpaLsdpspRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseId());
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
                lsdpsp.setPurchaseNo(purchaseInsertRequestData.getPurchaseId());
                lsdpsp.setPurchaseSeq(items.getPurchaseSeq());
                lsdpsp.setPurchasePlanQty(items.getPurchaseQty());//(items.getPurchaseQty() + lsdpsp.getPurchasePlanQty());
                lsdpsp.setAssortId(items.getAssortId());
                lsdpsp.setItemId(items.getItemId());
                lsdpsp.setPlanStatus(purchaseInsertRequestData.getPlanStatus());
                lsdpsp.setUpdId(purchaseInsertRequestData.getUserId());
            }
            
			System.out.println(lsdpsp);
            
            lsdpspList.add(lsdpsp);
            jpaLsdpspRepository.save(lsdpsp);
        }
        return lsdpspList;
    }

    private List<Ititmt> saveItitmt(PurchaseInsertRequestData purchaseInsertRequestData, Lspchm lspchm) {
        List<Ititmt> ititmtList = new ArrayList<>();

        for(PurchaseInsertRequestData.Items items : purchaseInsertRequestData.getItems()){
            ItitmtId ititmtId = new ItitmtId(purchaseInsertRequestData, items);

			System.out.println(ititmtId);

            Ititmt ititmt = jpaItitmtRepository.findById(ititmtId).orElseGet(() -> null);

			System.out.println(ititmt);
			System.out.println(lspchm);

            if(ititmt == null) { // insert

				Itasrt itasrt = jpaItasrtRepository.findByAssortId(items.getAssortId());

                ititmt = new Ititmt(ititmtId);

                ititmt.setStorageId(lspchm.getStoreCd());
                ititmt.setStockAmt(items.getPurchaseUnitAmt());
                ititmt.setTempQty(items.getPurchaseQty());
                ititmt.setTempIndicateQty(0l);

				boolean x = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne()); // 주문발주인가?
				boolean y = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbThree()); // 입고예정
								
				System.out.println(ititmt.getTempIndicateQty());
				// 주문발주인가?
				if (x || y) { // 일반발주면서 주문발주거나 입고예정 주문발주일 때 (01: 주문발주 02:상품발주 03:입고예정 주문발주)
					ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
				}
				System.out.println(ititmt.getTempIndicateQty());

				ititmt.setVendorId(purchaseInsertRequestData.getVendorId());
				ititmt.setOwnerId(itasrt.getOwnerId());
				ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb());

                ititmt.setRegId(purchaseInsertRequestData.getUserId());
                ititmt.setUpdId(purchaseInsertRequestData.getUserId());
			} else { // update
                boolean x = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne()); // 주문발주인가?
                boolean y = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbThree()); // 입고예정 주문발주인가?
                if (x || y) { // 일반발주면서 주문발주거나 입고예정 주문발주일 때 (01: 주문발주 02:상품발주 03:입고예정 주문발주)
                    ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
                }

                ititmt.setTempQty(ititmt.getTempQty() + items.getPurchaseQty());
				// ititmt.setStockGb(purchaseInsertRequestData.getStockGb()); //처음에 만들어지기떄문에 수정할
				// 필요없음
				// ititmt.setStockAmt(purchaseInsertRequestData.getStockAmt()); //처음에 만들어지기떄문에
				// 수정할 필요없음
				// ititmt.setVendorId(purchaseInsertRequestData.getVendorId()); //처음에 만들어지기떄문에
				// 수정할 필요없음
				// ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb()); //처음에 만들어지기떄문에 수정할
				// 필요없음

                ititmt.setUpdId(purchaseInsertRequestData.getUserId());
            }

            jpaItitmtRepository.save(ititmt);
            ititmtList.add(ititmt);
        }
        return ititmtList;
    }

    /**
     * 발주사후(발주관리, 발주내역) 페이지 json 만드는 함수
     * @param purchaseNo
     * @return
     */
    public PurchaseSelectDetailResponseData getPurchaseDetailPage(String purchaseNo) {
        Lspchm lspchm = jpaLspchmRepository.findById(purchaseNo).orElseGet(() -> null);//.get();
        if(lspchm == null){
            return null;
        }
        List<PurchaseSelectDetailResponseData.Items> itemsList = this.makeItemsList(lspchm);
        PurchaseSelectDetailResponseData purchaseSelectDetailResponseData = new PurchaseSelectDetailResponseData(lspchm);
        purchaseSelectDetailResponseData.setItems(itemsList);
        return purchaseSelectDetailResponseData;
    }

    private List<PurchaseSelectDetailResponseData.Items> makeItemsList(Lspchm lspchm) {
        List<Lspchd> lspchdList = lspchm.getLspchdList();
        List<PurchaseSelectDetailResponseData.Items> itemsList = new ArrayList<>();
        this.makePurchaseItem(itemsList, lspchdList);
        return itemsList;
    }

    private void makePurchaseItem(List<PurchaseSelectDetailResponseData.Items> itemsList, List<Lspchd> lspchdList) {
        for(Lspchd lspchd : lspchdList){
            Itasrt itasrt = lspchd.getItitmm().getItasrt();
            PurchaseSelectDetailResponseData.Items item = new PurchaseSelectDetailResponseData.Items(lspchd, itasrt);
            Utilities.setOptionNames(item, itasrt.getItvariList()); // optionNm set
			if (lspchd.getTbOrderDetail() != null) { // 주문발주인 경우
				TbOrderDetail tbOrderDetail = lspchd.getTbOrderDetail();
                item.setOrderId(tbOrderDetail.getOrderId());
                item.setOrderSeq(tbOrderDetail.getOrderSeq());
                item.setDeliMethod(tbOrderDetail.getDeliMethod());
            }

            List<Lspchb> lspchbList = lspchd.getLspchb();
            for(Lspchb lspchb : lspchbList){
                if(lspchb.getEffEndDt().compareTo(LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()))) == 0){
                    item.setPurchaseStatus(lspchb.getPurchaseStatus());
                    break;
                }
            }
            itemsList.add(item);
        }
    }

    /**
     * 발주사후(발주관리, 발주내역) 페이지에서 purchaseStatus를 변경해주는 함수 (A1:송금완료 A2:거래처선금입금 A3:거래처잔금입금)
     */
    private void changePurchaseStatusInDetailPage(List<Lspchd> lspchdList) {
        for(Lspchd lspchd : lspchdList){
            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
            if(tbOrderDetail != null){ // 01 : 주문이동, 02 : 상품이동
                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
                jpaTbOrderDetailRepository.save(tbOrderDetail);
            }
        }
    }

    /**
     * 입고 - 발주선택창 (입고처리 -> 발주조회 > 조회) : 조건을 넣고 조회했을 때 동작하는 함수 (Lspchm 기준의 list를 가져옴)
     */
	public PurchaseListInDepositModalData getPurchaseMasterList(LocalDate startDt, LocalDate endDt,
			String vendorId, String storageId) {
		PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData(startDt,
				endDt, vendorId, storageId);
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        TypedQuery<Lspchm> query = em.createQuery("select m from Lspchm m" +
                " where m.purchaseDt between ?1 and ?2" +
				" and (?3 is null or trim(?3)='' or m.vendorId=?3) "
				+ " and (?4 is null or trim(?4)='' or m.storeCd=?4) "
				+
                "and m.purchaseStatus in :statusArr", Lspchm.class);
        List<String> statusArr = Arrays.asList(StringFactory.getGbOne(), StringFactory.getGbThree()); // 01:발주 03:부분입고 04:완전입고 05:취소  A1:송금완료 A2:거래처선금입금 A3:거래처잔금입금
		query.setParameter(1, start).setParameter(2, end).setParameter(3, vendorId).setParameter(4, storageId)
                .setParameter("statusArr",statusArr);
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
        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();

        List<Lspchd> lspchdList = this.getLspchd(param);

        if(lspchdList.size() > 0){
            Lspchm lspchm = lspchdList.get(0).getLspchm();
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

			if (lspchd.getOrderId() != null && lspchd.getOrderSeq() != null) {
				TbOrderDetail tob = lspchd.getTbOrderDetail();//tbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),
						//lspchd.getOrderSeq());

				if (tob != null) {
					purchase.setOptionInfo(tob.getOptionInfo());
				}

			}

            purchaseList.add(purchase);
        }
        purchaseSelectListResponseData.setPurchaseList(purchaseList);
        return purchaseSelectListResponseData;
    }

    /**
     * lspchd 조건 검색 쿼리로 lspchd의 리스트를 가져오는 함수
     * @param param
     * @return
     */
    private List<Lspchd> getLspchd(HashMap<String, Object> param) {
        String purchaseVendorId = (String)param.get(StringFactory.getStrPurchaseVendorId());
        String assortId = (String)param.get(StringFactory.getStrAssortId());
        String assortNm = (String)param.get(StringFactory.getStrAssortNm());
        String dealtypeCd = (String)param.get(StringFactory.getStrDealtypeCd());
        String purchaseStatus = (String)param.get(StringFactory.getStrPurchaseStatus());
        String purchaseGb = (String)param.get(StringFactory.getStrPurchaseGb());
        LocalDateTime start = ((LocalDate)param.get(StringFactory.getStrStartDt())).atStartOfDay();
        LocalDateTime end = ((LocalDate)param.get(StringFactory.getStrEndDt())).atTime(23,59,59);
//        purchaseNo = purchaseNo == null || purchaseNo.equals("")? "":" and d.depositNo='"+purchaseNo+"'";

        Query query = em.createQuery("select ld from Lspchd ld " +
                "join fetch ld.lspchm lm " +
                "left outer join fetch ld.tbOrderDetail tod " +
                "left outer join fetch ld.ititmm itm " +
                "left outer join fetch itm.itvari1 iv1 " +
                "left outer join fetch itm.itvari2 iv2 " +
                "where lm.purchaseDt between ?1 and ?2 " +
                "and (?3 is null or trim(?3)='' or ld.lspchm.ownerId=?3) " +
                "and (?4 is null or trim(?4)='' or ld.assortId=?4) "+
                "and (?5 is null or trim(?5)='' or ld.lspchm.purchaseStatus=?5) "+
                "and (?6 is null or trim(?6)='' or ld.lspchm.purchaseGb=?6) " +
                "and (?7 is null or trim(?7)='' or ld.lspchm.dealtypeCd=?7) " +
                "and (?8 is null or trim(?8)='' or itm.itemNm like concat('%',?8,'%'))")
                .setParameter(1, start).setParameter(2, end)
                .setParameter(3,purchaseVendorId).setParameter(4,assortId)
                .setParameter(5,purchaseStatus).setParameter(6,purchaseGb).setParameter(7,dealtypeCd)
                .setParameter(8,assortNm);
//        EntityGraph graph = em.getEntityGraph("Lspchd.purchaseList");
//        query.setHint("javax.persistence.fetchgraph", graph);
        List<Lspchd> lspchdList = query.getResultList();
        return lspchdList;
    }

    /**
     * 입고처리 화면에서 발주번호로 검색 시 결과 리스트 가져오는 함수
     */
    public PurchaseSelectListResponseData getDepositPlanList(String purchaseNo) {
        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();
        List<Lsdpsp> lsdpspList = this.getLsdpsp(purchaseNo);

        if(lsdpspList.size() == 0){ // 해당 purchaseNo에 해당하는 data가 없을 때
            log.debug("there's no purchase exist.");
            return null;
        }
        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();

        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(lspchm);

        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getPurchasePlanQty() == lsdpsp.getPurchaseTakeQty()){
                log.debug("더 이상 입고할 수 없습니다.");
                continue;
            }
            Itasrt itasrt = lsdpsp.getItasrt();//lsdpsp.getTbOrderDetail().getItasrt();//.getLsdpsd().getItasrt();
            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchm, lsdpsp, itasrt);
            Utilities.setOptionNames(purchase, itasrt.getItvariList());

            long planQty = lsdpsp.getPurchasePlanQty() == null? 0l:lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty() == null? 0l:lsdpsp.getPurchaseTakeQty();
            purchase.setAvailableQty(planQty - takeQty);

            purchaseList.add(purchase);
        }
        purchaseSelectListResponseData.setPurchaseList(purchaseList);
        return purchaseSelectListResponseData;
    }

    /**
     * lsdpsp의 리스트를 조건 검색 쿼리로 가져오는 함수
     * @param purchaseNo
     * @return
     */
    private List<Lsdpsp> getLsdpsp(String purchaseNo) {
        TypedQuery<Lsdpsp> query =
                em.createQuery("select p from Lsdpsp p " +
                                "left join fetch p.lspchd d " +
                                "left join fetch d.lspchm m " +
                                "where p.purchaseNo=?1 order by p.depositPlanId asc"
                        , Lsdpsp.class);
        query.setParameter(1, purchaseNo);
        List<Lsdpsp> lsdpspList = query.getResultList();
        return lsdpspList;
    }

    private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        if(tod == null){
            log.debug("해당 주문이 존재하지 않습니다. - JpaPurchaseService.updateOrderStatusCd");
            return;
        }
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
     * tbOrderDetail의 orderStatus 판단시, 해외입고예정재고가 존재할 때 국내입고예정재고가 존재할 때
     * 발주 data가 만들어질 때 쓰는 함수 (lspchm, lspchd, lspchs, lspchb, lsdpsp)
     * @return
     */
    @Transactional
    public boolean makePurchaseDataByOrder(TbOrderDetail tbOrderDetail, DirectOrImport di) {

		System.out.println("makePurchaseDataByOrder");

		System.out.println("di >>" + di);

        // 1. lsdpsp 찾아오기 (d 딸려옴, d에 따라 b도 딸려옴)
        List<Lsdpsp> lsdpspList = jpaLsdpspRepository.findByAssortIdAndItemId(tbOrderDetail.getAssortId(), tbOrderDetail.getItemId());
        // 2. dealTypeCd = 02 (주문발주가 아닌 상품발주), purchaseGb = 01 (일반발주) 인 애들을 필터
		// if(di.equals(DirectOrImport.direct)){
		if (di.equals(DirectOrImport.purchase)) {

			System.out.println("발주");

            lsdpspList = lsdpspList.stream().filter(x->x.getDealtypeCd().equals(StringFactory.getGbTwo())&&x.getPurchaseGb().equals(StringFactory.getGbOne())).collect(Collectors.toList());
        }
        // 2. dealTypeCd = 02 (주문발주가 아닌 상품발주), purchaseGb = 02 (이동요청) 인 애들을 필터
		// else if(di.equals(DirectOrImport.imports)){
		else if (di.equals(DirectOrImport.move)) {

			System.out.println("이동지시");

            lsdpspList = lsdpspList.stream().filter(x->x.getDealtypeCd().equals(StringFactory.getGbTwo())&&x.getPurchaseGb().equals(StringFactory.getGbTwo())).collect(Collectors.toList());

			System.out.println(lsdpspList);

        }
        // 3. lspchb 중 purchaseStatus가 01(부분입고 완전입고 등등이 아닌 발주)인 애들만 남기기

        List<Lsdpsp> lsdpspList1 = new ArrayList<>();
        for(Lsdpsp lsdpsp : lsdpspList){
            List<Lspchb> lspchbList = lsdpsp.getLspchd().getLspchb();
            LocalDateTime date = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
            lspchbList = lspchbList.stream().filter(x->x.getPurchaseStatus().equals(StringFactory.getGbOne())&& x.getEffEndDt().compareTo(date)==0).collect(Collectors.toList());
            int num = lspchbList.size(); //.forEach(x -> System.out.println("ㅡㅡㅡㅡㅡ compare : "+date.compareTo(x.getEffEndDt())));
            if(num > 0){
                lsdpspList1.add(lsdpsp);
            }
        }
        lsdpspList = lsdpspList1;
        // 4. psp 찾기
        Lsdpsp lsdpsp = null;
        Lspchd origLspchd = null;
        Lspchm origLspchm = null;
        for(Lsdpsp item : lsdpspList){
            // lsdpsp의 purchasePlanQty - purchaseTakeQty 값이 tbOrderDetail의 수량 이상일 때
            if(item.getPurchasePlanQty() - item.getPurchaseTakeQty() >= tbOrderDetail.getQty()){
                lsdpsp = item;
                origLspchd = lsdpsp.getLspchd();
                origLspchm = origLspchd.getLspchm();
//                lspchd.setOrderId(tbOrderDetail.getOrderId());
//                lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
//                jpaLspchdRepository.save(lspchd);
                break;
            }
        }
        if(lsdpsp == null){ // 해당하는 psp가 없을 때 -> 발주대기
            return false;
        }
        // lspchm, lspchd, lspchb, lspchs 생성
        Lspchd lspchd = this.saveLspchByOrder(tbOrderDetail, origLspchm, origLspchd, di);
        // 기존 lsdpsp update하고 새로운 lsdpsp 추가
        if(lspchd != null){
//            this.minusLsdpsp(lsdpsp, tbOrderDetail);
//        }
//        else{

			Lspchm lp = jpaLspchmRepository.findByPurchaseNo(lspchd.getPurchaseNo()).orElse(null);

			Ititmt it = new Ititmt(lp, lspchd, "newRegID");
			it.setTempIndicateQty(lspchd.getPurchaseQty());

			jpaItitmtRepository.save(it);

			// jpaItitmtRepository.save(it);

            this.updateLsdpspWhenCandidateExist(lsdpsp, lspchd, tbOrderDetail);

        }

//        this.updateLspchbd(lsdpsp.getLspchd(), tbOrderDetail.getQty());
        // lspchm, s 저장
//        this.updateLspchs(lsdpsp.getPurchaseNo(), StringFactory.getGbOne()); // 01 하드코딩

        return true;
    }

    /**
     * 수입 : 국내입고예정재고가 있는 경우 기존 lsdpsp에서 주문량만큼 입고예정재고 차감
     */
    private void minusLsdpsp(Lsdpsp lsdpsp, TbOrderDetail tbOrderDetail) {
        lsdpsp.setPurchasePlanQty(lsdpsp.getPurchasePlanQty() - tbOrderDetail.getQty());
        jpaLsdpspRepository.save(lsdpsp);
    }

    /**
     * 입고예정재고 lsdpsp 업데이트용 함수
     * 기존 lsdpsp의 purchasePlanQty를 빼고 주문량만큼의 purchasePlanQty를 가진 새로운 lsdpsp를 생성함
     */
    private void updateLsdpspWhenCandidateExist(Lsdpsp lsdpsp, Lspchd lspchd, TbOrderDetail tbOrderDetail){
        long qty = tbOrderDetail.getQty();
        lsdpsp.setPurchasePlanQty(lsdpsp.getPurchasePlanQty() - qty);
        Lsdpsp newLsdpsp = new Lsdpsp(this.getDepositPlanId(), lsdpsp);
        newLsdpsp.setPurchaseTakeQty(0l);
        newLsdpsp.setPurchasePlanQty(qty);
		newLsdpsp.setOrderId(tbOrderDetail.getOrderId());
		newLsdpsp.setOrderSeq(tbOrderDetail.getOrderSeq());
        newLsdpsp.setPurchaseNo(lspchd.getPurchaseNo());
        newLsdpsp.setPurchaseSeq(lsdpsp.getPurchaseSeq());
        newLsdpsp.setDealtypeCd(StringFactory.getGbThree()); // dealtypeCd 03(입고예정주문발주) 하드코딩
        jpaLsdpspRepository.save(lsdpsp);
        jpaLsdpspRepository.save(newLsdpsp);
    }

    /**
     * 입고예정재고가 있을 때 발주 data를 만드는 함수
     */
    private Lspchd saveLspchByOrder(TbOrderDetail tbOrderDetail, Lspchm origLspchm, Lspchd origLspchd, DirectOrImport di) {
//        TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
        this.addMinusPurchase(tbOrderDetail, origLspchd);
//        if(di.equals(DirectOrImport.imports)){
//            log.debug("수입이므로 새로운 발주 데이터 생성하지 않음.");
//            return null;
//        }
        String purchaseNo = this.getPurchaseNo();
        Lspchm lspchm = new Lspchm(tbOrderDetail, di);
        lspchm.setPurchaseNo(purchaseNo);
        lspchm.setSiteOrderNo(origLspchm.getSiteOrderNo());
        Lspchd lspchd = new Lspchd(tbOrderDetail, origLspchd);
		lspchd.setOrderId(tbOrderDetail.getOrderId());
		lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
        lspchd.setPurchaseNo(purchaseNo);
        lspchd.setPurchaseSeq(StringFactory.getFourStartCd()); // 0001 하드코딩
		lspchd.setMemo(Utilities.addDashInMiddle(origLspchd.getPurchaseNo(), origLspchd.getPurchaseSeq()));
        Lspchs lspchs = new Lspchs(lspchm, "regId"); // regId 임시 하드코딩
        Lspchb lspchb = new Lspchb(lspchd, "regId"); // regId 임시 하드코딩
        jpaLspchmRepository.save(lspchm);
        jpaLspchdRepository.save(lspchd);
        jpaLspchsRepository.save(lspchs);
        jpaLspchbRepository.save(lspchb);

        return lspchd;
    }

    /**
     * 음의 qty값을 가진 lspchd를 생성하는 함수
     */
    private void addMinusPurchase(TbOrderDetail tbOrderDetail, Lspchd origLspchd) {
        Lspchd lspchd = new Lspchd(tbOrderDetail, origLspchd);
        lspchd.setPurchaseNo(origLspchd.getPurchaseNo());
        lspchd.setPurchaseSeq(Utilities.plusOne(origLspchd.getPurchaseSeq(),4));
		lspchd.setOwnerId(origLspchd.getOwnerId());
		lspchd.setPurchaseQty(-lspchd.getPurchaseQty());
		lspchd.setPurchaseUnitAmt(-lspchd.getPurchaseUnitAmt());
		lspchd.setPurchaseItemAmt(-lspchd.getPurchaseItemAmt());
		lspchd.setSetShipId(origLspchd.getPurchaseNo());
		lspchd.setSetShipSeq(origLspchd.getPurchaseSeq());

        Lspchb lspchb = new Lspchb(lspchd, "regId"); // regID 임시 하드코딩
        jpaLspchdRepository.save(lspchd);
        jpaLspchbRepository.save(lspchb);
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
        lspchb.setEffEndDt(LocalDateTime.now());
        Lspchb newLspchb = new Lspchb(lspchb);
        long newQty = qty;
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
        String depositPlanId = this.getDepositPlanId();
        Lsdpsp newLsdpsp = new Lsdpsp(depositPlanId,lsdpsp);
        if(newPurchasePlanQty > 0){
            newLsdpsp.setPurchasePlanQty(newPurchasePlanQty);
            newLsdpsp.setPurchaseTakeQty(qty);
            jpaLsdpspRepository.save(newLsdpsp);
        }
        jpaLsdpspRepository.save(lsdpsp);
    }

    /**
     *  depositService에서 이용하는 함수로, 입고 데이터 생성 후 부분입고/완전입고 여부를 따져 lsdchm,b,s의 purchaseStatus를 변경해줌.
     *  (01 : 기본, 03 : 부분입고, 04 : 완전입고)
     */
	public Lspchm changePurchaseStatus(String purchaseNo, List<Lsdpsp> lsdpspList) {
	    if(lsdpspList.size() == 0){
	        log.debug("purchaseStatus를 변경할 lsdpsp가 존재하지 않습니다.");
	        return null;
        }
        List<Lsdpsp> newLsdpspList = jpaLsdpspRepository.findByPurchaseNo(purchaseNo);
        for(Lsdpsp lsdpsp : lsdpspList){
            long planQty = lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty();
            Lspchd lspchd = lsdpsp.getLspchd();
            LocalDateTime doomDay = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
            List<Lspchb> lspchbList1 = lspchd.getLspchb();
            lspchbList1 = lspchbList1.stream().filter(x->x.getEffEndDt().compareTo(doomDay)==0).collect(Collectors.toList());
            Lspchb lspchb = lspchbList1.get(0);
            if(planQty - takeQty > 0){ // 부분입고 : 03
                lspchb = this.updateLspchbdStatus(lspchb,StringFactory.getGbThree()); // planStatus : 03으로 설정
            }
            else if(planQty - takeQty == 0){ // 완전입고 : 04
                lsdpsp.setPlanStatus(StringFactory.getGbFour()); // planStatus : 04로 설정
                lspchb = this.updateLspchbdStatus(lspchb,StringFactory.getGbFour()); // planStatus : 04로 설정
            }
            else if(lspchd.getPurchaseQty() < 0){
			    throw new IllegalArgumentException("purchaseQty must bigger than 0..");
            }
//            lspchbList.add(lspchb);
            jpaLspchdRepository.save(lspchd);
            jpaLsdpspRepository.save(lsdpsp);
        }
        List<Lspchb> lspchbList = jpaLspchbRepository.findByPurchaseNo(purchaseNo);
        LocalDateTime doomDay = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
        lspchbList = lspchbList.stream().filter(x->x.getEffEndDt().compareTo(doomDay)==0).collect(Collectors.toList());

//        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();
		Lspchm lspchm = jpaLspchmRepository.findById(purchaseNo).orElse(null);

        Lspchm newLspchm = this.changePurchaseStatusOfLspchm(lspchm, lspchbList);
        this.updateLspchsStatus(lspchm, newLspchm.getPurchaseStatus());
        return lspchm;
    }

    /**
     * lspchb의 status를 이력 꺾기 업데이트 해주는 함수
     */
    private Lspchb updateLspchbdStatus(Lspchb lspchb, String status){
        Lspchb newLspchb = new Lspchb(lspchb);
        lspchb.setEffEndDt(LocalDateTime.now());
        newLspchb.setPurchaseStatus(status);
        jpaLspchbRepository.save(newLspchb);

        return newLspchb;
    }

    /**
     * lspchs의 status를 이력 꺾기 업데이트 해주는 함수
     */
    private Lspchs updateLspchsStatus(Lspchm lspchm, String status){
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(), LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat())));
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
//        Lspchm newLspchm = new Lspchm(lspchm);

		System.out.println("lspchbList ==> " + lspchbList.size());

		// x -> x.getPurchaseStatus().equals("04")
		Stream<Lspchb> l04 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("04"));
		Stream<Lspchb> l01 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("01"));
		
		String purchaseStatus = "";
		if (lspchbList.size() == l04.count()) {
			purchaseStatus = "04";
		} else if (lspchbList.size() == l01.count()) {
			purchaseStatus = "01";
		} else {
			purchaseStatus = "03";
		}
		/*
		 * if(lspchbList.stream().filter(x->StringFactory.getGbFour().equals(x.
		 * getPurchaseStatus())).collect(Collectors.toList()).size() ==
		 * lspchbList.size()){ lspchm.setPurchaseStatus(StringFactory.getGbFour());
		 * System.out.println("changePurchaseStatusOfLspchm" +
		 * StringFactory.getGbFour()); } else{ for(Lspchb lspchb : lspchbList){
		 * if(lspchb.getPurchaseStatus().equals(StringFactory.getGbThree())){
		 * lspchm.setPurchaseStatus(StringFactory.getGbThree());
		 * System.out.println("changePurchaseStatusOfLspchm" +
		 * StringFactory.getGbThree()); // jpaLspchmRepository.save(newLspchm); break; }
		 * } }
		 */

		lspchm.setPurchaseStatus(purchaseStatus);

		System.out.println("lspchm ==> " + lspchm);

		jpaLspchmRepository.save(lspchm);
		return lspchm;
    }

	/**
	 * 주문이동처리 저장, 상품이동지시 저장시 생성되는 발주 data를 만드는 함수
	 */
	public void makePurchaseDataFromOrderMoveSave2(List<Lsshpd> moveList) {

		// 이동지시 구분에 따라 발주의 구분이 틀려짐.

		// lspchm,s,d,b insert
		for (int i = 0; i < moveList.size(); i++) {
			Lsshpd move = moveList.get(i);

		// 이동지시 개별발주도 생각해볼문제임.

			Lsshpm lsshpm = jpaLsshpmRepository.findById(move.getShipId()).orElse(null);
			Lsshpd lsshpd = move;

		// Lsshpd lsshpd = jpaLsshpdRepository.findByShipIdAndShipSeq(move.getShipId(),
		// move.getShipSeq());


			String purchaseNo = this.getPurchaseNo();

			// Lsdpsd itemLsdpsd = lsdpsdList.get(i);
			TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(move.getOrderId(),
					move.getOrderSeq());// itemLsdpsd.getLspchd().getTbOrderDetail();


			String orderGoodsType = "";

			// lspchm insert
			if (lsshpm.getShipOrderGb().equals("01")) { // 01 : 주문, 02 : 상품
				// 주문이동지시
				orderGoodsType = "01";
			}
			else if (lsshpm.getShipOrderGb().equals("02")) {
				// 상품이동지시
				orderGoodsType = "02";
			}

			Lspchm lspchm = new Lspchm(orderGoodsType, purchaseNo);

			// lspchm.setDealtypeCd(StringFactory.getGbOne()); // 01 : 주문발주, 02 : 상품발주, 03 :
			// 입고예정 주문발주 (01 하드코딩)
			// lspchm.setSiteOrderNo(tbOrderMaster.getChannelOrderNo());
			// lspchm.setPurchaseGb(StringFactory.getGbTwo()); // 이동지시의 경우 02 로 처리

			String OStorageId = "";

			// lspchm insert
			if (lsshpm.getShipOrderGb().equals("01")) {
				// 주문이동지시
				OStorageId = tbOrderDetail.getStorageId();
			} else if (lsshpm.getShipOrderGb().equals("02")) {
				// 상품이동지시
				OStorageId = lsshpm.getOStorageId();
			}

			lspchm.setStoreCd(OStorageId); // 도착지
			lspchm.setOStoreCd(lsshpm.getStorageId()); //출발지

			// lsdpsd.getLsdpsm().getStoreCd();

			// lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기
//            lspchm.setPurchaseRemark(receiveLsdpsm.getRegId());

			Lspchs lspchs = new Lspchs(lspchm, "1");

			// String purchaseSeq = StringUtils.leftPad(Integer.toString(i + 1), 4, '0');

			// 개별발주관련해서 생각해봐야함.
			String purchaseSeq = StringUtils.leftPad(Integer.toString(1), 4, '0');

			// todo 기존의 발주건을 조회할거라면 purchaseGb=02 인건이 대상이여야함.일단은 기존의 발주여부 확인부분제외

			// if(jpaLspchdRepository.findByOrderIdAndOrderSeq(tbOrderDetail.getOrderId(),tbOrderDetail.getOrderSeq())
			// != null){
			// log.debug("이미 해당 주문에 대한 발주 데이터가 존재합니다!");
			// return;
			// }
			Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, lsshpd, tbOrderDetail);
			Lspchb lspchb = new Lspchb(lspchd, "1");

			jpaLspchmRepository.save(lspchm);
			jpaLspchsRepository.save(lspchs);
			jpaLspchdRepository.save(lspchd);
			jpaLspchbRepository.save(lspchb);

//orderGoodsType 01 주문 02 상품

			// lsdpsp insert
			String depositPlanId = this.getDepositPlanId();
			// todo: 2021-10-14 작업자 입력받아야함.
			// 주문이동지시 처리를 위한내용임.
			Lsdpsp lsdpsp = new Lsdpsp(depositPlanId, lspchd, "1", orderGoodsType);
			// lsdpsp.setLspchd(lspchd);
			jpaLsdpspRepository.save(lsdpsp);

			if (lsshpm.getShipOrderGb().equals("01")) {
				// 주문이동지시
				lspchm.setStoreCd(tbOrderDetail.getStorageId()); // 도착지
			} else if (lsshpm.getShipOrderGb().equals("02")) {
				// 상품이동지시
				lspchm.setStoreCd(lsshpm.getOStorageId());
			}

			// ititmt qty update
			Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(
					lspchd.getAssortId(), lspchd.getItemId(), OStorageId,
					StringFactory.getStrEleven(), LocalDateTime.now());
			if (ititmt == null) {
				ititmt = new Ititmt(lspchm, lspchd, "1");
			} else {
				ititmt.setTempQty(ititmt.getTempQty() + lspchd.getPurchaseQty());
			}

			if (lsshpm.getShipOrderGb().equals("01")) {
				// 주문이동지시 에서는 헤당이동지시 수량의 꼬리표를 부착.다른주문건에서 재고를 차감하지 못하게함.
				ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + lspchd.getPurchaseQty());
			}
			jpaItitmtRepository.save(ititmt);

		}
	}

    /**
     * 주문이동 저장시 생성되는 발주 data를 만드는 함수
     */
	// 2021-10-18 사용안함 이후에 삭제해야함.
	/*
	 * public void makePurchaseDataFromOrderMoveSave(List<Lsdpsd> lsdpsdList,
	 * List<OrderMoveSaveData.Move> moveList) {
	 * 
	 * // 2021-10-18 사용안함 이후에 삭제해야함.
	 * 
	 * // lspchm,s,d,b insert for (int i = 0; i < moveList.size() ; i++) {
	 * OrderMoveSaveData.Move move = moveList.get(i);
	 * 
	 * String purchaseNo = this.getPurchaseNo(); Lsdpsd itemLsdpsd =
	 * lsdpsdList.get(i); TbOrderDetail tbOrderDetail =
	 * jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(move.getOrderId(),move.
	 * getOrderSeq());//itemLsdpsd.getLspchd().getTbOrderDetail(); TbOrderMaster
	 * tbOrderMaster = tbOrderDetail.getTbOrderMaster();
	 * 
	 * 
	 * String orderGoodsType = "";
	 * 
	 * // lspchm insert if (lsshpm.getShipOrderGb().equals("01")) { // 주문이동지시
	 * orderGoodsType = "01"; } else if (lsshpm.getShipOrderGb().equals("02")) { //
	 * 상품이동지시 orderGoodsType = "02"; }
	 * 
	 * // lspchm insert Lspchm lspchm = new Lspchm(orderGoodsType,purchaseNo);
	 * lspchm.setDealtypeCd(StringFactory.getGbOne()); // 01 : 주문발주, 02 : 상품발주, 03 :
	 * 입고예정 주문발주 (01 하드코딩) //
	 * lspchm.setSiteOrderNo(tbOrderMaster.getChannelOrderNo()); //
	 * lspchm.setPurchaseGb(StringFactory.getGbTwo()); // 이동지시의 경우 02 로 처리
	 * lspchm.setStoreCd(tbOrderDetail.getStorageId());
	 * lspchm.setOStoreCd(itemLsdpsd.getLsdpsm().getStoreCd());
	 * 
	 * // lsdpsd.getLsdpsm().getStoreCd();
	 * 
	 * // lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기 //
	 * lspchm.setPurchaseRemark(receiveLsdpsm.getRegId());
	 * 
	 * Lspchs lspchs = new Lspchs(lspchm, null);
	 * 
	 * String purchaseSeq = StringUtils.leftPad(Integer.toString(i+1),4,'0');
	 * 
	 * // todo 기존의 발주건을 조회할거라면 purchaseGb=02 인건이 대상이여야함.일단은 기존의 발주여부 확인부분제외
	 * 
	 * //
	 * if(jpaLspchdRepository.findByOrderIdAndOrderSeq(tbOrderDetail.getOrderId(),
	 * tbOrderDetail.getOrderSeq()) // != null){ //
	 * log.debug("이미 해당 주문에 대한 발주 데이터가 존재합니다!"); // return; // } Lspchd lspchd = new
	 * Lspchd(purchaseNo, purchaseSeq, itemLsdpsd, tbOrderDetail); Lspchb lspchb =
	 * new Lspchb(lspchd, null);
	 * 
	 * jpaLspchmRepository.save(lspchm); jpaLspchsRepository.save(lspchs);
	 * jpaLspchdRepository.save(lspchd); jpaLspchbRepository.save(lspchb);
	 * 
	 * // lsdpsp insert String depositPlanId = this.getDepositPlanId(); // todo:
	 * 2021-10-14 작업자 입력받아야함. // 주문이동지시 처리를 위한내용임. Lsdpsp lsdpsp = new
	 * Lsdpsp(depositPlanId, lspchd, "1", "01"); // lsdpsp.setLspchd(lspchd);
	 * jpaLsdpspRepository.save(lsdpsp);
	 * 
	 * // ititmt qty update Ititmt ititmt = jpaItitmtRepository.
	 * findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(
	 * lspchd.getAssortId(), lspchd.getItemId(), tbOrderDetail.getStorageId(),
	 * StringFactory.getStrEleven(), LocalDateTime.now()); if (ititmt == null) {
	 * ititmt = new Ititmt(lspchm, lspchd, "1"); } else {
	 * ititmt.setTempQty(ititmt.getTempQty() + lspchd.getPurchaseQty()); }
	 * 
	 * ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() +
	 * lspchd.getPurchaseQty());
	 * 
	 * jpaItitmtRepository.save(ititmt);
	 * 
	 * } }
	 */

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
    public Lsdpsp makePurchaseDataFromGoodsMoveSave(String regId, LocalDateTime purchaseDt, Lsshpm lsshpm, Lsshpd lsshpd) {
        String purchaseNo = this.getPurchaseNo();

        // lspchm insert
        Lspchm lspchm = new Lspchm(purchaseNo, lsshpm, regId);
        lspchm.setPurchaseDt(purchaseDt); // ititmc.effEndDt
        lspchm.setDealtypeCd(StringFactory.getGbTwo()); // 01 : 주문발주, 02 : 상품발주, 03 : 입고예정 주문발주 (02 하드코딩)
        // lspchm의 purchaseRemark, siteOrderNo, storeCd, oStoreCd set 해주기
        lspchm.setPurchaseRemark(regId);

        // lspchs insert
        Lspchs lspchs = new Lspchs(lspchm, regId);
        jpaLspchmRepository.save(lspchm);
        jpaLspchsRepository.save(lspchs);

        // lspchd,b insert
        String purchaseSeq = StringUtils.leftPad(Integer.toString(1),4,'0');
        Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, lsshpd, regId);
        Lspchb lspchb = new Lspchb(lspchd, regId);
        List<Lspchb> lspchbList = new ArrayList<>();
        lspchbList.add(lspchb);
        lspchd.setLspchb(lspchbList);
        lspchd.setLspchm(lspchm);
        jpaLspchdRepository.save(lspchd);
        jpaLspchbRepository.save(lspchb);

        // lsdpsp insert
        String depositPlanId = this.getDepositPlanId();
        Lsdpsp lsdpsp = new Lsdpsp(depositPlanId, lspchd, regId);
        lsdpsp.setLspchd(lspchd);
        jpaLsdpspRepository.save(lsdpsp);

        // ititmt qty update
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(lsshpd.getAssortId(), lsshpd.getItemId(), lsshpm.getStorageId(),
                StringFactory.getStrEleven(),LocalDateTime.now());
        if(ititmt == null){
            ititmt = new Ititmt(lspchm, lspchd, regId);
        }
        else{
            ititmt.setTempQty(ititmt.getTempQty() + lspchd.getPurchaseQty());
        }
        jpaItitmtRepository.save(ititmt);

        return lsdpsp;
    }

    /**
     * purchaseNo 채번 함수
     */
    private String getPurchaseNo(){
        String purchaseNo = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLspchm());
        purchaseNo = Utilities.getStringNo('C',purchaseNo,9);
        return purchaseNo;
    }

    /**
     * depositPlanId 채번 함수
     */
    private String getDepositPlanId(){
        String depositPlanId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsp());
        depositPlanId = StringUtils.leftPad(depositPlanId,9,'0');
        return depositPlanId;
    }
}
