package io.spring.service.deposit;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.*;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.deposit.entity.*;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositSelectDetailResponseData;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaDepositService {
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsdpssRepository jpaLsdpssRepository;
    private final JpaLsdpdsRepository jpaLsdpdsRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;

    @Transactional
    public String sequenceInsertDeposit(DepositInsertRequestData depositInsertRequestData){
        Lsdpsm lsdpsm = this.saveLsdpsm(depositInsertRequestData);// lsdpsm (입고 마스터)
        List<Lsdpsd> lsdpsdList = this.saveLsdpsd(depositInsertRequestData);// lsdpsd (입고 디테일)
        this.saveLsdpss(depositInsertRequestData);// lsdpss (입고 마스터 이력)
        this.saveLsdpds(depositInsertRequestData);// lsdpds (입고 디테일 이력)
        List<Lsdpsp> lsdpspList = this.saveLsdpsp(depositInsertRequestData);// lsdpsp (입고 예정)
        List<Ititmc> ititmcList = this.saveItitmc(depositInsertRequestData);// ititmc (상품 재고)
        List<Ititmt> ititmtList = this.saveItitmt(depositInsertRequestData);// ititmt (입고예정재고)
        return depositInsertRequestData.getDepositNo();
    }

    private Lsdpsm saveLsdpsm(DepositInsertRequestData depositInsertRequestData){
        Lsdpsm lsdpsm = new Lsdpsm(depositInsertRequestData);
        jpaLsdpsmRepository.save(lsdpsm);
        return lsdpsm;
    }

    private List<Lsdpsd> saveLsdpsd(DepositInsertRequestData depositInsertRequestData){
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
            if(item.getDepositSeq() == null || item.getDepositSeq().equals("")){
                String depositSeq = jpaLsdpsdRepository.findMaxDepositSeqByDepositNo(depositInsertRequestData.getDepositNo());
                if(depositSeq == null){
                    depositSeq = StringUtils.leftPad("1", 4, '0');
                }
                else{
                    depositSeq = Utilities.plusOne(depositSeq, 4);
                }
                item.setDepositSeq(depositSeq);
            }
            Lsdpsd lsdpsd = new Lsdpsd(depositInsertRequestData.getDepositNo(), item);
            jpaLsdpsdRepository.save(lsdpsd);
            lsdpsdList.add(lsdpsd);
        }
        return lsdpsdList;
    }

    private void saveLsdpss(DepositInsertRequestData depositInsertRequestData){
        Lsdpss lsdpss = new Lsdpss(depositInsertRequestData);
        jpaLsdpssRepository.save(lsdpss);
    }

    private void saveLsdpds(DepositInsertRequestData depositInsertRequestData) {
        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
            String depositSeq = jpaLsdpdsRepository.findMaxDepositSeqByDepositNo(depositInsertRequestData.getDepositNo());
            if(depositSeq == null){
                depositSeq = StringUtils.leftPad("1",4,'0');
            }
            else{
                depositSeq = Utilities.plusOne(depositSeq, 4);
            }
            item.setDepositSeq(depositSeq);
            Lsdpds lsdpds = new Lsdpds(depositInsertRequestData.getDepositNo(), item);
            jpaLsdpdsRepository.save(lsdpds);
        }
    }

    private List<Ititmc> saveItitmc(DepositInsertRequestData depositInsertRequestData) {
        List<Ititmc> ititmcList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            Ititmc ititmc = new Ititmc(depositInsertRequestData, item);
            ititmc.setQty(lsdpsp.getPurchaseTakeQty());
            jpaItitmcRepository.save(ititmc);
            ititmcList.add(ititmc);
        }
        return ititmcList;
    }

    private List<Lsdpsp> saveLsdpsp(DepositInsertRequestData depositInsertRequestData) {
        List<DepositInsertRequestData.Item> itemList = depositInsertRequestData.getItems();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : itemList){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            if(lsdpsp.getPurchasePlanQty() < item.getDepositQty()){
                log.debug("puchase_take_qty is bigger than purchase_plan_qty.");
                throw new NumberFormatException();
            }
            lsdpsp.setPurchaseTakeQty(item.getDepositQty());
            jpaLsdpspRepository.save(lsdpsp);
            lsdpspList.add(lsdpsp);
        }
        return lsdpspList;
    }

    private List<Ititmt> saveItitmt(DepositInsertRequestData depositInsertRequestData) {
        List<DepositInsertRequestData.Item> itemList = depositInsertRequestData.getItems();
        List<Ititmt> ititmtList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : itemList){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            ItitmtId ititmtId = new ItitmtId(depositInsertRequestData, item);
            Ititmt ititmt = jpaItitmtRepository.findById(ititmtId).orElseGet(() -> null);
            assert ititmt == null : "ititmt is null.";
            if(ititmt == null){
                log.debug("ititmt is null.");
                throw new NumberFormatException();
            }
            long tempQty = ititmt.getTempQty() - lsdpsp.getPurchaseTakeQty();
            assert tempQty < 0 : "ititmt is null.";
            if(tempQty < 0){
                log.debug("ititmt.temp_qty is smaller than lsdpsp.take_qty.");
                throw new NumberFormatException();
            }
            ititmt.setTempQty(tempQty);
            jpaItitmtRepository.save(ititmt);
            ititmtList.add(ititmt);
        }
        return ititmtList;
    }

    public DepositSelectDetailResponseData getDetail(String depositNo){
        List<Lsdpsd> lsdpsdList = jpaLsdpsdRepository.findByDepositNo(depositNo);
        List<DepositSelectDetailResponseData.Item> itemList = new ArrayList<>();
        for(Lsdpsd lsdpsd : lsdpsdList){
            DepositSelectDetailResponseData.Item item = new DepositSelectDetailResponseData.Item(lsdpsd);
        }
        Lsdpsm lsdpsm = lsdpsdList.get(0).getLsdpsm();
        DepositSelectDetailResponseData depositSelectDetailResponseData = new DepositSelectDetailResponseData(lsdpsm);

        return depositSelectDetailResponseData;
    }

    /**
     * Table 초기화 함수
     */
    public void init(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById(StringFactory.getStrDepositNo());
        SequenceData seq = op.get();
        seq.setSequenceCurValue(StringFactory.getStrZero());
        jpaSequenceDataRepository.save(seq);
        jpaItitmcRepository.deleteAll();
        jpaItitmtRepository.deleteAll();
        jpaLsdpspRepository.deleteAll();
        jpaLsdpdsRepository.deleteAll();
        jpaLsdpssRepository.deleteAll();
        jpaLsdpsmRepository.deleteAll();
        jpaLsdpsdRepository.deleteAll();

    }
}
