package io.spring.service.purchase;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.purchase.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.purchase.entity.*;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.model.purchase.response.PurchaseSelectDetailResponse;
import io.spring.service.common.JpaCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JpaPurchaseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JpaLspchmRepository jpaLspchmRepository;
    @Autowired
    private JpaLsdpspRepository jpaLsdpspRepository;
    @Autowired
    private JpaLspchbRepository jpaLspchbRepository;
    @Autowired
    private JpaLspchdRepository jpaLspchdRepository;
    @Autowired
    private JpaLspchsRepository jpaLspchsRepository;
    @Autowired
    private JpaItitmtRepository jpaItitmtRepository;
    @Autowired
    private JpaCommonService jpaCommonService;
    @Autowired
    private JpaSequenceDataRepository jpaSequenceDataRepository;

    /**
     * 21-05-03 Pecan
     * 발주 insert/update 시퀀스 함수
     * @param purchaseInsertRequest
     * @return String
     */
    @Transactional
    public String savePurchaseSquence(PurchaseInsertRequest purchaseInsertRequest) {
        // lspchm (발주마스터)
        System.out.println("-----"+purchaseInsertRequest.getPurchaseNo());
        Lspchm lspchm = this.saveLspchm(purchaseInsertRequest);
        // lspchs (발주 상태 이력)
        Lspchs lspchs = this.saveLspchs(purchaseInsertRequest);
        // lspchd (발주 디테일)
        List<Lspchd> lspchdList = this.saveLspchd(purchaseInsertRequest);
        // lspchb (발주 디테일 이력)
        List<Lspchb> lspchbList = this.saveLspchb(purchaseInsertRequest);
        // lsdpsp (입고 예정)
        List<Lsdpsp> lsdpsp = this.saveLsdpsp(purchaseInsertRequest);
        // ititmt (예정 재고)
        List<Ititmt> ititmt = this.saveItitmt(purchaseInsertRequest);
        return lspchm.getPurchaseNo();
    }

    private Lspchm saveLspchm(PurchaseInsertRequest purchaseInsertRequest) {
        Lspchm lspchm = jpaLspchmRepository.findByPurchaseNo(purchaseInsertRequest.getPurchaseNo()).orElseGet(() -> null);
        if(lspchm == null){ // insert
            lspchm = new Lspchm(purchaseInsertRequest);
        }
        else { // update
            lspchm.setPurchaseDt(purchaseInsertRequest.getPurchaseDt());
            lspchm.setEffEndDt(new Date());
            lspchm.setPurchaseStatus(purchaseInsertRequest.getPurchaseStatus());
            lspchm.setPurchaseRemark(purchaseInsertRequest.getPurchaseRemark());
//            lspchm.setSiteGb(purchaseInsertRequest.getSiteGb());
//            lspchm.setVendorId(purchaseInsertRequest.getVendorId());
            lspchm.setSiteOrderNo(purchaseInsertRequest.getSiteOrderNo());
//            lspchm.setSiteTrackno(purchaseInsertRequest.getSiteTrackno());
            lspchm.setNewLocalPrice(purchaseInsertRequest.getLocalPrice());
            lspchm.setNewLocalTax(purchaseInsertRequest.getLocalTax());
            lspchm.setNewDisPrice(purchaseInsertRequest.getDisPrice());
//            lspchm.setPurchaseGb(purchaseInsertRequest.getPurchaseGb());
            lspchm.setStoreCd(purchaseInsertRequest.getStoreCd());
            lspchm.setTerms(purchaseInsertRequest.getTerms());
            lspchm.setDelivery(purchaseInsertRequest.getDelivery());
            lspchm.setPayment(purchaseInsertRequest.getPayment());
            lspchm.setCarrier(purchaseInsertRequest.getCarrier());
        }
        jpaLspchmRepository.save(lspchm);
        return lspchm;
    }

    private Lspchs saveLspchs(PurchaseInsertRequest purchaseInsertRequest) {
        Date effEndDt = null;
        try
        {
            effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        }
        catch (Exception e){
            logger.debug(e.getMessage());
        }
        Lspchs lspchs = jpaLspchsRepository.findByPurchaseNoAndEffEndDt(purchaseInsertRequest.getPurchaseNo(), effEndDt);
        if(lspchs == null){ // insert
            lspchs = new Lspchs(purchaseInsertRequest);
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
        lspchs.setPurchaseNo(purchaseInsertRequest.getPurchaseNo());
        lspchs.setPurchaseStatus(purchaseInsertRequest.getPurchaseStatus());
        jpaLspchsRepository.save(lspchs);
        return lspchs;
    }

    private List<Lspchd> saveLspchd(PurchaseInsertRequest purchaseInsertRequest) {
        List<Lspchd> lspchdList = new ArrayList<>();
        for(PurchaseInsertRequest.Items item : purchaseInsertRequest.getItems()){
            Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequest.getPurchaseNo(), item.getPurchaseSeq() == null? null:item.getPurchaseSeq());
            if(lspchd == null){ // insert
                String purchaseSeq = jpaLspchdRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequest.getPurchaseNo());
                if(purchaseSeq == null){
                    purchaseSeq = StringFactory.getFourStartCd();
                }
                else {
                    purchaseSeq = Utilities.plusOne(purchaseSeq, 4);
                }
                lspchd = new Lspchd(purchaseInsertRequest.getPurchaseNo(), purchaseSeq);
            }
            lspchd.setPurchaseQty(item.getPurchaseQty());
            lspchd.setPurchaseUnitAmt(item.getPurchaseUnitAmt());
            lspchd.setAssortId(item.getAssortId());
            lspchd.setItemId(item.getItemId());
            jpaLspchdRepository.save(lspchd);
            lspchdList.add(lspchd);
        }
        return lspchdList;
    }

    private List<Lspchb> saveLspchb(PurchaseInsertRequest purchaseInsertRequest) {
        List<Lspchb> lspchbList = new ArrayList<>();
        Date effEndDt = null;
        for(PurchaseInsertRequest.Items items: purchaseInsertRequest.getItems()){
            Date doomDate = null;
            try{
                doomDate = Utilities.getStringToDate(StringFactory.getDoomDay());
            }
            catch(Exception e){
                logger.debug(e.getMessage());
            }
            Lspchb lspchb = jpaLspchbRepository.findByPurchaseNoAndPurchaseSeqAndEffEndDt(purchaseInsertRequest.getPurchaseNo(), items.getPurchaseSeq(), doomDate);
            if(lspchb == null){ // insert
                lspchb = new Lspchb(purchaseInsertRequest);
                String purchaseSeq = jpaLspchbRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequest.getPurchaseNo());
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
            lspchb.setPurchaseNo(purchaseInsertRequest.getPurchaseNo());
            lspchb.setPurchaseStatus(items.getPurchaseStatus());
            lspchb.setCancelGb(StringFactory.getNinetyNine()); // 추후 수정
            jpaLspchbRepository.save(lspchb);
            lspchbList.add(lspchb);
        }
        return lspchbList;
    }

    private List<Lsdpsp> saveLsdpsp(PurchaseInsertRequest purchaseInsertRequest) {
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(PurchaseInsertRequest.Items items : purchaseInsertRequest.getItems()){
            System.out.println("items.getPurchaseSeq() : "+items.getPurchaseSeq());
            Lsdpsp lsdpsp = items.getPurchaseSeq() == null || items.getPurchaseSeq().equals("")? null : jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(purchaseInsertRequest.getPurchaseNo(), items.getPurchaseSeq());
            if(lsdpsp == null){ // insert
                String depositPlanId = jpaCommonService.getNumberId(purchaseInsertRequest.getDepositPlanId(), StringFactory.getDepositPlanId(), StringFactory.getIntNine());
                purchaseInsertRequest.setDepositPlanId(depositPlanId); // depositPlanId 채번
                String seq = jpaLsdpspRepository.findMaxPurchaseSeqByPurchaseNo(purchaseInsertRequest.getPurchaseNo());
                if(seq == null){
                    seq = StringFactory.getFourStartCd();
                }
                else{
                    seq = Utilities.plusOne(seq, 4);
                }
                items.setPurchaseSeq(seq);
                lsdpsp = new Lsdpsp(purchaseInsertRequest, items);
                purchaseInsertRequest.setDepositPlanId(null);
            }
            else{ // update
                lsdpsp.setPurchaseNo(purchaseInsertRequest.getPurchaseNo());
                lsdpsp.setPurchaseSeq(items.getPurchaseSeq());
                lsdpsp.setPurchasePlanQty(purchaseInsertRequest.getPurchasePlanQty());
                lsdpsp.setPurchaseTakeQty(purchaseInsertRequest.getPurchaseTakeQty());
                lsdpsp.setAssortId(items.getAssortId());
                lsdpsp.setItemId(items.getItemId());
                lsdpsp.setPlanStatus(purchaseInsertRequest.getPlanStatus());
            }
            lsdpspList.add(lsdpsp);
            jpaLsdpspRepository.save(lsdpsp);
        }
        return lsdpspList;
    }

    private List<Ititmt> saveItitmt(PurchaseInsertRequest purchaseInsertRequest) {
        List<Ititmt> ititmtList = new ArrayList<>();
        for(PurchaseInsertRequest.Items items : purchaseInsertRequest.getItems()){
            ItitmtId ititmtId = new ItitmtId(purchaseInsertRequest, items);
            Ititmt ititmt = jpaItitmtRepository.findById(ititmtId).orElseGet(() -> null);
            if(ititmt == null){ // insert
                ititmt = new Ititmt(ititmtId);
                ititmt.setTempIndicateQty(0L);
                ititmt.setTempQty(1L);
            }
            else{ // update
                ititmt.setTempQty(ititmt.getTempQty()+1);
            }
            ititmt.setStockGb(purchaseInsertRequest.getStockGb());
            ititmt.setStockAmt(purchaseInsertRequest.getStockAmt());
            ititmt.setVendorId(purchaseInsertRequest.getVendorId());
            ititmt.setSiteGb(purchaseInsertRequest.getSiteGb());
            jpaItitmtRepository.save(ititmt);
            ititmtList.add(ititmt);
        }
        return ititmtList;
    }

    public PurchaseSelectDetailResponse getPurchaseDetailPage(String purchaseNo) {
        Lspchm lspchm = jpaLspchmRepository.findById(purchaseNo).get();
        List<PurchaseSelectDetailResponse.Items> itemsList = makeItemsList(lspchm.getLspchdList());
        PurchaseSelectDetailResponse purchaseSelectDetailResponse = new PurchaseSelectDetailResponse(lspchm);
        purchaseSelectDetailResponse.setItems(itemsList);
        return purchaseSelectDetailResponse;
    }

    private List<PurchaseSelectDetailResponse.Items> makeItemsList(List<Lspchd> lspchdList) {
        List<PurchaseSelectDetailResponse.Items> itemsList = new ArrayList<>();
        for(Lspchd lspchd : lspchdList){
            PurchaseSelectDetailResponse.Items item = new PurchaseSelectDetailResponse.Items();
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

}
