package io.spring.service.deposit;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.*;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.purchase.JpaLspchmRepository;
import io.spring.model.common.entity.SequenceData;
import io.spring.model.deposit.entity.*;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.deposit.response.DepositSelectDetailResponseData;
import io.spring.model.deposit.response.DepositSelectListResponseData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaDepositService {
    private final JpaLsdpsdRepository jpaLsdpsdRepository;
    private final JpaLsdpsmRepository jpaLsdpsmRepository;
    private final JpaLsdpssRepository jpaLsdpssRepository;
    private final JpaLsdpdsRepository jpaLsdpdsRepository;
    private final JpaLsdpspRepository jpaLsdpspRepository;
    private final JpaItasrtRepository jpaItasrtRepository;
    private final JpaItitmcRepository jpaItitmcRepository;
    private final JpaItitmtRepository jpaItitmtRepository;
    private final JpaLspchmRepository jpaLspchmRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;

    private final JpaPurchaseService jpaPurchaseService;
    private final JpaOrderService jpaOrderService;
    private final EntityManager em;

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

    /**
     * 입고처리 화면에서 발주조회 후 입고 데이터 저장
     */
    @Transactional
	public String sequenceCreateDeposit(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        // 0. lsdpsp, ititmc, ititmt의 수량 관련값 변경
        List<Lsdpsp> lsdpspList = this.updateDepositQty(depositListWithPurchaseInfoData);
        if(lsdpspList.size() == 0){
            return null;
        }
        // 1. lsdpsm 저장
        Lsdpsm lsdpsm = this.insertLsdpsm(depositListWithPurchaseInfoData);
        // 2. lsdpss 저장 (입고 마스터 이력)
        this.insertLsdpss(lsdpsm, depositListWithPurchaseInfoData);
        // 3. lsdpsd 저장 (입고 디테일)
        List<Lsdpsd> lsdpsdList = this.insertLsdpsd(depositListWithPurchaseInfoData, lsdpsm);
        // 4. lsdpds 저장 (입고 디테일 이력)
        this.insertLsdpds(lsdpsdList, depositListWithPurchaseInfoData);
        // 5. lsdpsp의 입고예정과 실제 입고량을 비교해 부분입고인지 완전입고인지 여부로 lspchm,b,s의 purchaseStatus 변경
        jpaPurchaseService.changePurchaseStatus(lsdpspList);
        // 8. tbOrderdetail 주문상태 변경 (lspchm.dealtypeCd = 01(주문발주) 일 때)
        this.changeStatusCdOfTbOrderDetail(lsdpspList);

        return lsdpsm.getDepositNo();
    }

    /**
     * 입고처리 화면에서 발주조회 후 입고 데이터 수정
     */
    @Transactional
    public void sequenceUpdateDeposit(DepositInsertRequestData depositInsertRequestData) {
    }

    private void changeStatusCdOfTbOrderDetail(List<Lsdpsp> lsdpspList) {
        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getDealtypeCd().equals(StringFactory.getGbOne())){ // dealtypeCd가 01(주문발주)인 애들만 해당
                Lspchd lspchd = lsdpsp.getLspchd();
                String orderId = lspchd.getOrderId();
                String orderSeq = lspchd.getOrderSeq();
                Lspchm lspchm = lspchd.getLspchm();
                TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,orderSeq);
                String statusCd;
                if(tbOrderDetail.getAssortGb().equals(StringFactory.getGbOne())){ // 직구
                    statusCd = StringFactory.getStrC04();
                }
                else{ //if(tbOrderDetail.getAssortGb().equals(StringFactory.getGbTwo())){ // 수입
                    if (tbOrderDetail.getStorageId().equals(lspchm.getStoreCd())){
                        statusCd = StringFactory.getStrC04(); // 국내입고완료
                    }
                    else {
                        statusCd = StringFactory.getStrC01(); // 해외입고완료
                    }
                }
                jpaOrderService.updateOrderStatusCd(orderId, orderSeq, statusCd);
            }
        }
    }

    private Lsdpsm insertLsdpsm(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData){
        // depositNo 채번
        String no = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
        String depositNo = Utilities.getStringNo('D', no,9);
        Lsdpsm lsdpsm = new Lsdpsm(depositNo, depositListWithPurchaseInfoData);
        jpaLsdpsmRepository.save(lsdpsm);
        return lsdpsm;
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

    private List<Lsdpsd> insertLsdpsd(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, Lsdpsm lsdpsm){
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        int index = 1;
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
            if(deposit.getAvailableQty() < deposit.getDepositQty()){
                log.debug("input deposit qty is bigger than deposit available qty.");
                continue;
            }
            String depositSeq = StringUtils.leftPad(Integer.toString(index), 4, '0');
            Lsdpsd lsdpsd = new Lsdpsd(lsdpsm, depositSeq, deposit);
            lsdpsdList.add(lsdpsd);
            jpaLsdpsdRepository.save(lsdpsd);
            index++;
        }
        return lsdpsdList;
    }

    private void saveLsdpss(DepositInsertRequestData depositInsertRequestData){
        Lsdpss lsdpss = new Lsdpss(depositInsertRequestData);
        jpaLsdpssRepository.save(lsdpss);
    }

    private Lsdpss insertLsdpss(Lsdpsm lsdpsm, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData){
        Lsdpss lsdpss = new Lsdpss(lsdpsm);
        jpaLsdpssRepository.save(lsdpss);
        return lsdpss;
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

    private List<Lsdpds> insertLsdpds(List<Lsdpsd> lsdpsdList, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        int ind = lsdpsdList.size();
        List<DepositListWithPurchaseInfoData.Deposit> depositList = depositListWithPurchaseInfoData.getDeposits();
        List<Lsdpds> lsdpdsList = new ArrayList<>();
        for (int i = 0; i < ind ; i++) {
            if(depositList.get(i).getAvailableQty() < depositList.get(i).getDepositQty()){
                continue;
            }
            Lsdpds lsdpds = new Lsdpds(lsdpsdList.get(i), depositList.get(i));
            lsdpdsList.add(lsdpds);
            jpaLsdpdsRepository.save(lsdpds);
        }
        return lsdpdsList;
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

    private List<Lsdpsp> saveLsdpsp(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        List<DepositListWithPurchaseInfoData.Deposit> depositList = depositListWithPurchaseInfoData.getDeposits();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositList){

        }
        return lsdpspList;
    }

    private List<Ititmc> saveItitmc(DepositInsertRequestData depositInsertRequestData) {
        List<Ititmc> ititmcList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            Lspchd lsdpsd = lsdpsp.getLspchd();
            Ititmc ititmc = new Ititmc(depositInsertRequestData, item);
            long takeQty = lsdpsp.getPurchaseTakeQty() == null? 0l : lsdpsp.getPurchaseTakeQty();
            long qty = ititmc.getQty() == null? 0l : ititmc.getQty();
            ititmc.setQty(takeQty + qty);
            ititmc.setStockAmt(lsdpsd.getPurchaseUnitAmt());
            jpaItitmcRepository.save(ititmc);
            ititmcList.add(ititmc);
        }
        return ititmcList;
    }

    private List<Ititmt> saveItitmt(DepositInsertRequestData depositInsertRequestData) {
        List<DepositInsertRequestData.Item> itemList = depositInsertRequestData.getItems();
        List<Ititmt> ititmtList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : itemList){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            ItitmtId ititmtId = new ItitmtId(depositInsertRequestData, item);
            Ititmt ititmt = jpaItitmtRepository.findById(ititmtId).orElseGet(() -> null);
            if(ititmt == null){
                log.debug("ititmt is null.");
                throw new NumberFormatException();
            }
            long tempQty = ititmt.getTempQty() - lsdpsp.getPurchaseTakeQty();
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

    private List<Ititmt> saveItitmt(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        List<DepositListWithPurchaseInfoData.Deposit> depositList = depositListWithPurchaseInfoData.getDeposits();
        List<Ititmt> ititmtList = new ArrayList<>();
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){

        }
        return ititmtList;
    }

    /**
     * 입고번호를 통해 입고번호 상세 정보를 가져오는 함수
     * @return
     */
    public DepositSelectDetailResponseData getDetail(String depositNo){
        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d join fetch d.lsdpsp p join fetch d.lsdpsm m join fetch d.lsdpds s " +
                "where d.depositNo=?1 and s.effEndDt=?2", Lsdpsd.class);
        query.setParameter(1, depositNo);
        query.setParameter(2, Utilities.getStringToDate(StringFactory.getDoomDay()));
        List<Lsdpsd> lsdpsdList = query.getResultList();
        if(lsdpsdList.size() == 0){
            log.debug("lsdpsdList is empty.");
            return null;
        }
        List<DepositSelectDetailResponseData.Item> itemList = new ArrayList<>();
        for(Lsdpsd lsdpsd : lsdpsdList){
            DepositSelectDetailResponseData.Item item = new DepositSelectDetailResponseData.Item(lsdpsd);
            item.setPurchaseNo(lsdpsd.getLsdpsp().getPurchaseNo());
            item.setPurchaseSeq(lsdpsd.getLsdpsp().getPurchaseSeq());
            item.setDepositQty(lsdpsd.getLsdpsp().getPurchasePlanQty());
            Date doomDay = Utilities.getStringToDate(StringFactory.getDoomDay());
            Lsdpds lsdpds1 = lsdpsd.getLsdpds().stream().filter(x -> x.getEffEndDt().equals(doomDay)).collect(Collectors.toList()).get(0);
            item.setDepositStatus(lsdpds1.getDepositStatus());
            itemList.add(item);
        }
        Lsdpsm lsdpsm = lsdpsdList.get(0).getLsdpsm();
        DepositSelectDetailResponseData depositSelectDetailResponseData = new DepositSelectDetailResponseData(lsdpsm);
        depositSelectDetailResponseData.setItems(itemList);

        return depositSelectDetailResponseData;
    }

    /**
     * Table 초기화 함수
     */
    public void init(){
        Optional<SequenceData> op = jpaSequenceDataRepository.findById(StringFactory.getStrSeqLsdpsm());
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

    /**
     * 입고 리스트를 가져오는 함수 
     * @return
     */
    public List<DepositSelectListResponseData> getList(HashMap<String, Object> param) {
        List<DepositSelectListResponseData> depositSelectListResponseDataList = new ArrayList<>();
        TypedQuery<Lsdpsd> query = em.createQuery("select ld from Lsdpsd ld " +
                        "join fetch ld.lsdpsm lm " +
                        "join fetch ld.lsdpsp lp " +
                        "join fetch ld.lsdpds ls " +
                        "join fetch ld.itasrt it " +
                        "join fetch lm.cmvdmr cm " +
                        "left join fetch ld.ititmm im " +
                        "left join fetch im.itvari1 iv1 " +
                        "left join fetch im.itvari2 iv2 " +
                        "where lm.depositDt between ?1 and ?2 " +
                        "and lm.depositVendorId like CONCAT('%',?3,'%') " +
                        "and ld.assortId like concat('%', ?4, '%')",
                Lsdpsd.class);
        query.setParameter(1, param.get("startDt"));
        query.setParameter(2, param.get("endDt"));
        query.setParameter(3, param.get("depositVendorId"));
        query.setParameter(4, param.get("assortId"));
        List<Lsdpsd> resultList = query.getResultList();
        for(Lsdpsd lsdpsd : resultList){
            DepositSelectListResponseData depositSelectListResponseData = new DepositSelectListResponseData(lsdpsd);
            depositSelectListResponseData.setDepositVendorId(lsdpsd.getLsdpsm().getDepositVendorId());
            depositSelectListResponseData.setVdNm(lsdpsd.getLsdpsm().getCmvdmr().getVdNm());
            depositSelectListResponseData.setAssortNm(lsdpsd.getItasrt().getAssortNm());
            // 2 depth 주의...
            depositSelectListResponseData.setOptionNm2(lsdpsd.getItitmm().getItvari2().getOptionNm());
            depositSelectListResponseData.setDepositQty(lsdpsd.getLsdpsp().getPurchaseTakeQty());
            //
            depositSelectListResponseDataList.add(depositSelectListResponseData);
        }
        return depositSelectListResponseDataList;
    }

    /**
     * 발주번호를 받아 해당 발주번호에 해당하는 입고리스트를 가져오는 함수 
     */
    public DepositListWithPurchaseInfoData getDepositListByPurchaseNo(String purchaseNo) {
        List<Lsdpsp> lsdpspList = jpaLsdpspRepository.findByPurchaseNo(purchaseNo);
        lsdpspList = lsdpspList.stream().filter(x->x.getPlanStatus().equals(StringFactory.getGbOne())).collect(Collectors.toList());
        List<DepositListWithPurchaseInfoData.Deposit> depositList = new ArrayList<>();
        Lspchm lspchm = null;
        int i = 0;
        for(Lsdpsp lsdpsp : lsdpspList){
            if(i == 0){
                lspchm = lsdpsp.getLspchd().getLspchm();
            }
            DepositListWithPurchaseInfoData.Deposit deposit = makeDepositSelectListResponseData(lsdpsp);
            depositList.add(deposit);
            i++;
        }
        DepositListWithPurchaseInfoData depositListWithPurchaseInfoDataList = new DepositListWithPurchaseInfoData(lspchm, depositList);
        return depositListWithPurchaseInfoDataList;
    }

    /**
     * lsdpsp로 DepositSelectListResponseData 객체를 만드는 함수
     */
    private DepositListWithPurchaseInfoData.Deposit makeDepositSelectListResponseData(Lsdpsp lsdpsp) {
        Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsp.getAssortId());
        DepositListWithPurchaseInfoData.Deposit deposit = new DepositListWithPurchaseInfoData.Deposit(itasrt, lsdpsp);
        return deposit;
    }

    /**
     * 입고 처리가능수량을 변경했을 때 수정하는 함수
     */
    private List<Lsdpsp> updateDepositQty(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        String storageId = depositListWithPurchaseInfoData.getStorageId();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByDepositPlanId(deposit.getDepositPlanId());
            Long purchasePlanQty = lsdpsp.getPurchasePlanQty() == null? 0l : lsdpsp.getPurchasePlanQty();
            Long purchaseTakeQty = lsdpsp.getPurchaseTakeQty() == null? 0l : lsdpsp.getPurchaseTakeQty();;
            Long availableQty = purchasePlanQty - purchaseTakeQty;
            String dealtypeCd = lsdpsp.getDealtypeCd();
//            boolean isOrderPurchase = dealtypeCd.equals(StringFactory.getGbOne()); // 주문발주
//            boolean isPartDeposit = availableQty >= deposit.getDepositQty(); // 부분입고
            boolean isCompleteDeposit = availableQty == deposit.getDepositQty(); // 완전입고
            boolean notGoodsPurchaseAndAvailableQty = availableQty >= deposit.getDepositQty() && !dealtypeCd.equals(StringFactory.getGbOne()); // 입고가능수량 >= 입력값 && 주문발주 아님
            boolean orderPurchaseAndCompleteDeposit = isCompleteDeposit && dealtypeCd.equals(StringFactory.getGbOne());
            if(notGoodsPurchaseAndAvailableQty || orderPurchaseAndCompleteDeposit){ // '주문발주가 아니고 부분입고or완전입고' or '주문발주이고 완전입고'
                lsdpsp.setPurchaseTakeQty(lsdpsp.getPurchaseTakeQty() + deposit.getDepositQty());
                jpaLsdpspRepository.save(lsdpsp);
                lsdpspList.add(lsdpsp);
            }
            else if(availableQty != deposit.getDepositQty() && dealtypeCd.equals(StringFactory.getGbOne())){ // 주문발주인데 부분입고
                log.debug("This purchase.dealtypeCd is 01, but this isn't complete deposit.");
                continue;
            }
            else{
                log.debug("input qty is bigger than available qty.");
                continue;
            }
            Lspchm lspchm = lsdpsp.getLspchd().getLspchm();
            Date purchaseDt = lspchm.getPurchaseDt();
            this.whenPartDeposit(lsdpsp, isCompleteDeposit);
            this.saveItitmt(purchaseDt, storageId, deposit, dealtypeCd);
            this.saveItitmc(purchaseDt, storageId, deposit);
        }
        return lsdpspList;
    }

    /**
     * 부분입고인 경우 lsdpsp의 내역이 입고만큼만 처리되고 나머지 수량은 신규로 생성시켜주는 함수
     */
    private void whenPartDeposit(Lsdpsp lsdpsp, boolean isCompleteDeposit) {
        if(isCompleteDeposit){ // 완전 입고인 경우
            return;
        }
        // 부분입고인 경우 : lsdpsp는 입고량 만큼 처리, 나머지 수량은 신규생성
        long remainQty = lsdpsp.getPurchasePlanQty() - lsdpsp.getPurchaseTakeQty();
        lsdpsp.setPurchasePlanQty(lsdpsp.getPurchaseTakeQty());
        lsdpsp.setPlanStatus(StringFactory.getGbFour());
        Lsdpsp newLsdpsp = new Lsdpsp(this.getDepositPlanId(), lsdpsp);
        newLsdpsp.setPurchasePlanQty(remainQty);
        newLsdpsp.setPurchaseTakeQty(0l);
        jpaLsdpspRepository.save(lsdpsp);
        jpaLsdpspRepository.save(newLsdpsp);
    }

    private Ititmc saveItitmc(Date purchaseDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {
        Ititmc ititmc = new Ititmc(storageId, purchaseDt, deposit);
//        ititmc.setShipIndicateQty(deposit.getDepositQty());
        ititmc.setQty(deposit.getDepositQty());
        jpaItitmcRepository.save(ititmc);
        return ititmc;
    }

    private Ititmt saveItitmt(Date purchaseDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit, String dealTypeCd){
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt
                        (deposit.getAssortId(), deposit.getItemId(), storageId, StringFactory.getStrEleven(), purchaseDt); // dealtypeCd = '01'인 애들(주문)
        if(ititmt == null){
//            ititmt = new Ititmt(purchaseDt, storageId, deposit);
            log.debug("There is no proper ititmt. Check data.");
            return null;
        }
        else {
            ititmt.setTempQty(ititmt.getTempQty() + deposit.getDepositQty());
        }
        if(dealTypeCd.equals(StringFactory.getGbOne())){ // 주문발주일 때
            ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() + deposit.getDepositQty());
        }
        jpaItitmtRepository.save(ititmt);
        return ititmt;
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
