package io.spring.service.purchase;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.purchase.*;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.purchase.entity.*;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import io.spring.service.common.JpaCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
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
        Lspchm lspchm = this.saveLspchm(purchaseInsertRequest);
        // lspchs (발주 상태 이력)
        Lspchs lspchs = this.saveLspchs(purchaseInsertRequest);
        // lspchd (발주 디테일)
        List<Lspchd> lspchd = this.saveLspchd(purchaseInsertRequest);
        // lspchb (발주 디테일 이력)
        Lspchb lspchb = this.saveLspchb(purchaseInsertRequest);
        // lsdpsp (입고 예정)
        Lsdpsp lsdpsp = this.saveLsdpsp(purchaseInsertRequest);
        // ititmt (예정 재고)
        Ititmt ititmt = this.saveItitmt(purchaseInsertRequest);
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
            effEndDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59"); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
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
            Lspchd lspchd = jpaLspchdRepository.findByAssortIdAndItemId(item.getAssortId(), item.getItemId());
            if(lspchd == null){
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
            lspchdList.add(lspchd);
        }
        return lspchdList;
    }

    private Lspchb saveLspchb(PurchaseInsertRequest purchaseInsertRequest) {

        return null;
    }

    private Lsdpsp saveLsdpsp(PurchaseInsertRequest purchaseInsertRequest) {

        return null;
    }

    private Ititmt saveItitmt(PurchaseInsertRequest purchaseInsertRequest) {

        return null;
    }

    /**
     * Table 초기화 함수
     */
    public void initTables(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById(StringFactory.getPurchaseSeqStr());
        SequenceData seq = op.get();
        jpaLspchmRepository.deleteAll();
        jpaLspchsRepository.deleteAll();
        jpaLspchmRepository.deleteAll();
        jpaLspchbRepository.deleteAll();
        jpaLsdpspRepository.deleteAll();
        jpaItitmtRepository.deleteAll();
        seq.setSequenceCurValue("0");
        jpaSequenceDataRepository.save(seq);
    }
}
