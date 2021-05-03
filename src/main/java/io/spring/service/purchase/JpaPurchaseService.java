package io.spring.service.purchase;

import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.purchase.*;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.purchase.entity.*;
import io.spring.model.purchase.request.PurchaseInsertRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     *
     * @param purchaseInsertRequest
     * @return
     */
    public String savePurchaseSquence(PurchaseInsertRequest purchaseInsertRequest) {
        // lspchm (발주마스터)
        Lspchm lspchm = this.saveLspchm(purchaseInsertRequest);
        // lspchs (발주 상태 이력)
        Lspchs lspchs = this.saveLspchs(purchaseInsertRequest);
        // lspchd (발주 디테일)
        Lspchd lspchd = this.saveLspchd(purchaseInsertRequest);
        // lspchb (발주 디테일 이력)
        Lspchb lspchb = this.saveLspchb(purchaseInsertRequest);
        // lsdpsp (입고 예정)
        Lsdpsp lsdpsp = this.saveLsdpsp(purchaseInsertRequest);
        // ititmt (예정 재고)
        Ititmt ititmt = this.saveItitmt(purchaseInsertRequest);
        return lspchd.getAssortId();
    }

    private Lspchm saveLspchm(PurchaseInsertRequest purchaseInsertRequest) {
        return null;
    }

    private Lspchs saveLspchs(PurchaseInsertRequest purchaseInsertRequest) {
        return null;
    }

    private Lspchd saveLspchd(PurchaseInsertRequest purchaseInsertRequest) {
        return null;
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


}
