package io.spring.service.purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import io.spring.model.purchase.response.PurchaseDetailCancelResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.enums.DirectOrImport;
import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.mapstruct.ItemsMapper;
import io.spring.infrastructure.mapstruct.LspchmMapper;
import io.spring.infrastructure.mapstruct.PurchaseMasterListResponseDataMapper;
import io.spring.infrastructure.mapstruct.PurchaseSelectDetailResponseDataMapper;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaCmstgmRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaIfBrandRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.purchase.JpaLspchbRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.purchase.JpaLspchmRepository;
import io.spring.jparepos.purchase.JpaLspchsRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.deposit.response.PurchaseListInDepositModalData;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmm;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderHistory;
import io.spring.model.purchase.entity.Lspchb;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import io.spring.model.purchase.response.PurchaseMasterListResponseData;
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
    private final JpaIfBrandRepository jpaIfBrandRepository;

	private final JpaCmstgmRepository jpaCmstgmRepository;

	private final JpaLsshpdRepository jpaLsshpdRepository;
	private final JpaLsshpmRepository jpaLsshpmRepository;

	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

    private final ItemsMapper itemsMapper;
    private final PurchaseSelectDetailResponseDataMapper purchaseSelectDetailResponseDataMapper;
    private final PurchaseMasterListResponseDataMapper purchaseMasterListResponseDataMapper;
    private final LspchmMapper lspchmMapper;

    private final EntityManager em;

    /**
     * 21-05-03 Pecan
     * ?????? insert ????????? ??????
     * @param purchaseInsertRequestData
     * @return String
     */
    @Transactional
    public String createPurchaseSquence(String purchaseNo, PurchaseInsertRequestData purchaseInsertRequestData) {

		String userId = purchaseInsertRequestData.getUserId();

		System.out.println(purchaseInsertRequestData);

        if(purchaseNo == null){
            purchaseNo = this.getPurchaseNo();
        }
        purchaseInsertRequestData.setPurchaseNo(purchaseNo);
        // lspchd (?????? ?????????)
		List<Lspchd> lspchdList = this.saveLspchd(purchaseInsertRequestData, userId);
        // lspchm (???????????????)
		Lspchm lspchm = this.saveLspchm(purchaseInsertRequestData, lspchdList, userId);
        // lspchb (??????????????? ??????)
		List<Lspchb> lspchbList = this.saveLspchb(lspchdList, purchaseInsertRequestData, userId);
        // lspchs (??????????????? ??????)
		Lspchs lspchs = this.saveLspchs(lspchm, purchaseInsertRequestData, userId);
        // lsdpsp (?????? ??????)
		List<Lsdpsp> lsdpsp = this.saveLsdpsp(purchaseInsertRequestData, userId);
        // ititmt (?????? ??????)
		List<Ititmt> ititmt = this.saveItitmt(purchaseInsertRequestData, lspchm, userId);
        // tbOrderDetail ????????????
        if(purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne())){ // ??????????????? ??????
			this.changeStatusCdOfTbOrderDetail(lspchdList, userId);
        }

        return lspchm.getPurchaseNo();
    }

    /**
     * ????????????(??????) ?????? ??? tbOrderDetail??? statusCd??? B01?????? B02??? ??????????????? ??????
     */
	private void changeStatusCdOfTbOrderDetail(List<Lspchd> lspchdList, String userId) {
        for(Lspchd lspchd : lspchdList){
//            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
//            if(tbOrderDetail != null){ // 01 : ????????????, 02 : ????????????
//                tbOrderDetail.setStatusCd(StringFactory.getStrB02());
			this.updateOrderStatusCd(lspchd.getOrderId(), lspchd.getOrderSeq(), StringFactory.getStrB02(), userId);
//                jpaTbOrderDetailRepository.save(tbOrderDetail);
//            }
        }
    }

    /**
     * ???????????? ???????????? ???????????? ??? ???
     */
	public String updatePurchaseSquence(String purchaseNo, PurchaseInsertRequestData purchaseInsertRequestData,
			String userId) {
        // lspchd (?????? ?????????)
        List<Lspchd> lspchdList = jpaLspchdRepository.findByPurchaseNo(purchaseNo);//this.saveLspchd(purchaseInsertRequestData);
        Lspchm lspchm = lspchdList.size() > 0? lspchdList.get(0).getLspchm() : null;
        if(lspchm == null){
            log.debug("update??? lspchm??? ???????????? ????????????. purcahseNo : " + purchaseNo);
            return null;
        }
//        lspchm = lspchmMapper.to(purchaseInsertRequestData);
        lspchm.setPurchaseStatus(purchaseInsertRequestData.getPurchaseStatus());
        lspchm.setVendorId(purchaseInsertRequestData.getVendorId());
        lspchm.setPurchaseDt(purchaseInsertRequestData.getPurchaseDt());
        lspchm.setStoreCd(purchaseInsertRequestData.getStorageId());
        lspchm.setSiteOrderNo(purchaseInsertRequestData.getSiteOrderNo());
        lspchm.setTerms(purchaseInsertRequestData.getTerms());
        lspchm.setDelivery(purchaseInsertRequestData.getDelivery());
        lspchm.setPayment(purchaseInsertRequestData.getPayment());
        lspchm.setCarrier(purchaseInsertRequestData.getCarrier());

        lspchm.setPiNo(purchaseInsertRequestData.getPiNo());
        lspchm.setMemo(purchaseInsertRequestData.getMemo());
        lspchm.setDeliFee(Utilities.nullOrEmptyFilter(purchaseInsertRequestData.getDeliFee()) == null? null : Float.parseFloat(purchaseInsertRequestData.getDeliFee()));

		lspchm.setUpdId(userId);

        jpaLspchmRepository.save(lspchm);
        Date effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay());
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(purchaseNo, Utilities.dateToLocalDateTime(effEndDt));
        lspchs.setUpdId(purchaseInsertRequestData.getUserId());
        this.updateLspchs(lspchs, lspchm.getPurchaseNo(), purchaseInsertRequestData.getPurchaseStatus(),
                userId);
        for(PurchaseInsertRequestData.Items i : purchaseInsertRequestData.getItems()){
            Lspchd l = lspchdList.stream().filter(x->x.getPurchaseSeq().equals(i.getPurchaseSeq())).collect(Collectors.toList()).get(0);
            l.setPurchaseUnitAmt(i.getPurchaseUnitAmt());
            l.setPurchaseItemAmt(l.getPurchaseQty() * l.getPurchaseUnitAmt());
            l.setCompleDt(i.getCompleDt());

			l.setUpdId(userId);

            jpaLspchdRepository.save(l);
        }
        return purchaseNo;
    }

    /**
     * printDt update ??????
     * * printDt??? ?????? ???????????? ????????? ??????????????? printDt??? ??????
     * * printDt??? ?????? ????????? ???????????? ??????
     */
	public String savePrintDt(String purchaseNo, Date printDt, String userId) {

		System.out.println(purchaseNo);
		System.out.println(printDt);
		System.out.println(userId);

        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseNo).orElseGet(() -> null);
        if(lspchm == null){
            log.debug("???????????? ??????????????? ?????????????????? ???????????? ????????????.");
            return null;
        }
        if(lspchm.getPrintDt() != null){
            return Utilities.removeTAndTransToStr(lspchm.getPrintDt());
        }
        lspchm.setPrintDt(Utilities.dateToLocalDateTime(printDt));

		lspchm.setUpdId(userId);

        jpaLspchmRepository.save(lspchm);

		System.out.println("printDt => " + printDt);

        return Utilities.dateToString(printDt);
    }

	private Lspchm saveLspchm(PurchaseInsertRequestData purchaseInsertRequestData, List<Lspchd> lspchdList,
			String userId) {

        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseInsertRequestData.getPurchaseNo()).orElseGet(() -> null);
        if(lspchm == null){ // insert
            if(lspchdList.size() == 0){
                log.debug("????????? ?????? ????????? ???????????? ????????????.");
                return null;
            }
//            Lspchd lspchd = lspchdList.get(0);
            lspchm = new Lspchm(purchaseInsertRequestData);

			lspchm.setRegId(userId);

            // todo(??????): itasrt.storageId??? ?????????????????? ?????? ??? ????????? ?????? -> ??????. ???????????? ????????? storageId??? ???????????? ???.
			lspchm.setPurchaseStatus(StringFactory.getGbOne()); // 01 ????????????
        }
        else { // update
            lspchm.setPurchaseDt(purchaseInsertRequestData.getPurchaseDt());
            lspchm.setEffEndDt(LocalDateTime.now());
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
            lspchm.setPiNo(purchaseInsertRequestData.getPiNo());
            lspchm.setMemo(purchaseInsertRequestData.getMemo());
            lspchm.setDeliFee(purchaseInsertRequestData.getDeliFee() == null || purchaseInsertRequestData.getDeliFee().trim().equals("")? null : Float.parseFloat(purchaseInsertRequestData.getDeliFee()));

//            lspchm.setDealtypeCd(purchaseInsertRequestData.getDealtypeCd());
            purchaseInsertRequestData.setDealtypeCd(lspchm.getDealtypeCd());
            lspchm.setUpdId(purchaseInsertRequestData.getUserId());
        }
        if(lspchdList.size() > 0){
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
        }

		lspchm.setUpdId(userId);
        jpaLspchmRepository.save(lspchm);
        return lspchm;
    }

	private Lspchs saveLspchs(Lspchm lspchm, PurchaseInsertRequestData purchaseInsertRequestData, String userId) {
        Date effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // ????????? ??????(?????? ?????? 9999-12-31 23:59:59?)

        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(), Utilities.dateToLocalDateTime(effEndDt));
        if(lspchs == null){ // insert
            lspchs = new Lspchs(purchaseInsertRequestData);

			lspchs.setRegId(userId);

			lspchs.setUpdId(userId);

            jpaLspchsRepository.save(lspchs);
        }
        else{ // update
            lspchs.setUpdId(purchaseInsertRequestData.getUserId());
			lspchs = this.updateLspchs(lspchs, lspchm.getPurchaseNo(), purchaseInsertRequestData.getPurchaseStatus(),
					userId);
        }
        return lspchs;
    }

	private List<Lspchd> saveLspchd(PurchaseInsertRequestData purchaseInsertRequestData, String userId) {
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
                lspchd = new Lspchd(purchaseInsertRequestData.getPurchaseNo(), purchaseSeq, item);
				lspchd.setRegId(userId);
//                lspchd.setUpdId(purchaseInsertRequestData.getUserId());
            }
            // ???????????? null?????? 500 ?????? ?????? ???
