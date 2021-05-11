package io.spring.service.deposit;

import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.deposit.*;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.deposit.entity.Lsdpss;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.goods.entity.Ititmc;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaDepositService {
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsdpssRepository jpaLsdpssRepository;
    private final JpaLsdpdsRepository jpaLsdpdsRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaItitmcRepository jpaItitmcRepository;

    public String sequenceInsertDeposit(DepositInsertRequestData depositInsertRequestData){
        Lsdpsm lsdpsm = this.saveLsdpsm(depositInsertRequestData);// lsdpsm (입고 마스터)
        List<Lsdpsd> lsdpsdList = this.saveLsdpsd(depositInsertRequestData);// lsdpsd (입고 디테일)
        this.saveLsdpss(depositInsertRequestData);// lsdpss (입고 마스터 이력)
        this.saveLsdpds(depositInsertRequestData);// lsdpds (입고 디테일 이력)
        List<Ititmc> ititmcList = this.saveItitmc(depositInsertRequestData);// ititmc (상품 재고)
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
            Lsdpds lsdpds = new Lsdpds(depositInsertRequestData.getDepositNo(), item);
            jpaLsdpdsRepository.save(lsdpds);
        }
    }

    private List<Ititmc> saveItitmc(DepositInsertRequestData depositInsertRequestData) {
        List<Ititmc> ititmcList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
            Ititmc ititmc = new Ititmc(depositInsertRequestData, item);
            jpaItitmcRepository.save(ititmc);
            ititmcList.add(ititmc);
        }
        return ititmcList;
    }
}
