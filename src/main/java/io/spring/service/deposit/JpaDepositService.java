package io.spring.service.deposit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpdsRepository;
import io.spring.jparepos.deposit.JpaLsdpsdRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.deposit.JpaLsdpssRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaItitmtRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.deposit.entity.Lsdpss;
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
import io.spring.service.move.JpaMoveService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.ship.JpaShipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.flywaydb.core.internal.util.StringUtils;

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
    private final JpaLspchdRepository jpaLspchdRepository;
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;


    private final JpaPurchaseService jpaPurchaseService;
	private final JpaMoveService jpaMoveService;
	private final JpaShipService jpaShipService;

    private final JpaOrderService jpaOrderService;
    private final EntityManager em;

//    @Transactional
//    public String sequenceInsertDeposit(DepositInsertRequestData depositInsertRequestData){
//        Lsdpsm lsdpsm = this.saveLsdpsm(depositInsertRequestData);// lsdpsm (입고 마스터)
//        List<Lsdpsd> lsdpsdList = this.saveLsdpsd(depositInsertRequestData);// lsdpsd (입고 디테일)
//        this.insertLsdpss(depositInsertRequestData);// lsdpss (입고 마스터 이력)
//        this.ssaveLsdpds(depositInsertRequestData);// lsdpds (입고 디테일 이력)
//        List<Lsdpsp> lsdpspList = this.saveLsdpsp(depositInsertRequestData);// lsdpsp (입고 예정)
//        List<Ititmc> ititmcList = this.saveItitmc(depositInsertRequestData);// ititmc (상품 재고)
//        List<Ititmt> ititmtList = this.saveItitmt(depositInsertRequestData);// ititmt (입고예정재고)
//        return depositInsertRequestData.getDepositNo();
//    }

    /**
	 * 입고처리 화면에서 발주조회 후 입고 데이터 저장
	 * 
	 * @throws Exception
	 */
	@Transactional
	public boolean sequenceCreateDeposit(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			List<String> messageList) throws Exception {

		// todo:주문발주의 경우 처리후 이동지시나 출고지시에 대한 내역 생성

        // 0. lsdpsp, ititmc, ititmt의 수량 관련값 변경
        List<Lsdpsp> lsdpspList = this.updateDepositQty(depositListWithPurchaseInfoData, messageList);
        if(lsdpspList.size() == 0){
            log.debug("입고 데이터를 저장할 수 없습니다.");
            return false;
        }
        // 1. lsdpsm 저장
        Lsdpsm lsdpsm = this.insertLsdpsm(depositListWithPurchaseInfoData);
        // 2. lsdpss 저장 (입고 마스터 이력)
        this.saveLsdpss(lsdpsm, depositListWithPurchaseInfoData);
        // 3. lsdpsd 저장 (입고 디테일)
        List<Lsdpsd> lsdpsdList = this.insertLsdpsd(depositListWithPurchaseInfoData, lsdpsm, lsdpspList);
        // 4. lsdpds 저장 (입고 디테일 이력)
        this.saveLsdpds(lsdpsdList, depositListWithPurchaseInfoData);
        // 5. lsdpsp의 입고예정과 실제 입고량을 비교해 부분입고인지 완전입고인지 여부로 lspchm,b,s의 purchaseStatus 변경
        jpaPurchaseService.changePurchaseStatus(depositListWithPurchaseInfoData.getPurchaseNo(), lsdpspList);
        // 8. tbOrderdetail 주문상태 변경 (lspchm.dealtypeCd = 01(주문발주) 일 때)

		// 주문입고건에 대해 상태확인후 이동지시 또는 출고지시 처리

		// jpaMoveService.saveOrderMoveByDeposit(lsdpsdList);
		List<HashMap<String, Object>> retList = this.saveMoveOrShip(lsdpsdList);

        this.changeStatusCdOfTbOrderDetail(lsdpspList);

        messageList.add(lsdpsm.getDepositNo());
        return true;
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
                String orderId = lsdpsp.getOrderId();
                String orderSeq = lsdpsp.getOrderSeq();
                Lspchm lspchm = lspchd.getLspchm();
                TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,orderSeq);
                String statusCd;

				Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsp.getAssortId());

				String assortId2;

				if (tbOrderDetail.getAssortGb().equals("002")) { // add_goods
					TbOrderDetail tbOrderDetail2 = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,
							tbOrderDetail.getParentOrderSeq());

					assortId2 = tbOrderDetail2.getAssortId();

				} else {
					assortId2 = lsdpsp.getAssortId();
				}

				Itasrt itasrt2 = jpaItasrtRepository.findByAssortId(assortId2);

				if (itasrt2.getAssortGb().equals(StringFactory.getGbOne())) { // 직구
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

//    private List<Lsdpsd> saveLsdpsd(DepositInsertRequestData depositInsertRequestData){
//        List<Lsdpsd> lsdpsdList = new ArrayList<>();
//        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
//            if(item.getDepositSeq() == null || item.getDepositSeq().equals("")){
//                String depositSeq = jpaLsdpsdRepository.findMaxDepositSeqByDepositNo(depositInsertRequestData.getDepositNo());
//                if(depositSeq == null){
//                    depositSeq = StringUtils.leftPad("1", 4, '0');
//                }
//                else{
//                    depositSeq = Utilities.plusOne(depositSeq, 4);
//                }
//                item.setDepositSeq(depositSeq);
//            }
//            Lsdpsd lsdpsd = new Lsdpsd(depositInsertRequestData, item);
//            jpaLsdpsdRepository.save(lsdpsd);
//            lsdpsdList.add(lsdpsd);
//        }
//        return lsdpsdList;
//    }

    private List<Lsdpsd> insertLsdpsd(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, Lsdpsm lsdpsm, List<Lsdpsp> lsdpspList){
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        int index = 1;
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
            lsdpspList = lsdpspList.stream().filter(x->x.getPurchaseNo().equals(deposit.getPurchaseNo()) && x.getPurchaseSeq().equals(deposit.getPurchaseSeq()))
                    .collect(Collectors.toList());
            if(deposit.getAvailableQty() < deposit.getDepositQty()){
                log.debug("input deposit qty is bigger than deposit available qty.");
                continue;
            }

            String depositSeq = StringUtils.leftPad(Integer.toString(index), 4, '0');
            Lsdpsd lsdpsd = new Lsdpsd(depositListWithPurchaseInfoData, lsdpsm, depositSeq, deposit, lsdpspList.get(0));

            Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(), lsdpsd.getInputSeq());
			// lspchd.setDepositNo(lsdpsd.getDepositNo());
			// lspchd.setDepositSeq(lsdpsd.getDepositSeq());
            jpaLspchdRepository.save(lspchd);
            
			lsdpsd.setOrderId(lspchd.getOrderId() == null ? null : lspchd.getOrderId());
			lsdpsd.setOrderSeq(lspchd.getOrderSeq() == null ? null : lspchd.getOrderSeq());

			lsdpsdList.add(lsdpsd);

			jpaLsdpsdRepository.save(lsdpsd);

            index++;
        }
        return lsdpsdList;
    }

    private void insertLsdpss(DepositInsertRequestData depositInsertRequestData){
        Lsdpss lsdpss = new Lsdpss(depositInsertRequestData);
        jpaLsdpssRepository.save(lsdpss);
    }

    private Lsdpss saveLsdpss(Lsdpsm lsdpsm, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData){
        Lsdpss lsdpss = jpaLsdpssRepository.findByDepositNoAndEffEndDt(lsdpsm.getDepositNo(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        if(lsdpss == null){
            lsdpss = new Lsdpss(lsdpsm);
        }
        else{
            Lsdpss newLsdpss = new Lsdpss(lsdpsm);
            jpaLsdpssRepository.save(newLsdpss);
        }
        jpaLsdpssRepository.save(lsdpss);
        return lsdpss;
    }

//    private void ssaveLsdpds(DepositInsertRequestData depositInsertRequestData) {
//        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
//            String depositSeq = jpaLsdpdsRepository.findMaxDepositSeqByDepositNo(depositInsertRequestData.getDepositNo());
//            if(depositSeq == null){
//                depositSeq = StringUtils.leftPad("1",4,'0');
//            }
//            else{
//                depositSeq = Utilities.plusOne(depositSeq, 4);
//            }
//            item.setDepositSeq(depositSeq);
//            Lsdpds lsdpds = new Lsdpds(depositInsertRequestData.getDepositNo(), item);
//            jpaLsdpdsRepository.save(lsdpds);
//        }
//    }

    private void saveLsdpds(List<Lsdpsd> lsdpsdList, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) {
        int ind = lsdpsdList.size();
        List<DepositListWithPurchaseInfoData.Deposit> depositList = depositListWithPurchaseInfoData.getDeposits();
        List<Lsdpds> lsdpdsList = new ArrayList<>();
        for (int i = 0; i < ind ; i++) {
            Lsdpsd lsdpsd = lsdpsdList.get(i);
            DepositListWithPurchaseInfoData.Deposit deposit = depositList.get(i);
            if(deposit.getAvailableQty() < deposit.getDepositQty()){
                continue;
            }
            Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(), lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
            if(lsdpds == null){
                lsdpds = new Lsdpds(lsdpsd);
            }
            else {
                Lsdpds newLsdpds = new Lsdpds(lsdpsd);
                jpaLsdpdsRepository.save(newLsdpds);
            }
            jpaLsdpdsRepository.save(lsdpds);
        }
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

//    private List<Ititmc> saveItitmc(DepositInsertRequestData depositInsertRequestData) {
//        List<Ititmc> ititmcList = new ArrayList<>();
//        for(DepositInsertRequestData.Item item : depositInsertRequestData.getItems()){
//            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
//            Lspchd lsdpsd = lsdpsp.getLspchd();
//            Ititmc ititmc = new Ititmc(depositInsertRequestData, item);
//            long takeQty = lsdpsp.getPurchaseTakeQty() == null? 0l : lsdpsp.getPurchaseTakeQty();
//            long qty = ititmc.getQty() == null? 0l : ititmc.getQty();
//            ititmc.setQty(takeQty + qty);
//            ititmc.setStockAmt(lsdpsd.getPurchaseUnitAmt());
//            jpaItitmcRepository.save(ititmc);
//            ititmcList.add(ititmc);
//        }
//        return ititmcList;
//    }

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
        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d " +
                "left join fetch d.lspchd lspchd " +
//                "left join fetch lspchd.lsdpsp lsdpsp " +
//                "left join fetch d.lsdpsm m " +
//                "left join fetch d.lsdpds s " +
                "where d.depositNo=?1", Lsdpsd.class);
        query.setParameter(1, depositNo);
        List<Lsdpsd> lsdpsdList = query.getResultList();
        if(lsdpsdList.size() == 0){
            log.debug("lsdpsdList is empty.");
            return null;
        }
        List<DepositSelectDetailResponseData.Item> itemList = new ArrayList<>();
        for(Lsdpsd lsdpsd : lsdpsdList){
            DepositSelectDetailResponseData.Item item = new DepositSelectDetailResponseData.Item(lsdpsd);
            Itasrt itasrt = lsdpsd.getItasrt();
            item.setItemNm(itasrt.getAssortNm());
            Utilities.setOptionNames(item,itasrt.getItvariList());
            item.setPurchaseNo(lsdpsd.getLspchd().getPurchaseNo());
            item.setPurchaseSeq(lsdpsd.getLspchd().getPurchaseSeq());
            item.setDepositQty(lsdpsd.getDepositQty());
//            Date doomDay = Utilities.getStringToDate(StringFactory.getDoomDay());
//            Lsdpds lsdpds1 = lsdpsd.getLsdpds().stream().filter(x -> x.getEffEndDt().equals(doomDay)).collect(Collectors.toList()).get(0);
//            item.setDepositStatus(lsdpsd.getLsdpsm().getDepositStatus());
            itemList.add(item);
        }
        Lsdpsm lsdpsm = lsdpsdList.get(0).getLsdpsm();
        DepositSelectDetailResponseData depositSelectDetailResponseData = new DepositSelectDetailResponseData(lsdpsm);
        depositSelectDetailResponseData.setItems(itemList);

        return depositSelectDetailResponseData;
    }

    /**
     * 입고 리스트를 가져오는 함수 (입고 - 입고리스트)
     * assortId가 null이거나 ""면 검색 조건에 미포함
     * assortNm은 like 검색
     */
	public DepositSelectListResponseData getList(String vendorId, String assortId, String assortNm, LocalDate startDt,
			LocalDate endDt, String storageId) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<DepositSelectListResponseData.Deposit> depositList = new ArrayList<>();
        TypedQuery<Lsdpsd> query = em.createQuery("select ld from Lsdpsd ld " +
                        "left join fetch ld.lsdpsm lm " +
//                        "left join fetch ld.lsdpsp lp " +
//                        "left join fetch ld.lsdpds ls " +
                        "left join fetch ld.itasrt it " +
                        "left join fetch lm.cmvdmr cm " +
                        "left join fetch ld.ititmm im " +
//                        "left join fetch im.itvari1 iv1 " +
//                        "left join fetch im.itvari2 iv2 " +
                        "where lm.depositDt between ?1 and ?2 " +
                        "and (?3 is null or trim(?3)='' or it.assortId=?3) " +
                        "and (?4 is null or trim(?4)='' or it.assortNm like concat('%', ?4, '%')) " +
                        "and (?5 is null or trim(?5)='' or lm.vendorId=?5) " +
				"and (?6 is null or trim(?6)='' or lm.storeCd=?6) " +
                        "order by ld.depositNo asc, ld.depositSeq asc",
                Lsdpsd.class);
        query.setParameter(1, start).setParameter(2, end).setParameter(3, assortId)
				.setParameter(4, assortNm).setParameter(5, vendorId).setParameter(6, storageId);
//        query.setParameter(4, param.get("assortId"));
        List<Lsdpsd> resultList = query.getResultList();
        for(Lsdpsd lsdpsd : resultList){
            DepositSelectListResponseData.Deposit deposit = new DepositSelectListResponseData.Deposit(lsdpsd);
			deposit.setVendorId(lsdpsd.getLsdpsm().getVendorId());
            deposit.setVdNm(lsdpsd.getLsdpsm().getCmvdmr() == null? "":lsdpsd.getLsdpsm().getCmvdmr().getVdNm());
            Itasrt itasrt = lsdpsd.getItasrt();
            deposit.setAssortNm(itasrt.getAssortNm());
            Utilities.setOptionNames(deposit, itasrt.getItvariList());
//            List<Lsdpsp> lsdpspList = lsdpsd.getLspchd().getLsdpsp();
//            lsdpspList.stream().filter(x->x.getPlanStatus().equals(StringFactory.getGbOne())).map(x->x.getq).reduce((a,b)->a+b).get();
            deposit.setDepositQty(lsdpsd.getDepositQty());
            //
            depositList.add(deposit);
        }
		DepositSelectListResponseData depositSelectListResponseData = new DepositSelectListResponseData(startDt, endDt,
				assortId, assortNm, vendorId);
        depositSelectListResponseData.setDepositList(depositList);
        return depositSelectListResponseData;
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
	 * 
	 * @throws Exception
	 */
	private List<Lsdpsp> updateDepositQty(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			List<String> messageList) throws Exception {
        String storageId = depositListWithPurchaseInfoData.getStorageId();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        List<DepositListWithPurchaseInfoData.Deposit> depositList = new ArrayList<>();
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
            Lsdpsp lsdpsp = this.getLsdpspWithLspchm(deposit.getDepositPlanId());//jpaLsdpspRepository.findByDepositPlanId(deposit.getDepositPlanId());
            Long purchasePlanQty = lsdpsp.getPurchasePlanQty() == null? 0l : lsdpsp.getPurchasePlanQty();
            Long purchaseTakeQty = lsdpsp.getPurchaseTakeQty() == null? 0l : lsdpsp.getPurchaseTakeQty();;
            Long availableQty = purchasePlanQty - purchaseTakeQty;
            String dealtypeCd = lsdpsp.getDealtypeCd();
//            boolean isOrderPurchase = dealtypeCd.equals(StringFactory.getGbOne()); // 주문발주
//            boolean isPartDeposit = availableQty >= deposit.getDepositQty(); // 부분입고
            boolean isCompleteDeposit = availableQty == deposit.getDepositQty(); // 완전입고
            boolean notGoodsPurchaseAndAvailableQty = availableQty >= deposit.getDepositQty() && !StringFactory.getGbOne().equals(dealtypeCd); // 입고가능수량 >= 입력값 && 주문발주 아님
            boolean orderPurchaseAndCompleteDeposit = isCompleteDeposit && StringFactory.getGbOne().equals(dealtypeCd);
            if(deposit.getDepositQty() == 0){
                log.debug(StringFactory.getStrDepositQtyZero());
                messageList.add(StringFactory.getStrDepositQtyZero());
                continue;
            }
            if(notGoodsPurchaseAndAvailableQty || orderPurchaseAndCompleteDeposit){ // '주문발주가 아니고 부분입고or완전입고' or '주문발주이고 완전입고'
                lsdpsp.setPurchaseTakeQty(lsdpsp.getPurchaseTakeQty() + deposit.getDepositQty());
                jpaLsdpspRepository.save(lsdpsp);
            }
            else if(availableQty != deposit.getDepositQty() && dealtypeCd.equals(StringFactory.getGbOne())){ // 주문발주인데 부분입고
                log.debug(StringFactory.getStrNotCompleteDeposit());
                messageList.add(StringFactory.getStrNotCompleteDeposit());
                continue;
            }

            else{
                log.debug(StringFactory.getStrInputQtyBig());
                messageList.add(StringFactory.getStrInputQtyBig());
                continue;
            }

            Lspchm lspchm = lsdpsp.getLspchd().getLspchm();
            // lspchd.purchaseGb=02(상품발주), lspchd.dealtypeCd = 03 (입고예정 주문발주) 이면 해당 lsdpsp와 주문을 연결시켜줌 (tbOrderDetail.statusCd = C03인 주문을 대상으로)
            if(lsdpsp.getPurchaseGb().equals(StringFactory.getGbTwo()) && lsdpsp.getDealtypeCd().equals(StringFactory.getGbThree())){
                List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository
                        .findByAssortIdAndItemIdAndQtyAndStatusCd(lsdpsp.getAssortId(), lsdpsp.getItemId(), lsdpsp.getPurchasePlanQty(), StringFactory.getStrC03());
                TbOrderDetail to = tbOrderDetailList.get(0);
                lsdpsp.setOrderId(to.getOrderId());
                lsdpsp.setOrderSeq(to.getOrderSeq());
                lsdpsp.setDealtypeCd(StringFactory.getGbOne()); // 03(입고예정주문발주) -> 01(일반발주) 로 변경
            }
            LocalDateTime purchaseDt = lspchm.getPurchaseDt();
            lsdpsp = this.changeLsdpspStatus(lsdpsp, isCompleteDeposit);
            lsdpspList.add(lsdpsp);
            depositList.add(deposit);

            this.saveItitmt(purchaseDt, storageId, deposit, dealtypeCd);

			// System.out.println(depositListWithPurchaseInfoData.getDepositDt());

			LocalDateTime localDateTime = LocalDateTime.parse(depositListWithPurchaseInfoData.getDepositDt(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


			this.saveItitmc(depositListWithPurchaseInfoData, localDateTime, storageId, deposit);
        }
        depositListWithPurchaseInfoData.setDeposits(depositList);
        return lsdpspList;
    }

    private Lsdpsp getLsdpspWithLspchm(String depositPlanId){
        TypedQuery<Lsdpsp> query = em.createQuery("select p from Lsdpsp p " +
                "join fetch p.lspchd d " +
                "join fetch d.lspchm m " +
                "where p.depositPlanId=?1",Lsdpsp.class);
        query.setParameter(1,depositPlanId);
        return query.getSingleResult();
    }


    /**
     * 부분입고인 경우 lsdpsp의 내역이 입고만큼만 처리되고 나머지 수량은 신규로 생성시켜주는 함수
     */
    private Lsdpsp changeLsdpspStatus(Lsdpsp lsdpsp, boolean isCompleteDeposit) {
        if(isCompleteDeposit){ // 완전 입고인 경우
            lsdpsp.setPlanStatus(StringFactory.getGbFour()); // 04 하드코딩
        }
        else{
            // 부분입고인 경우 : lsdpsp는 입고량 만큼 처리, 나머지 수량은 신규생성
            long remainQty = lsdpsp.getPurchasePlanQty() - lsdpsp.getPurchaseTakeQty();
            lsdpsp.setPurchasePlanQty(lsdpsp.getPurchaseTakeQty());
            Lsdpsp newLsdpsp = new Lsdpsp(this.getDepositPlanId(), lsdpsp);
            String newDepositPlanId = StringUtils.leftPad(jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsp()),9,'0');
            newLsdpsp.setDepositPlanId(newDepositPlanId);
            newLsdpsp.setPurchasePlanQty(remainQty);
            newLsdpsp.setPurchaseTakeQty(0l);
            lsdpsp.setPlanStatus(StringFactory.getGbFour());
            jpaLsdpspRepository.save(newLsdpsp);
            return newLsdpsp;
        }
        jpaLsdpspRepository.save(lsdpsp);
        return lsdpsp;
    }

    private Ititmc saveItitmc(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, LocalDateTime depositDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {
        Ititmc ititmc = new Ititmc(storageId, depositDt, deposit);

		ititmc.setVendorId(depositListWithPurchaseInfoData.getVendorId());

		ititmc.setShipIndicateQty(0L);
//		ititmc.setShipIndicateQty(0);
        Itasrt itasrt = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
        ititmc.setOwnerId(itasrt.getOwnerId());
        ititmc.setQty(deposit.getDepositQty());
        jpaItitmcRepository.save(ititmc);
        return ititmc;
    }

	private Ititmt saveItitmt(LocalDateTime purchaseDt, String storageId,
			DepositListWithPurchaseInfoData.Deposit deposit, String dealTypeCd) throws Exception {
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt
                        (deposit.getAssortId(), deposit.getItemId(), storageId, StringFactory.getStrEleven(), purchaseDt); // dealtypeCd = '01'인 애들(주문)
        if(ititmt == null){
//            ititmt = new Ititmt(purchaseDt, storageId, deposit);
            log.debug("There is no proper ititmt. Check data.");

			// throw new Exception();
			// throw new RuntimeException(e);
			throw new RuntimeException("There is no proper ititmt. Check data.");

			// return null;
        }
        else {
            ititmt.setTempQty(ititmt.getTempQty() - deposit.getDepositQty());
        }

		if (!dealTypeCd.equals(StringFactory.getGbTwo())) { // 주문발주일 때
			if (ititmt.getTempIndicateQty() - deposit.getDepositQty() > 0) {
				ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() - deposit.getDepositQty());
			} else {
				ititmt.setTempIndicateQty(0L);
			}

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

	private List<HashMap<String, Object>> saveMoveOrShip(List<Lsdpsd> list) {

		List<HashMap<String, Object>> ret = new ArrayList<HashMap<String, Object>>();

		for (Lsdpsd lsdpsd : list) {

//			Lspchd jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(), lsdpsd.getInputSeq());

			Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(),
					lsdpsd.getInputSeq());

			// 주문발주나 입고예정발주시 처리
			if (lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbOne())
					|| lspchd.getLspchm().getDealtypeCd().equals("03")) {


				System.out.println(lsdpsd);

				if (lsdpsd.getOrderId() != null && lsdpsd.getOrderSeq() != null) {


					String orderId = lspchd.getOrderId();
					String orderSeq = lspchd.getOrderSeq();
					Lspchm lspchm = lspchd.getLspchm();
					TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,
							orderSeq);
					String statusCd;
					
					String assortId2 = "";

					if (tbOrderDetail.getAssortGb().equals("002")) {
						TbOrderDetail tbOrderDetail2 = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,
								tbOrderDetail.getParentOrderSeq());
						assortId2 = tbOrderDetail2.getAssortId();
					} else {
						assortId2 = lsdpsd.getAssortId();
					}

					Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsd.getAssortId());
					
					Itasrt itasrt2 = jpaItasrtRepository.findByAssortId(assortId2); // 추가상품일경우 원건의 상품구분사용

					if (itasrt2.getAssortGb().equals(StringFactory.getGbOne())) { // 직구

						System.out.println("-----------------------수입------------------------------");

						// 입고창고와 주문의 창고가 같은경우 출고지시
						List<String> r = jpaShipService.saveShipIndicateByDeposit(lsdpsd);
						if (r.size() > 0) {
							HashMap<String, Object> p = new HashMap<String, Object>();

							p.put("type", "ship");
							p.put("shipId", r.get(0));
							ret.add(p);

						}

					} else { // if(tbOrderDetail.getAssortGb().equals(StringFactory.getGbTwo())){ // 수입

						System.out.println("-----------------------직구------------------------------");

						if (tbOrderDetail.getStorageId().equals(lspchm.getStoreCd())) {
							// 입고창고와 주문의 창고가 같은경우 출고지시
							List<String> r = jpaShipService.saveShipIndicateByDeposit(lsdpsd);
							if (r.size() > 0) {
								HashMap<String, Object> p = new HashMap<String, Object>();

								p.put("type", "ship");
								p.put("shipId", r.get(0));
								ret.add(p);

							}
						} else {
							// 입고창고와 주문의 창고가 다른경우 이동지시

							List<String> r = jpaMoveService.saveOrderMoveByDeposit(lsdpsd);
							if (r.size() > 0) {
								HashMap<String, Object> p = new HashMap<String, Object>();

								p.put("type", "move");
								p.put("shipId", r.get(0));
								ret.add(p);

							}

						}
					}

				}



			}

		}

		System.out.println(ret);

		return ret;

	}


}