//            if(item.getPurchaseUnitAmt() == null){
//                throw new Exception("???????????? null ?????????.");
//            }
            lspchd.setPurchaseUnitAmt(item.getPurchaseUnitAmt());
            if(lspchd.getPurchaseQty()== null || item.getPurchaseUnitAmt() == null){
                log.debug("purchaseQty ?????? purchaseUnitAmt??? null ?????????.");
            }
//            else{
                item.setPurchaseUnitAmt(item.getPurchaseUnitAmt() == null? null:item.getPurchaseUnitAmt());
                lspchd.setPurchaseItemAmt(lspchd.getPurchaseQty()*item.getPurchaseUnitAmt());
//            }
				// lspchd.setUpdId(purchaseInsertRequestData.getUserId());

			lspchd.setUpdId(userId);

            jpaLspchdRepository.save(lspchd);
            lspchdList.add(lspchd);
        }
        return lspchdList;
    }

    private List<Lspchb> saveLspchb(List<Lspchd> lspchdList, PurchaseInsertRequestData purchaseInsertRequestData,String userId) {
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

				lspchb.setRegId(userId);
				lspchb.setUpdId(userId);
                
                jpaLspchbRepository.save(lspchb);
            }
            else{ // update (??????)
				lspchb = this.updateLspchbd(lspchd, lspchd.getPurchaseQty(), userId);

				lspchb.setUpdId(userId);
            }
            lspchbList.add(lspchb);

			String purchaseGb = purchaseInsertRequestData.getPurchaseGb();  //purchaseGb ??? ????????? ??????????????? ????????? ??????????????? ??????????????? dealTypeCd??? ??????

			String dealTypeCd = purchaseInsertRequestData.getDealtypeCd();

			System.out.println("dealTypeCd - : " + dealTypeCd);

			String purchaseStatus = purchaseInsertRequestData.getPurchaseStatus();

//			if (purchaseGb.equals("01")) {
//				if (dealTypeCd != null && dealTypeCd.equals("01") && purchaseStatus.equals("01")) { // ?????????????????? ??????????????????
//					updateOrderStatusCd(item.getOrderId(), item.getOrderSeq(), StringFactory.getStrB01());
//				}
//			}

        }
        return lspchbList;
    }

	private List<Lsdpsp> saveLsdpsp(PurchaseInsertRequestData purchaseInsertRequestData, String userId) {
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(PurchaseInsertRequestData.Items items : purchaseInsertRequestData.getItems()){
            Lsdpsp lsdpsp = items.getPurchaseSeq() == null || items.getPurchaseSeq().equals("")? null : jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequestData.getPurchaseNo(), items.getPurchaseSeq());
            if(lsdpsp == null){ // insert
                String depositPlanId = jpaCommonService.getNumberId(purchaseInsertRequestData.getDepositPlanId(), StringFactory.getStrSeqLsdpsp(), StringFactory.getIntNine());
                purchaseInsertRequestData.setDepositPlanId(depositPlanId); // depositPlanId ??????
                String seq = jpaLsdpspRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequestData.getPurchaseNo());
                if(seq == null){
                    seq = StringFactory.getFourStartCd();
                }
                else{
                    seq = Utilities.plusOne(seq, 4);
                }
                items.setPurchaseSeq(seq);
                lsdpsp = new Lsdpsp(purchaseInsertRequestData, items);
				lsdpsp.setRegId(userId);
                purchaseInsertRequestData.setDepositPlanId(null);
            }
            else{ // update
//                lsdpsp.setPurchaseNo(purchaseInsertRequestData.getPurchaseId());
//                lsdpsp.setPurchaseSeq(items.getPurchaseSeq());
//                lsdpsp.setPurchasePlanQty(items.getPurchaseQty());//(items.getPurchaseQty() + lsdpsp.getPurchasePlanQty());
//                lsdpsp.setAssortId(items.getAssortId());
//                lsdpsp.setItemId(items.getItemId());
//                lsdpsp.setPlanStatus(purchaseInsertRequestData.getPlanStatus());
                lsdpsp.setUpdId(purchaseInsertRequestData.getUserId());
            }
            
			System.out.println(lsdpsp);
            
            lsdpspList.add(lsdpsp);
			lsdpsp.setUpdId(userId);
            jpaLsdpspRepository.save(lsdpsp);
        }
        return lsdpspList;
    }

	private List<Ititmt> saveItitmt(PurchaseInsertRequestData purchaseInsertRequestData, Lspchm lspchm, String userId) {
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

				boolean x = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne()); // ???????????????????
				boolean y = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbThree()); // ????????????
								
				System.out.println(ititmt.getTempIndicateQty());
				// ???????????????????
				if (x || y) { // ?????????????????? ?????????????????? ???????????? ??????????????? ??? (01: ???????????? 02:???????????? 03:???????????? ????????????)
					ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
				}
				System.out.println(ititmt.getTempIndicateQty());

				ititmt.setVendorId(purchaseInsertRequestData.getVendorId());
				ititmt.setOwnerId(itasrt.getOwnerId());
				ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb());

				ititmt.setRegId(userId);
				ititmt.setUpdId(userId);
			} else { // update
                boolean x = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbOne()); // ???????????????????
                boolean y = purchaseInsertRequestData.getDealtypeCd().equals(StringFactory.getGbThree()); // ???????????? ???????????????????
                if (x || y) { // ?????????????????? ?????????????????? ???????????? ??????????????? ??? (01: ???????????? 02:???????????? 03:???????????? ????????????)
                    ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + items.getPurchaseQty());
                }
                ititmt.setTempQty(ititmt.getTempQty() + items.getPurchaseQty());
                ititmt.setStockAmt(items.getPurchaseUnitAmt());
				// ititmt.setStockGb(purchaseInsertRequestData.getStockGb()); //????????? ???????????????????????? ?????????
				// ????????????
				// ititmt.setStockAmt(purchaseInsertRequestData.getStockAmt()); //????????? ????????????????????????
				// ????????? ????????????
				// ititmt.setVendorId(purchaseInsertRequestData.getVendorId()); //????????? ????????????????????????
				// ????????? ????????????
				// ititmt.setSiteGb(purchaseInsertRequestData.getSiteGb()); //????????? ???????????????????????? ?????????
				// ????????????

                ititmt.setUpdId(purchaseInsertRequestData.getUserId());
            }

			ititmt.setUpdId(userId);
            jpaItitmtRepository.save(ititmt);
            ititmtList.add(ititmt);
        }
        return ititmtList;
    }

    /**
     * ????????????(????????????, ????????????) ????????? json ????????? ??????
     * @param purchaseNo
     * @return
     */
    public PurchaseSelectDetailResponseData getPurchaseDetailPage(String purchaseNo) {
        List<Lspchd> lspchdList = jpaLspchdRepository.findLspchdByPurchaseNo(purchaseNo);
        if(lspchdList == null || lspchdList.size() == 0){
            log.debug("?????? ??????????????? ???????????? ????????????????????? ???????????? ????????????.");
            return null;
        }

//        List<String> brandIdList = new ArrayList<>();
//        for(Lspchd lspchd : lspchdList){
//            System.out.println();
//            if(lspchd.getItitmm().getItasrt().getBrandId() != null && !brandIdList.contains(lspchd.getItitmm().getItasrt().getBrandId())){
//                brandIdList.add(lspchd.getItitmm().getItasrt().getBrandId());
//            }
//        }
//        List<IfBrand> ifBrandList = brandIdList.size() > 0? jpaIfBrandRepository.findByBrandIdListByChannelIdAndBrandIdList(StringFactory.getGbOne(), brandIdList) : null;

		// List<PurchaseListInDepositModalData.Purchase> purchaseList = new
		// ArrayList<>();
        Lspchm lspchm = lspchdList.get(0).getLspchm();
		List<PurchaseSelectDetailResponseData.Items> itemsList = this.makeItemsList(lspchdList);
        PurchaseSelectDetailResponseData purchaseSelectDetailResponseData = new PurchaseSelectDetailResponseData(lspchm);
        purchaseSelectDetailResponseData.setItems(itemsList);
        purchaseSelectDetailResponseData = purchaseSelectDetailResponseDataMapper.nullToEmpty(purchaseSelectDetailResponseData);
        return purchaseSelectDetailResponseData;
    }

	private List<PurchaseSelectDetailResponseData.Items> makeItemsList(List<Lspchd> lspchdList) {
        List<PurchaseSelectDetailResponseData.Items> itemsList = new ArrayList<>();
		this.makePurchaseItem(itemsList, lspchdList);
        return itemsList;
    }

     /**
     * ????????? ????????? itaimg??? ???????????? ?????? ??????
     */
//    private void makePurchaseItem(List<PurchaseSelectDetailResponseData.Items> itemsList, List<Lspchd> lspchdList) {
//        for(Lspchd lspchd : lspchdList){
//            Ititmm ititmm = lspchd.getItitmm();
//            Itasrt itasrt = ititmm.getItasrt();
//            List<Itaimg> imgList = itasrt.getItaimg();
//            if(imgList != null){
//                imgList = imgList.stream().filter(x->x.getImageGb().equals(StringFactory.getGbOne())).collect(Collectors.toList());
//            }
//            else {
//                imgList = new ArrayList<>();
//            }
//            PurchaseSelectDetailResponseData.Items item = new PurchaseSelectDetailResponseData.Items(lspchd, ititmm, itasrt, imgList.size() == 0? null : imgList.get(0));
//            List<Itvari> itvariList = new ArrayList<>();
//            itvariList.add(ititmm.getItvari1());
//            itvariList.add(ititmm.getItvari2());
//            itvariList.add(ititmm.getItvari3());
//            Utilities.setOptionNames(item, itvariList); // optionNm set
//            if (lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbOne()) && ((lspchd.getOrderId() != null && !lspchd.getOrderId().trim().equals("")) && lspchd.getOrderSeq() != null && !lspchd.getOrderSeq().trim().equals(""))) { // ??????????????? ??????
//                TbOrderDetail tbOrderDetail = lspchd.getTbOrderDetail();
//                IfBrand ifBrand = itasrt.getIfBrand();
////                TbMember tbMember = tbOrderDetail.getTbOrderMaster().getTbMember();
//                item.setOrderId(tbOrderDetail.getOrderId());
//                item.setOrderSeq(tbOrderDetail.getOrderSeq());
//                item.setDeliMethod(tbOrderDetail.getDeliMethod());
//                item.setCustNm(tbOrderDetail.getTbOrderMaster().getReceiverName());
//                item.setReceiverNm(tbOrderDetail.getTbOrderMaster().getReceiverName());
//                item.setReceiverTel(tbOrderDetail.getTbOrderMaster().getReceiverTel());
//                item.setReceiverHp(tbOrderDetail.getTbOrderMaster().getReceiverHp());
//                item.setReceiverAddr1(tbOrderDetail.getTbOrderMaster().getReceiverAddr1());
//                item.setReceiverAddr2(tbOrderDetail.getTbOrderMaster().getReceiverAddr2());
//                item.setReceiverZipcode(tbOrderDetail.getTbOrderMaster().getReceiverZipcode());
//                item.setReceiverZonecode(tbOrderDetail.getTbOrderMaster().getReceiverZonecode());
//                item.setOrderMemo(tbOrderDetail.getTbOrderMaster().getOrderMemo());
//                item.setBrandNm(ifBrand == null? "" : ifBrand.getBrandNm());
////                item.setCustNm(tbMember.getCustNm());
//                item.setChannelOrderNo(tbOrderDetail.getChannelOrderNo());
//            }
//
//            List<Lspchb> lspchbList = lspchd.getLspchb();
//            for(Lspchb lspchb : lspchbList){
//                if(lspchb.getEffEndDt().compareTo(LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()))) == 0){
//                    item.setPurchaseStatus(lspchb.getPurchaseStatus());
//                    break;
//                }
//            }
//            item = itemsMapper.nullToEmpty(item);
//            itemsList.add(item);
//        }
//    }

    /**
     * ????????? ????????? itasrt??? ????????? ????????? ???????????????
     */
    private void makePurchaseItem( List<PurchaseSelectDetailResponseData.Items> itemsList, List<Lspchd> lspchdList) {
        for(Lspchd lspchd : lspchdList){
            Ititmm ititmm = lspchd.getItitmm();
            Itasrt itasrt = ititmm.getItasrt();
//            System.out.println("----- "+ ititmm.getAssortId() + " : " + ititmm.getVariationSeq2() + ", " + ititmm.getVariationSeq3());
            List<Itaimg> imgList = itasrt.getItaimg();
            if(imgList != null){
                imgList = imgList.stream().filter(x->x.getImageGb().equals(StringFactory.getGbOne())).collect(Collectors.toList());
            }
            else {
                imgList = new ArrayList<>();
            }
            PurchaseSelectDetailResponseData.Items item = new PurchaseSelectDetailResponseData.Items(lspchd, ititmm, itasrt, imgList.size() == 0? null : imgList.get(0));

			// 2022-02-09 ?????????????????????????????? new PurchaseSelectDetailResponseData.Items ?????? ?????????????????? ??????

//            List<Itvari> itvariList = new ArrayList<>(); 2022-02-09
			// itvariList.add(ititmm.getItvari1()); 2022-02-09
			// if(ititmm.getVariationSeq2() != null){ 2022-02-09
			// itvariList.add(ititmm.getItvari2()); 2022-02-09
//            } 2022-02-09
			// if(ititmm.getVariationSeq3() != null){ 2022-02-09
			// itvariList.add(ititmm.getItvari3()); 2022-02-09
			// } 2022-02-09
			// Utilities.setOptionNames(item, itvariList); // optionNm set 2022-02-09
            if ((lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbOne()) || lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbThree()))
                    && ((lspchd.getOrderId() != null && !lspchd.getOrderId().trim().equals(""))
                    && lspchd.getOrderSeq() != null && !lspchd.getOrderSeq().trim().equals(""))) { // ??????????????? ??????
                TbOrderDetail tbOrderDetail = lspchd.getTbOrderDetail();
//                IfBrand ifBrand = itasrt.getIfBrand();
//                TbMember tbMember = tbOrderDetail.getTbOrderMaster().getTbMember();
                item.setOrderId(tbOrderDetail.getOrderId());
                item.setOrderSeq(tbOrderDetail.getOrderSeq());
                item.setDeliMethod(tbOrderDetail.getDeliMethod());
                item.setCustNm(tbOrderDetail.getTbOrderMaster().getTbMember().getCustNm());
                item.setReceiverNm(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliNm());
                item.setReceiverTel(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliTel());
                item.setReceiverHp(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliHp());
                item.setReceiverAddr1(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliAddr1());
                item.setReceiverAddr2(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliAddr2());
                item.setReceiverZipcode(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliZipcode());
                item.setReceiverZonecode(tbOrderDetail.getTbOrderMaster().getTbMemberAddress().getDeliZonecode());
                item.setOrderMemo(tbOrderDetail.getTbOrderMaster().getOrderMemo());
                item.setOrderMemo(tbOrderDetail.getTbOrderMaster().getOrderMemo());
                item.setImagePath(tbOrderDetail.getListImageData());
//                item.setBrandNm(itasrt.getBrandId() == null || itasrt.getBrandId().trim().equals("") || itasrt.getIfBrand() == null? "" : itasrt.getIfBrand().getBrandNm());
//                item.setBrandNm(ifBrand == null? "" : ifBrand.getBrandNm());
//                item.setCustNm(tbMember.getCustNm());
                item.setChannelOrderNo(tbOrderDetail.getChannelOrderNo());
                item.setBrandId(itasrt.getBrandId());
				item.setBrandNm(itasrt.getBrandId() != null && !itasrt.getBrandId().trim().equals("") && itasrt.getItbrnd() != null? itasrt.getItbrnd().getBrandNm() : "");
            }

            List<Lspchb> lspchbList = lspchd.getLspchb();
            for(Lspchb lspchb : lspchbList){
                if(lspchb.getEffEndDt().compareTo(LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()))) == 0){
                    item.setPurchaseStatus(lspchb.getPurchaseStatus());
                    break;
                }
            }
            item = itemsMapper.nullToEmpty(item);
            itemsList.add(item);
        }

//        if(brandList != null){
  //          for(PurchaseSelectDetailResponseData.Items item : itemsList){
    //            List<IfBrand> ifBrandList1 = brandList.stream().filter(x->x.getBrandId().equals(item.getBrandId())).collect(Collectors.toList());
//                IfBrand ifBrand = ifBrandList1.size() == 0? null : ifBrandList1.get(0);
  //              item.setBrandNm(ifBrand == null? "" : ifBrand.getBrandNm());
    //        }
     //   }
    }


    /**
     * ????????????(????????????, ????????????) ??????????????? purchaseStatus??? ??????????????? ?????? (A1:???????????? A2:????????????????????? A3:?????????????????????)
     */
	private void changePurchaseStatusInDetailPage(List<Lspchd> lspchdList, String userId) {
        for(Lspchd lspchd : lspchdList){
            TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),lspchd.getOrderSeq());
            if(tbOrderDetail != null){ // 01 : ????????????, 02 : ????????????
                tbOrderDetail.setStatusCd(StringFactory.getStrB02());

				tbOrderDetail.setUpdId(userId);

                jpaTbOrderDetailRepository.save(tbOrderDetail);
            }
        }
    }

    /**
     * ?????? - ??????????????? (???????????? -> ???????????? > ??????) : ????????? ?????? ???????????? ??? ???????????? ?????? (Lspchm ????????? list??? ??????????????? ??? ????????? ?????? ?????? ?????? ????????? ?????????. ?????? ????????? ??????.)
     */
	public PurchaseListInDepositModalData getPurchaseMasterListWithDetails(LocalDate startDt, LocalDate endDt,
                                                                           String vendorId, String storageId, String piNo, String siteOrderNo
    , String blNo) {
		PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData(startDt,
				endDt, vendorId, storageId);
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<String> statusArr = Arrays.asList(StringFactory.getGbFour(), StringFactory.getGbFive()); // 01:?????? 03:???????????? 04:???????????? 05:??????  A1:???????????? A2:????????????????????? A3:?????????????????????
        List<Lspchd> lspchdList;
        List<Lsshpd> lsshpdList;
        Set<Lspchd> purchaseSet = new HashSet<>();
        if(storageId.equals("000002")){ // ??????????????????
            lspchdList = jpaLspchdRepository.findPurchaseList(start, end, vendorId, storageId, piNo, siteOrderNo, statusArr);
        }
        else if(storageId.equals("000001")){ // ??????????????????
            lsshpdList = jpaLsshpdRepository.findPurchaseList(start, end, vendorId, storageId, blNo, statusArr);
            lspchdList = new ArrayList<>();
            for(Lsshpd lsshpd : lsshpdList){
                if(purchaseSet.contains(lsshpd.getPurchaseNo()+lsshpd.getPurchaseSeq())){
                    continue;
                }
                lspchdList.add(lsshpd.getLspchd());
            }
        }
        else{
            log.debug("storageId??? ???????????? ????????????.");
            return null;
        }
        List<Lspchd> filteredLspchdList = new ArrayList<>();
        for(Lspchd lspchd : lspchdList){
            if(lspchd.getOrderId() == null || lspchd.getOrderId().trim().equals("")){
                filteredLspchdList.add(lspchd);
            }
            else if(storageId.equals("000001") && lspchd.getTbOrderDetail().getStatusCd().equals(TrdstOrderStatus.C03.toString())){ // ?????????????????? ?????????????????? ???
                filteredLspchdList.add(lspchd);
            }
            else if(storageId.equals("000002") && lspchd.getTbOrderDetail().getStatusCd().equals(TrdstOrderStatus.B02.toString())){ // ?????????????????? ?????????????????? ???
                filteredLspchdList.add(lspchd);
            }
        }
        lspchdList = filteredLspchdList;

        List<Lspchm> lspchmList = new ArrayList<>();
        List<String> brandIdList = new ArrayList<>();
        Set<String> purchaseNoSet = new HashSet<>();

        for(Lspchd lspchd : lspchdList){
            if(purchaseNoSet.contains(lspchd.getPurchaseNo())){
                continue;
            }
            System.out.println();
            if(lspchd.getItitmm().getItasrt().getBrandId() != null && !brandIdList.contains(lspchd.getItitmm().getItasrt().getBrandId())){
                brandIdList.add(lspchd.getItitmm().getItasrt().getBrandId());
            }
            lspchmList.add(lspchd.getLspchm());
            purchaseNoSet.add(lspchd.getPurchaseNo());
        }
		// List<IfBrand> ifBrandList = brandIdList.size() > 0?
		// jpaIfBrandRepository.findByBrandIdListByChannelIdAndBrandIdList(StringFactory.getGbOne(),
		// brandIdList) : null;
        List<PurchaseListInDepositModalData.Purchase> purchaseList = new ArrayList<>();

        for(Lspchm lspchm : lspchmList){
            PurchaseListInDepositModalData.Purchase purchase = new PurchaseListInDepositModalData.Purchase(lspchm);
			List<PurchaseSelectDetailResponseData.Items> itemsList = this.makeItemsList(lspchdList.stream()
					.filter(x -> x.getPurchaseNo().equals(lspchm.getPurchaseNo()))
                    .collect(Collectors.toList()));
            purchase.setItems(itemsList);
            purchaseList.add(purchase);
        }
        purchaseListInDepositModalData.setPurchases(purchaseList);
//        PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData();
//        return purchaseListInDepositModalData;
        return purchaseListInDepositModalData;
    }

    /**
     * ??????????????? ???????????? (????????? ??????. Lspchm ????????????)
     */
    public PurchaseMasterListResponseData getPurchaseMasterList2(LocalDate startDt, LocalDate endDt,
                                                                 String siteOrderNo, String unifiedOrderNo, String brandId, String vendorId, String purchaseGb, String orderNm,
                                                                 String purchaseNo) {
        PurchaseMasterListResponseData purchaseMasterListResponseData = new PurchaseMasterListResponseData(startDt,
                endDt, siteOrderNo, unifiedOrderNo, brandId, vendorId, purchaseGb);
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<Lspchd> lspchdList = jpaLspchdRepository.getLspchdList(start, end, vendorId, null, null, purchaseGb,
                null, purchaseNo, siteOrderNo, null, brandId, null, unifiedOrderNo,
                orderNm);//query.getResultList();
        List<String> purchaseNoList = new ArrayList<>();
        List<PurchaseMasterListResponseData.Purchase> purchaseList = new ArrayList<>();
        for(Lspchd lspchd : lspchdList){
            Lspchm lspchm = lspchd.getLspchm();
            if(purchaseNoList.contains(lspchm.getPurchaseNo())){
               continue;
            }
            purchaseNoList.add(lspchm.getPurchaseNo());
            PurchaseMasterListResponseData.Purchase purchase = new PurchaseMasterListResponseData.Purchase(lspchm);
            purchase = purchaseMasterListResponseDataMapper.nullToEmpty(purchase);
            purchaseList.add(purchase);
        }
        purchaseMasterListResponseData.setPurchases(purchaseList);
        purchaseMasterListResponseData = purchaseMasterListResponseDataMapper.nullToEmpty(purchaseMasterListResponseData);
//        PurchaseListInDepositModalData purchaseListInDepositModalData = new PurchaseListInDepositModalData();
//        return purchaseListInDepositModalData;
        return purchaseMasterListResponseData;
    }

//    /**
//     * ??????????????? ?????? ?????? ????????? ???????????? ?????? (Lspchd ????????? list??? ?????????)
//     */
//    public PurchaseSelectListResponseData getPurchaseList(String vendorId, String assortId, String purchaseNo, String channelOrderNo, String siteOrderNo, String custNm, String assortNm,
//                                                          String purchaseStatus, String brandId, LocalDate startDt, LocalDate endDt, String purchaseGb, String dealtypeCd) {
//        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(vendorId, assortId, purchaseNo, channelOrderNo, custNm, assortNm, purchaseStatus, brandId,
//                startDt, endDt, purchaseGb, dealtypeCd);
//        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();
//
//        List<Lspchd> lspchdList = this.getLspchd(vendorId, assortId, purchaseNo, channelOrderNo, siteOrderNo, custNm, assortNm, purchaseStatus, brandId,
//                startDt, endDt, purchaseGb, dealtypeCd);
//
//        if(lspchdList.size() > 0){
//            Lspchm lspchm = lspchdList.get(0).getLspchm();
//            purchaseSelectListResponseData.setPurchaseNo(lspchm.getPurchaseNo());
//            purchaseSelectListResponseData.setPurchaseDt(Utilities.removeTAndTransToStr(lspchm.getPurchaseDt()));
//        }
//
//        for(Lspchd lspchd : lspchdList){
//            Ititmm ititmm = lspchd.getItitmm();
//            List<Itvari> itvariList = ititmm.getItasrt().getItvariList();
//            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchd.getLspchm(), lspchd);
//            Utilities.setOptionNames(purchase, itvariList);
//            purchase.setItemNm(ititmm.getItemNm());
//            purchase.setDepositQty(lspchd.getPurchaseQty());
//
//			if ((lspchd.getOrderId() != null && !lspchd.getOrderId().trim().equals("")) && (lspchd.getOrderSeq() != null && !lspchd.getOrderSeq().trim().equals(""))) {
//				TbOrderDetail tob = lspchd.getTbOrderDetail();//tbOrderDetailRepository.findByOrderIdAndOrderSeq(lspchd.getOrderId(),
//						//lspchd.getOrderSeq());
//
//				if (tob != null) {
//					purchase.setOptionInfo(tob.getOptionInfo());
//				}
//
//			}
//
//            purchaseList.add(purchase);
//        }
//        purchaseSelectListResponseData.setPurchaseList(purchaseList);
//        return purchaseSelectListResponseData;
//    }

//    /**
//     * lspchd ?????? ?????? ????????? lspchd??? ???????????? ???????????? ??????
//     * @return
//     */
//    private List<Lspchd> getLspchd(String vendorId,String assortId, String purchaseNo, String channelOrderNo, String siteOrderNo, String custNm, String assortNm,
//                                   String purchaseStatus, String brandId, LocalDate startDt, LocalDate endDt, String purchaseGb, String dealtypeCd) {
//        LocalDateTime start = startDt.atStartOfDay();
//        LocalDateTime end = endDt.atTime(23,59,59);
////        purchaseNo = purchaseNo == null || purchaseNo.equals("")? "":" and d.depositNo='"+purchaseNo+"'";
//
//        List<Lspchd> lspchdList = jpaLspchdRepository.getLspchdList(start, end, vendorId, assortId, purchaseStatus,
//                purchaseGb, dealtypeCd, purchaseNo, siteOrderNo, custNm, brandId, assortNm, channelOrderNo, null);//query.getResultList();
//        return lspchdList;
//    }

    /**
     * ???????????? ???????????? ??????????????? ?????? ??? ?????? ????????? ???????????? ??????
     */
    public PurchaseSelectListResponseData getDepositPlanList(String purchaseNo) {
        List<PurchaseSelectListResponseData.Purchase> purchaseList = new ArrayList<>();
        List<Lsdpsp> lsdpspList = this.getLsdpsp(purchaseNo);
        List<Lsdpsp> lsdpspList1 = new ArrayList<>();
        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getOrderId() == null || lsdpsp.getOrderId().trim().equals("")){
                lsdpspList1.add(lsdpsp);
            }
            else if(lsdpsp.getTbOrderDetail().getStatusCd().equals(TrdstOrderStatus.B02.toString()) || lsdpsp.getTbOrderDetail().getStatusCd().equals(TrdstOrderStatus.C03.toString())){
                lsdpspList1.add(lsdpsp);
            }
        }
        lsdpspList = lsdpspList1;

        if(lsdpspList.size() == 0){ // ?????? purchaseNo??? ???????????? data??? ?????? ???
            log.debug("there's no purchase exist.");
            return null;
        }
        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();

        PurchaseSelectListResponseData purchaseSelectListResponseData = new PurchaseSelectListResponseData(lspchm);

        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getPurchasePlanQty() == lsdpsp.getPurchaseTakeQty()){
                log.debug("??? ?????? ????????? ??? ????????????.");
                continue;
            }
            Itasrt itasrt = lsdpsp.getItasrt();//lsdpsp.getTbOrderDetail().getItasrt();//.getLsdpsd().getItasrt();
            PurchaseSelectListResponseData.Purchase purchase = new PurchaseSelectListResponseData.Purchase(lspchm, lsdpsp, itasrt);
            Lspchd lspchd = lsdpsp.getLspchd();
            if(lspchd.getOrderId() != null && !lspchd.getOrderId().trim().equals("") && lspchd.getOrderSeq() != null && !lspchd.getOrderSeq().trim().equals("")){
                purchase.setCustNm(lspchd.getTbOrderDetail().getTbOrderMaster().getOrderName());
            }
			// Utilities.setOptionNames(purchase, itasrt.getItvariList()); //???????????? ????????? ?????????
			// itasrt itvari??? ?????? ?????? ??? ???????????? ??????????????? ititmm???????????? ??????
			// 2022-02-09 ?????? ???????????? ?????? ??????????????? ?????????.

            long planQty = lsdpsp.getPurchasePlanQty() == null? 0l:lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty() == null? 0l:lsdpsp.getPurchaseTakeQty();
            purchase.setAvailableQty(planQty - takeQty);

			String rackNo = "999999";
            
			// List<Cmstgm> l
			// =jpaCmstgmRepository.findByUpStorageIdAndDefaultYnAndDelYn(lspchm.getStoreCd().toString(),"01",
			// "02");

//			if (l.size() > 0) {
			// rackNo = l.get(0).getStorageId();
			// } else {
			// rackNo = "xxxxxx";
//			}

			purchase.setRackNo(rackNo);

            purchaseList.add(purchase);
        }
        purchaseSelectListResponseData.setPurchaseList(purchaseList);
        return purchaseSelectListResponseData;
    }

    /**
     * lsdpsp??? ???????????? ?????? ?????? ????????? ???????????? ??????
     * @param purchaseNo
     * @return
     */
    private List<Lsdpsp> getLsdpsp(String purchaseNo) {
        TypedQuery<Lsdpsp> query =
                em.createQuery("select p from Lsdpsp p " +
                                "left join fetch p.lspchd d " +
                                "left join fetch p.itasrt it " +
                                "left join fetch d.lspchm m " +
                                "left join fetch d.tbOrderDetail tod " +
						"join fetch p.ititmm itm " + "left join fetch itm.itvari1 itv1 "
						+ "left join fetch itm.itvari2 itv2 " + "left join fetch itm.itvari3 itv3 "
						+
                                "where p.purchaseNo=?1 " +
//                                "and tod.statusCd in (?2, ?3) " +
                                "order by p.depositPlanId asc"
                        , Lsdpsp.class);
        query.setParameter(1, purchaseNo);
//                .setParameter(2, TrdstOrderStatus.B02.toString())
//                .setParameter(3,TrdstOrderStatus.C03.toString());
        List<Lsdpsp> lsdpspList = query.getResultList();
        return lsdpspList;
    }

	private void updateOrderStatusCd(String orderId, String orderSeq, String statusCd, String userId) {

		TbOrderDetail tod = tbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId, orderSeq);
        if(tod == null){
            log.debug("?????? ????????? ???????????? ????????????. - JpaPurchaseService.updateOrderStatusCd");
            return;
        }
        LocalDateTime date = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
        List<TbOrderHistory> tohs = tbOrderHistoryrRepository.findByOrderIdAndOrderSeqAndEffEndDt(orderId, orderSeq, date);

		tod.setStatusCd(statusCd);

        LocalDateTime newEffEndDate = LocalDateTime.now();

		for (int i = 0; i < tohs.size(); i++) {
			tohs.get(i).setEffEndDt(newEffEndDate);
			tohs.get(i).setLastYn("002");
			tohs.get(i).setUpdId(userId);
		}

		TbOrderHistory toh = new TbOrderHistory(orderId, orderSeq, statusCd, "001", newEffEndDate,
				Utilities.strToLocalDateTime(StringFactory.getDoomDayT()));

		toh.setRegId(userId);
		toh.setUpdId(userId);

		tohs.add(toh);

		tod.setUpdId(userId);

		tbOrderDetailRepository.save(tod);

		tbOrderHistoryrRepository.saveAll(tohs);
	}

    /**
     * tbOrderDetail??? orderStatus ?????????, ??????????????????????????? ????????? ??? ??????????????????????????? ????????? ???
     * ?????? data??? ???????????? ??? ?????? ?????? (lspchm, lspchd, lspchs, lspchb, lsdpsp)
     * @return
     */
    @Transactional
	public boolean makePurchaseDataByOrder(TbOrderDetail tbOrderDetail, DirectOrImport di, String userId) {

		System.out.println("makePurchaseDataByOrder");

		System.out.println("di >>" + di);

        // 1. lsdpsp ???????????? (d ?????????, d??? ?????? b??? ?????????)
        List<Lsdpsp> lsdpspList = jpaLsdpspRepository.findByAssortIdAndItemId(tbOrderDetail.getAssortId(), tbOrderDetail.getItemId());
        // 2. dealTypeCd = 02 (??????????????? ?????? ????????????), purchaseGb = 01 (????????????) ??? ????????? ??????
		// if(di.equals(DirectOrImport.direct)){
		if (di.equals(DirectOrImport.purchase)) {

			System.out.println("??????");

            lsdpspList = lsdpspList.stream().filter(x->x.getDealtypeCd().equals(StringFactory.getGbTwo())&&x.getPurchaseGb().equals(StringFactory.getGbOne())).collect(Collectors.toList());
        }
        // 2. dealTypeCd = 02 (??????????????? ?????? ????????????), purchaseGb = 02 (????????????) ??? ????????? ??????
		// else if(di.equals(DirectOrImport.imports)){
		else if (di.equals(DirectOrImport.move)) {

			System.out.println("????????????");

            lsdpspList = lsdpspList.stream().filter(x->x.getDealtypeCd().equals(StringFactory.getGbTwo())&&x.getPurchaseGb().equals(StringFactory.getGbTwo())).collect(Collectors.toList());

			System.out.println(lsdpspList);

        }
        // 3. lspchb ??? purchaseStatus??? 01(???????????? ???????????? ????????? ?????? ??????)??? ????????? ?????????

        List<Lsdpsp> lsdpspList1 = new ArrayList<>();
        for(Lsdpsp lsdpsp : lsdpspList){
            List<Lspchb> lspchbList = lsdpsp.getLspchd().getLspchb();
            LocalDateTime date = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
            lspchbList = lspchbList.stream().filter(x->x.getPurchaseStatus().equals(StringFactory.getGbOne())&& x.getEffEndDt().compareTo(date)==0).collect(Collectors.toList());
            int num = lspchbList.size(); //.forEach(x -> System.out.println("??????????????? compare : "+date.compareTo(x.getEffEndDt())));
            if(num > 0){
                lsdpspList1.add(lsdpsp);
            }
        }
        lsdpspList = lsdpspList1;
        // 4. psp ??????
        Lsdpsp lsdpsp = null;
        Lspchd origLspchd = null;
        Lspchm origLspchm = null;
        for(Lsdpsp item : lsdpspList){
            // lsdpsp??? purchasePlanQty - purchaseTakeQty ?????? tbOrderDetail??? ?????? ????????? ???
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
        if(lsdpsp == null){ // ???????????? psp??? ?????? ??? -> ????????????
            return false;
        }
        // lspchm, lspchd, lspchb, lspchs ??????
		Lspchd lspchd = this.saveLspchByOrder(tbOrderDetail, origLspchm, origLspchd, di, userId);
        // ?????? lsdpsp update?????? ????????? lsdpsp ??????
        if(lspchd != null){
//            this.minusLsdpsp(lsdpsp, tbOrderDetail);
//        }
//        else{

			Lspchm lp = jpaLspchmRepository.findByPurchaseNo(lspchd.getPurchaseNo()).orElse(null);

			Ititmt it = new Ititmt(lp, lspchd, "batch to server (orderStatus)");
			it.setTempIndicateQty(lspchd.getPurchaseQty());

			it.setRegId(userId);
			it.setUpdId(userId);

			jpaItitmtRepository.save(it);

			// jpaItitmtRepository.save(it);

			this.updateLsdpspWhenCandidateExist(lsdpsp, lspchd, tbOrderDetail, userId);

        }
//        this.updateLspchbd(lsdpsp.getLspchd(), tbOrderDetail.getQty());
        // lspchm, s ??????
//        this.updateLspchs(lsdpsp.getPurchaseNo(), StringFactory.getGbOne()); // 01 ????????????

        return true;
    }

    /**
     * ?????? : ??????????????????????????? ?????? ?????? ?????? lsdpsp?????? ??????????????? ?????????????????? ??????
     */
	private void minusLsdpsp(Lsdpsp lsdpsp, TbOrderDetail tbOrderDetail, String userId) {
        lsdpsp.setPurchasePlanQty(lsdpsp.getPurchasePlanQty() - tbOrderDetail.getQty());

		lsdpsp.setUpdId(userId);

        jpaLsdpspRepository.save(lsdpsp);
    }

    /**
     * ?????????????????? lsdpsp ??????????????? ??????
     * ?????? lsdpsp??? purchasePlanQty??? ?????? ?????????????????? purchasePlanQty??? ?????? ????????? lsdpsp??? ?????????
     */
	private void updateLsdpspWhenCandidateExist(Lsdpsp lsdpsp, Lspchd lspchd, TbOrderDetail tbOrderDetail,
			String userId) {
        long qty = tbOrderDetail.getQty();
        lsdpsp.setPurchasePlanQty(lsdpsp.getPurchasePlanQty() - qty);
        Lsdpsp newLsdpsp = new Lsdpsp(this.getDepositPlanId(), lsdpsp);
		newLsdpsp.setRegId(userId);
        newLsdpsp.setPurchaseTakeQty(0l);
        newLsdpsp.setPurchasePlanQty(qty);
		newLsdpsp.setOrderId(tbOrderDetail.getOrderId());
		newLsdpsp.setOrderSeq(tbOrderDetail.getOrderSeq());
        newLsdpsp.setPurchaseNo(lspchd.getPurchaseNo());
        newLsdpsp.setPurchaseSeq(lsdpsp.getPurchaseSeq());
        newLsdpsp.setDealtypeCd(StringFactory.getGbThree()); // dealtypeCd 03(????????????????????????) ????????????

		lsdpsp.setUpdId(userId);

        jpaLsdpspRepository.save(lsdpsp);

		newLsdpsp.setUpdId(userId);

        jpaLsdpspRepository.save(newLsdpsp);

    }

    /**
     * ????????????????????? ?????? ??? ?????? data??? ????????? ??????
     */
	private Lspchd saveLspchByOrder(TbOrderDetail tbOrderDetail, Lspchm origLspchm, Lspchd origLspchd,
			DirectOrImport di, String userId) {
//        TbOrderMaster tbOrderMaster = tbOrderDetail.getTbOrderMaster();
		this.addMinusPurchase(tbOrderDetail, origLspchd, userId);
//        if(di.equals(DirectOrImport.imports)){
//            log.debug("??????????????? ????????? ?????? ????????? ???????????? ??????.");
//            return null;
//        }
        String purchaseNo = this.getPurchaseNo();
        Lspchm lspchm = new Lspchm(tbOrderDetail, di);

		lspchm.setRegId(userId);

        lspchm.setPurchaseNo(purchaseNo);
        lspchm.setSiteOrderNo(origLspchm.getSiteOrderNo());
        Lspchd lspchd = new Lspchd(tbOrderDetail, origLspchd);
		lspchd.setRegId(userId);

		lspchd.setOrderId(tbOrderDetail.getOrderId());
		lspchd.setOrderSeq(tbOrderDetail.getOrderSeq());
        lspchd.setPurchaseNo(purchaseNo);
        lspchd.setPurchaseSeq(StringFactory.getFourStartCd()); // 0001 ????????????
		lspchd.setMemo(Utilities.addDashInMiddle(origLspchd.getPurchaseNo(), origLspchd.getPurchaseSeq()));
        Lspchs lspchs = new Lspchs(lspchm, "regId"); // regId ?????? ????????????
		lspchs.setRegId(userId);
        Lspchb lspchb = new Lspchb(lspchd, "regId"); // regId ?????? ????????????
		lspchb.setRegId(userId);

		lspchm.setUpdId(userId);

        jpaLspchmRepository.save(lspchm);

		lspchd.setUpdId(userId);
        jpaLspchdRepository.save(lspchd);

		lspchs.setUpdId(userId);
        jpaLspchsRepository.save(lspchs);

		lspchb.setUpdId(userId);
        jpaLspchbRepository.save(lspchb);

        return lspchd;
    }

    /**
     * ?????? qty?????? ?????? lspchd??? ???????????? ??????
     */
	private void addMinusPurchase(TbOrderDetail tbOrderDetail, Lspchd origLspchd, String userId) {
        Lspchd lspchd = new Lspchd(tbOrderDetail, origLspchd);

		lspchd.setRegId(userId);

        lspchd.setPurchaseNo(origLspchd.getPurchaseNo());
        lspchd.setPurchaseSeq(Utilities.plusOne(origLspchd.getPurchaseSeq(),4));
		lspchd.setOwnerId(origLspchd.getOwnerId());
		lspchd.setPurchaseQty(-lspchd.getPurchaseQty());
		lspchd.setPurchaseUnitAmt(-lspchd.getPurchaseUnitAmt());
		lspchd.setPurchaseItemAmt(-lspchd.getPurchaseItemAmt());
		lspchd.setSetShipId(origLspchd.getPurchaseNo());
		lspchd.setSetShipSeq(origLspchd.getPurchaseSeq());

        Lspchb lspchb = new Lspchb(lspchd, "regId"); // regID ?????? ????????????

		lspchb.setRegId(userId);

		lspchd.setUpdId(userId);

        jpaLspchdRepository.save(lspchd);

		lspchb.setUpdId(userId);
        jpaLspchbRepository.save(lspchb);
    }

    /**
     * lspchs ?????? (?????? ??? row ??????)
     * @return
     */
	private Lspchs updateLspchs(Lspchs oldLspchs, String purchaseNo, String purchaseStatus, String userId) {
        oldLspchs.setEffEndDt(LocalDateTime.now());
        Lspchs newLspchs = new Lspchs(oldLspchs);

		newLspchs.setRegId(userId);

        newLspchs.setPurchaseNo(purchaseNo);
        newLspchs.setPurchaseStatus(purchaseStatus);

		oldLspchs.setUpdId(userId);

        jpaLspchsRepository.save(oldLspchs);

		newLspchs.setUpdId(userId);
        jpaLspchsRepository.save(newLspchs);

        return oldLspchs;
    }

    /**
     * lspchb,d ?????? (b??? ?????? ??? row ??????, d??? qty ??? ??????)
     * @return
     */
	private Lspchb updateLspchbd(Lspchd lspchd, long qty, String userId) {
        LocalDateTime doomDay = Utilities.strToLocalDateTime(StringFactory.getDoomDayT());
        Lspchb lspchb = lspchd.getLspchb().stream().filter(x->x.getEffEndDt().equals(doomDay)).collect(Collectors.toList()).get(0);
        lspchb.setEffEndDt(LocalDateTime.now());

        Lspchb newLspchb = new Lspchb(lspchb);

		newLspchb.setRegId(userId);

        long newQty = qty;
        newLspchb.setPurchaseQty(newQty);
        lspchd.setPurchaseQty(newQty);

		lspchb.setUpdId(userId);

        jpaLspchbRepository.save(lspchb);

		newLspchb.setUpdId(userId);
        jpaLspchbRepository.save(newLspchb);

		lspchd.setUpdId(userId);
        jpaLspchdRepository.save(lspchd);

        return lspchb;
    }

    /**
     * lsdpsp ??????, ??? row ?????? ??????
     * @return
     */
	private void updateLsdpsp(Lsdpsp lsdpsp, long qty, String userId) {
        long oldPlanQty = lsdpsp.getPurchasePlanQty();
        long oldTakeQty = lsdpsp.getPurchaseTakeQty();
        long newPurchasePlanQty = oldPlanQty - oldTakeQty;
//        lsdpsp.setPurchasePlanQty(oldTakeQty);
        String depositPlanId = this.getDepositPlanId();
        Lsdpsp newLsdpsp = new Lsdpsp(depositPlanId,lsdpsp);

		newLsdpsp.setRegId(userId);

        if(newPurchasePlanQty > 0){
            newLsdpsp.setPurchasePlanQty(newPurchasePlanQty);
            newLsdpsp.setPurchaseTakeQty(qty);

			newLsdpsp.setUpdId(userId);

            jpaLsdpspRepository.save(newLsdpsp);
        }

		lsdpsp.setUpdId(userId);
        jpaLsdpspRepository.save(lsdpsp);
    }

    /**
     *  depositService?????? ???????????? ?????????, ?????? ????????? ?????? ??? ????????????/???????????? ????????? ?????? lsdchm,b,s??? purchaseStatus??? ????????????.
     *  (01 : ??????, 03 : ????????????, 04 : ????????????)
     */
	public Lspchm changePurchaseStatus(String purchaseNo, List<Lsdpsp> lsdpspList, String userId) {
	    if(lsdpspList.size() == 0){
	        log.debug("purchaseStatus??? ????????? lsdpsp??? ???????????? ????????????.");
	        return null;
        }
//        List<Lsdpsp> newLsdpspList = jpaLsdpspRepository.findByPurchaseNo(purchaseNo);
        for(Lsdpsp lsdpsp : lsdpspList){
            long planQty = lsdpsp.getPurchasePlanQty();
            long takeQty = lsdpsp.getPurchaseTakeQty();
            Lspchd lspchd = lsdpsp.getLspchd();
            LocalDateTime doomDay = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
            List<Lspchb> lspchbList1 = lspchd.getLspchb();
            lspchbList1 = lspchbList1.stream().filter(x->x.getEffEndDt().compareTo(doomDay)==0).collect(Collectors.toList());
            Lspchb lspchb = lspchbList1.get(0);
            if(planQty - takeQty > 0){ // ???????????? : 03
				lspchb = this.updateLspchbdStatus(lspchb, StringFactory.getGbThree(), userId); // planStatus : 03?????? ??????
            }
            else if(planQty - takeQty == 0){ // ???????????? : 04
                lsdpsp.setPlanStatus(StringFactory.getGbFour()); // planStatus : 04??? ??????
				lspchb = this.updateLspchbdStatus(lspchb, StringFactory.getGbFour(), userId); // planStatus : 04??? ??????
            }
            else if(lspchd.getPurchaseQty() < 0){
			    throw new IllegalArgumentException("purchaseQty must bigger than 0..");
            }
//            lspchbList.add(lspchb);

			lspchd.setUpdId(userId);
            jpaLspchdRepository.save(lspchd);

			lsdpsp.setUpdId(userId);
            jpaLsdpspRepository.save(lsdpsp);
        }
        List<Lspchb> lspchbList = jpaLspchbRepository.findByPurchaseNo(purchaseNo);
        LocalDateTime doomDay = LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat()));
        lspchbList = lspchbList.stream().filter(x->x.getEffEndDt().compareTo(doomDay)==0).collect(Collectors.toList());

//        Lspchm lspchm = lsdpspList.get(0).getLspchd().getLspchm();
		Lspchm lspchm = jpaLspchmRepository.findById(purchaseNo).orElse(null);

		Lspchm newLspchm = this.changePurchaseStatusOfLspchm(lspchm, lspchbList, userId);
		this.updateLspchsStatus(lspchm, newLspchm.getPurchaseStatus(), userId);
        return lspchm;
    }

    /**
     * lspchb??? status??? ?????? ?????? ???????????? ????????? ??????
     */
	private Lspchb updateLspchbdStatus(Lspchb lspchb, String status, String userId) {
        Lspchb newLspchb = new Lspchb(lspchb);


		newLspchb.setRegId(userId);

        lspchb.setEffEndDt(LocalDateTime.now());
        newLspchb.setPurchaseStatus(status);

		lspchb.setUpdId(userId);

		jpaLspchbRepository.save(lspchb);

		newLspchb.setUpdId(userId);
        jpaLspchbRepository.save(newLspchb);

        return newLspchb;
    }

	/**
     * lspchd -> lspchb??? status??? ?????? ?????? ???????????? ????????? ??????
     */
	private Lspchb updateLspchbdStatus(Lspchd lspchd, String status, String userId) {
		Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(lspchd.getPurchaseNo(),
				lspchd.getPurchaseSeq(), LocalDateTime.parse(StringFactory.getDoomDay(),
						DateTimeFormatter.ofPattern(StringFactory.getDateFormat())));
        Lspchb newLspchb = new Lspchb(lspchb);

		newLspchb.setRegId(userId);

        lspchb.setEffEndDt(LocalDateTime.now());
        newLspchb.setPurchaseStatus(status);

		lspchb.setUpdId(userId);

		jpaLspchbRepository.save(lspchb);

		newLspchb.setUpdId(userId);
        jpaLspchbRepository.save(newLspchb);

        return newLspchb;
    }

    /**
     * lspchs??? status??? ?????? ?????? ???????????? ????????? ??????
     */
	private Lspchs updateLspchsStatus(Lspchm lspchm, String status, String userId) {
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(), LocalDateTime.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat())));
        Lspchs newLspchs = new Lspchs(lspchs);

		newLspchs.setRegId(userId);

        lspchs.setEffEndDt(LocalDateTime.now());
        newLspchs.setPurchaseStatus(status);

		lspchs.setUpdId(userId);

        jpaLspchsRepository.save(lspchs);

		newLspchs.setUpdId(userId);
        jpaLspchsRepository.save(newLspchs);

        return newLspchs;
    }
    
	private Lspchm cancelPurchaseStatusOfLspchm(Lspchm lspchm, String userId) {
//      Lspchm newLspchm = new Lspchm(lspchm);

		List<Lspchb> lspchbList = jpaLspchbRepository.findByPurchaseNoAndEffEndDt(lspchm.getPurchaseNo(), LocalDateTime
				.parse(StringFactory.getDoomDay(), DateTimeFormatter.ofPattern(StringFactory.getDateFormat())));

		System.out.println("lspchbList ==> " + lspchbList.size());

		// x -> x.getPurchaseStatus().equals("04")
		Stream<Lspchb> l05 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("05"));

		String purchaseStatus = "";
		if (lspchbList.size() == l05.count()) {
			purchaseStatus = "05";
			lspchm.setPurchaseStatus(purchaseStatus);

			System.out.println("lspchm ==> " + lspchm);

			lspchm.setUpdId(userId);

			jpaLspchmRepository.save(lspchm);
			updateLspchsStatus(lspchm, purchaseStatus, userId);

		}

		return lspchm;

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


	}

    /**
     * lspchb ????????? ?????? ???????????? lspchm??? purchaseStatus??? ??????????????? ??????
     * ?????? purchaseNo??? b??? ?????? ??????????????? m??? ????????????, ???????????? ??????????????? m??? ????????????.
     */
	private Lspchm changePurchaseStatusOfLspchm(Lspchm lspchm, List<Lspchb> lspchbList, String userId) {
//        Lspchm newLspchm = new Lspchm(lspchm);

		System.out.println("lspchbList ==> " + lspchbList.size());

		// x -> x.getPurchaseStatus().equals("04")
//		Stream<Lspchb> l05 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("05"));
//		Stream<Lspchb> l04 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("04"));
//		Stream<Lspchb> l01 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("01"));
        long l05 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("05")).count();
        long l04 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("04")).count();
        long l01 = lspchbList.stream().filter(x -> x.getPurchaseStatus().equals("01")).count();
        int size = lspchbList.size();
		String purchaseStatus = "";
		if (size == (l04 + l05)) {
			purchaseStatus = "04";
		} else if (size == (l01 + l05)) {
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

		lspchm.setUpdId(userId);

		jpaLspchmRepository.save(lspchm);
		return lspchm;
    }

	/**
	 * ?????????????????? ??????, ?????????????????? ????????? ???????????? ?????? data??? ????????? ??????
	 */
	public void makePurchaseDataFromOrderMoveSave2(List<Lsshpd> moveList, String userId) {

		// ???????????? ????????? ?????? ????????? ????????? ?????????.

		// lspchm,s,d,b insert
		for (int i = 0; i < moveList.size(); i++) {
			Lsshpd move = moveList.get(i);

		// ???????????? ??????????????? ?????????????????????.

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
			if (lsshpm.getShipOrderGb().equals("01")) { // 01 : ??????, 02 : ??????
				// ??????????????????
				orderGoodsType = "01";
			}
			else if (lsshpm.getShipOrderGb().equals("02")) {
				// ??????????????????
				orderGoodsType = "02";
			}

			Lspchm lspchm = new Lspchm(orderGoodsType, purchaseNo);

			lspchm.setRegId(userId);

			// lspchm.setDealtypeCd(StringFactory.getGbOne()); // 01 : ????????????, 02 : ????????????, 03 :
			// ???????????? ???????????? (01 ????????????)
			// lspchm.setSiteOrderNo(tbOrderMaster.getChannelOrderNo());
			// lspchm.setPurchaseGb(StringFactory.getGbTwo()); // ??????????????? ?????? 02 ??? ??????

            String OStorageId = "";

            // lspchm insert
            if (lsshpm.getShipOrderGb().equals("01")) {
                // ??????????????????
                OStorageId = tbOrderDetail.getStorageId();
            } else if (lsshpm.getShipOrderGb().equals("02")) {
                // ??????????????????
                OStorageId = lsshpm.getOStorageId();
            }

            lspchm.setStoreCd(OStorageId); // ?????????
            lspchm.setOStoreCd(lsshpm.getStorageId()); //?????????

			// lsdpsd.getLsdpsm().getStoreCd();

			// lspchm??? purchaseRemark, siteOrderNo, storeCd, oStoreCd set ?????????
//            lspchm.setPurchaseRemark(receiveLsdpsm.getRegId());

			Lspchs lspchs = new Lspchs(lspchm, "1");

			lspchs.setRegId(userId);

			// String purchaseSeq = StringUtils.leftPad(Integer.toString(i + 1), 4, '0');

			// ???????????????????????? ??????????????????.
			String purchaseSeq = StringUtils.leftPad(Integer.toString(1), 4, '0');

			// todo ????????? ???????????? ?????????????????? purchaseGb=02 ????????? ??????????????????.????????? ????????? ???????????? ??????????????????

			// if(jpaLspchdRepository.findByOrderIdAndOrderSeq(tbOrderDetail.getOrderId(),tbOrderDetail.getOrderSeq())
			// != null){
			// log.debug("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????!");
			// return;
			// }
			Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, lsshpd, tbOrderDetail);
			Lspchb lspchb = new Lspchb(lspchd, "1");

			lspchd.setRegId(userId);
			lspchb.setRegId(userId);

            lsshpd.setPurchaseNo(lspchd.getPurchaseNo());
            lsshpd.setPurchaseSeq(lspchd.getPurchaseSeq());

			lsshpd.setUpdId(userId);

            jpaLsshpdRepository.save(lsshpd);

			lspchm.setUpdId(userId);

			jpaLspchmRepository.save(lspchm);

			lspchs.setUpdId(userId);
			jpaLspchsRepository.save(lspchs);

			lspchd.setUpdId(userId);
			jpaLspchdRepository.save(lspchd);

			lspchb.setUpdId(userId);
			jpaLspchbRepository.save(lspchb);

//orderGoodsType 01 ?????? 02 ??????

			// lsdpsp insert
			String depositPlanId = this.getDepositPlanId();
			// todo: 2021-10-14 ????????? ??????????????????.
			// ?????????????????? ????????? ???????????????.
			Lsdpsp lsdpsp = new Lsdpsp(depositPlanId, lspchd, "1", orderGoodsType);

			lsdpsp.setRegId(userId);

			// lsdpsp.setLspchd(lspchd);

			lsdpsp.setUpdId(userId);

			jpaLsdpspRepository.save(lsdpsp);

//			if (lsshpm.getShipOrderGb().equals("01")) {
//				// ??????????????????
//				lspchm.setStoreCd(tbOrderDetail.getStorageId()); // ?????????
//			} else if (lsshpm.getShipOrderGb().equals("02")) {
//				// ??????????????????
//				lspchm.setStoreCd(lsshpm.getOStorageId());
//			}

			// ititmt qty update
			Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(
					lspchd.getAssortId(), lspchd.getItemId(), OStorageId,
					StringFactory.getStrEleven(), LocalDateTime.now());
			if (ititmt == null) {
				ititmt = new Ititmt(lspchm, lspchd, "1");
				ititmt.setRegId(userId);
			} else {
				ititmt.setTempQty(ititmt.getTempQty() + lspchd.getPurchaseQty());
			}

			if (lsshpm.getShipOrderGb().equals("01")) {
				// ?????????????????? ????????? ?????????????????? ????????? ???????????? ??????.????????????????????? ????????? ???????????? ????????????.
				ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + lspchd.getPurchaseQty());
			}

			ititmt.setUpdId(userId);

			jpaItitmtRepository.save(ititmt);

		}
	}

    /**
     * ???????????? ????????? ???????????? ?????? data??? ????????? ??????
     */
	// 2021-10-18 ???????????? ????????? ???????????????.
	/*
	 * public void makePurchaseDataFromOrderMoveSave(List<Lsdpsd> lsdpsdList,
	 * List<OrderMoveSaveData.Move> moveList) {
	 * 
	 * // 2021-10-18 ???????????? ????????? ???????????????.
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
	 * // lspchm insert if (lsshpm.getShipOrderGb().equals("01")) { // ??????????????????
	 * orderGoodsType = "01"; } else if (lsshpm.getShipOrderGb().equals("02")) { //
	 * ?????????????????? orderGoodsType = "02"; }
	 * 
	 * // lspchm insert Lspchm lspchm = new Lspchm(orderGoodsType,purchaseNo);
	 * lspchm.setDealtypeCd(StringFactory.getGbOne()); // 01 : ????????????, 02 : ????????????, 03 :
	 * ???????????? ???????????? (01 ????????????) //
	 * lspchm.setSiteOrderNo(tbOrderMaster.getChannelOrderNo()); //
	 * lspchm.setPurchaseGb(StringFactory.getGbTwo()); // ??????????????? ?????? 02 ??? ??????
	 * lspchm.setStoreCd(tbOrderDetail.getStorageId());
	 * lspchm.setOStoreCd(itemLsdpsd.getLsdpsm().getStoreCd());
	 * 
	 * // lsdpsd.getLsdpsm().getStoreCd();
	 * 
	 * // lspchm??? purchaseRemark, siteOrderNo, storeCd, oStoreCd set ????????? //
	 * lspchm.setPurchaseRemark(receiveLsdpsm.getRegId());
	 * 
	 * Lspchs lspchs = new Lspchs(lspchm, null);
	 * 
	 * String purchaseSeq = StringUtils.leftPad(Integer.toString(i+1),4,'0');
	 * 
	 * // todo ????????? ???????????? ?????????????????? purchaseGb=02 ????????? ??????????????????.????????? ????????? ???????????? ??????????????????
	 * 
	 * //
	 * if(jpaLspchdRepository.findByOrderIdAndOrderSeq(tbOrderDetail.getOrderId(),
	 * tbOrderDetail.getOrderSeq()) // != null){ //
	 * log.debug("?????? ?????? ????????? ?????? ?????? ???????????? ???????????????!"); // return; // } Lspchd lspchd = new
	 * Lspchd(purchaseNo, purchaseSeq, itemLsdpsd, tbOrderDetail); Lspchb lspchb =
	 * new Lspchb(lspchd, null);
	 * 
	 * jpaLspchmRepository.save(lspchm); jpaLspchsRepository.save(lspchs);
	 * jpaLspchdRepository.save(lspchd); jpaLspchbRepository.save(lspchb);
	 * 
	 * // lsdpsp insert String depositPlanId = this.getDepositPlanId(); // todo:
	 * 2021-10-14 ????????? ??????????????????. // ?????????????????? ????????? ???????????????. Lsdpsp lsdpsp = new
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
//     * ???????????? ????????? ???????????? ?????? data??? ????????? ??????
//     */
//    public void makePurchaseDataFromGoodsMoveSave(String regId, GoodsMoveSaveData goodsMoveSaveData, List<GoodsMoveSaveData.Goods> newGoodsList) {
//        String purchaseNo = this.getPurchaseNo();
//        List<GoodsMoveSaveData.Goods> goodsList = goodsMoveSaveData.getGoods();
//
//        // lspchm insert
//        Lspchm lspchm = new Lspchm(purchaseNo);
//        lspchm.setDealtypeCd(StringFactory.getGbTwo()); // 01 : ????????????, 02 : ????????????, 03 : ???????????? ???????????? (02 ????????????)
//        // lspchm??? purchaseRemark, siteOrderNo, storeCd, oStoreCd set ?????????
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
     * ???????????? ????????? ???????????? ?????? data??? ????????? ??????
     */
	public Lsdpsp makePurchaseDataFromGoodsMoveSave(String regId, LocalDateTime purchaseDt, Lsshpm lsshpm,
			Lsshpd lsshpd, String userId) {
        String purchaseNo = this.getPurchaseNo();

        // lspchm insert
        Lspchm lspchm = new Lspchm(purchaseNo, lsshpm, regId);

		lspchm.setRegId(userId);

        lspchm.setPurchaseDt(purchaseDt); // ititmc.effEndDt
        lspchm.setDealtypeCd(StringFactory.getGbTwo()); // 01 : ????????????, 02 : ????????????, 03 : ???????????? ???????????? (02 ????????????)
        // lspchm??? purchaseRemark, siteOrderNo, storeCd, oStoreCd set ?????????
        lspchm.setPurchaseRemark(regId);

        // lspchs insert
        Lspchs lspchs = new Lspchs(lspchm, regId);

		lspchs.setRegId(userId);

		lspchm.setUpdId(userId);
        jpaLspchmRepository.save(lspchm);

		lspchs.setUpdId(userId);
        jpaLspchsRepository.save(lspchs);

        // lspchd,b insert
        String purchaseSeq = StringUtils.leftPad(Integer.toString(1),4,'0');
        Lspchd lspchd = new Lspchd(purchaseNo, purchaseSeq, lsshpd, regId);

		lspchd.setRegId(userId);

        Lspchb lspchb = new Lspchb(lspchd, regId);

		lspchb.setRegId(userId);

        List<Lspchb> lspchbList = new ArrayList<>();
        lspchbList.add(lspchb);
        lspchd.setLspchb(lspchbList);
        lspchd.setLspchm(lspchm);

		lspchd.setUpdId(userId);
        jpaLspchdRepository.save(lspchd);

		lspchb.setUpdId(userId);
        jpaLspchbRepository.save(lspchb);

        // lsdpsp insert
        String depositPlanId = this.getDepositPlanId();

        Lsdpsp lsdpsp = new Lsdpsp(depositPlanId, lspchd, regId);

        lsdpsp.setLspchd(lspchd);

		lsdpsp.setRegId(userId);

		lsdpsp.setUpdId(userId);

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

		ititmt.setUpdId(userId);
        jpaItitmtRepository.save(ititmt);

        return lsdpsp;
    }

    /**
     * purchaseNo ?????? ??????
     */
    private String getPurchaseNo(){
        String purchaseNo = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLspchm());
        purchaseNo = Utilities.getStringNo('C',purchaseNo,9);
        return purchaseNo;
    }

    /**
     * depositPlanId ?????? ??????
     */
    private String getDepositPlanId(){
        String depositPlanId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsp());
        depositPlanId = StringUtils.leftPad(depositPlanId,9,'0');
        return depositPlanId;
    }

    public boolean cancelOrderPurchase(String purchaseNo, String purchaseSeq, String userId) {
        Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(purchaseNo, purchaseSeq);
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", lspchd.getOrderId());
        map.put("orderSeq", lspchd.getOrderSeq());
        return this.innerCancelOrderPurchase(map, userId);
    }

    /**
     * ?????? ??????
     */
    @Transactional
    public void cancelOrderPurchase(PurchaseDetailCancelResponse purchaseDetailCancelResponse){
        List<String> seqList = new ArrayList<>();
        for (int i = 0; i < purchaseDetailCancelResponse.getItems().size() ; i++) {
            seqList.add(purchaseDetailCancelResponse.getItems().get(i).getPurchaseSeq());
        }
        List<Lspchd> lspchdList = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq2(purchaseDetailCancelResponse.getPurchaseNo(),
                seqList);
        for(Lspchd l : lspchdList){
            HashMap<String , Object> map = new HashMap<>();
            map.put("orderId", l.getOrderId());
            map.put("orderSeq", l.getOrderSeq());
            this.innerCancelOrderPurchase(map, purchaseDetailCancelResponse.getUserId());
        }
    }
//	@Transactional
	public boolean innerCancelOrderPurchase(HashMap<String, Object> param, String userId) {

		// ????????????
		// ????????????
		// ????????????
		// ???????????????

		String orderId = param.get("orderId").toString();
		String orderSeq = param.get("orderSeq").toString();
//		String cancelGb = param.get("cancelGb").toString();
//		String cancelMsg = param.get("cancelMsg").toString();

		//??????????????? ???????????? ????????????
		List<Lspchd> l = jpaLspchdRepository.findItemByOrderIdAndOrderSeq2(orderId, orderSeq);

		if (l.size() != 1) {
			System.out.println("??????????????? ??????!!!");
			throw new RuntimeException("??????????????? ??????!!!.");
			// return false;
		}

		Lspchd o = l.get(0);
		Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(o.getPurchaseNo()).orElse(null);

		
		List<Lsdpsp> l2 = jpaLsdpspRepository.findItemByPurchaseNoAndPurchaseSeq(o.getPurchaseNo(), o.getPurchaseSeq());

		Lsdpsp lp = null;
		
		if(l2.size()!=1) {
			System.out.println("????????????????????? ????????????!!!");
			throw new RuntimeException("????????????????????? ????????????!!!.");
			// return false;
		}else {
			lp = l2.get(0);
			
			if(! lp.getPlanStatus().equals("01")) {
				System.out.println("????????????????????? ????????????!!!");
				throw new RuntimeException("????????????????????? ????????????!!!.");
				// return false;
			}
		}
		
		// ???????????? ????????? ??????
		
		updateLspchbdStatus(o, "05", userId);

		
		// ??????????????????
		lp.setPlanStatus("05");
		lp.setPurchasePlanQty(0L);
		lp.setPurchaseTakeQty(0L);

		lp.setUpdId(userId);

		jpaLsdpspRepository.save(lp);

		// ititmt??????

		// ititmt qty update

		System.out.println(

				o.getAssortId() + " : " + o.getItemId() + " : " + lspchm.getStoreCd() + " : "
						+ StringFactory.getStrEleven() + " : " + lspchm.getPurchaseDt());

		Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(o.getAssortId(),
				o.getItemId(), lspchm.getStoreCd(), StringFactory.getStrEleven(), lspchm.getPurchaseDt());

		if (ititmt == null) {
			System.out.println("???????????? ????????????");
			throw new RuntimeException("???????????? ????????????");
			// return false;
		}

		Long tempIndQty = ititmt.getTempIndicateQty() == null ? 0L : ititmt.getTempIndicateQty();

		Long tempQty = ititmt.getTempQty() == null ? 0L : ititmt.getTempQty();

		if (tempIndQty == 0 || tempIndQty == 0) {
			System.out.println("???????????? ???????????? ??????");
			throw new RuntimeException("???????????? ???????????? ??????");
			// return false;
		}

		ititmt.setTempIndicateQty(tempIndQty - o.getPurchaseQty());
		ititmt.setTempQty(tempQty - o.getPurchaseQty());

		ititmt.setUpdId(userId);

		jpaItitmtRepository.save(ititmt);

		// ??????????????? ?????????????????? ????????? ????????? ?????? ??????????????? ???????????? ???????????? ??????
		cancelPurchaseStatusOfLspchm(lspchm, userId);


		// ???????????? ????????????
		this.updateOrderStatusCd(o.getOrderId(), o.getOrderSeq(), StringFactory.getStrB01(), userId);
		return true;
	}

    /**
     * ?????? ????????? ?????????(vendorId) ????????????
     */
    public String updateVendorId(String purchaseNo, String purchaseSeq, String vendorId, String userId) {
        Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(purchaseNo, purchaseSeq);
        lspchd.setVendorId(vendorId);
        this.updateLspchbd(lspchd, lspchd.getPurchaseQty(), userId);
        return Utilities.addDashInMiddle(lspchd.getPurchaseNo(), lspchd.getPurchaseSeq());
    }
}
