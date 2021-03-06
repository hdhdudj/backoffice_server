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

import io.spring.enums.TrdstOrderStatus;
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
import io.spring.jparepos.goods.JpaTmmapiRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.model.deposit.entity.Lsdpds;
import io.spring.model.deposit.entity.Lsdpsd;
import io.spring.model.deposit.entity.Lsdpsm;
import io.spring.model.deposit.entity.Lsdpsp;
import io.spring.model.deposit.entity.Lsdpss;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.request.DepositSelectDetailRequestData;
import io.spring.model.deposit.request.InsertDepositEtcRequestData;
import io.spring.model.deposit.response.DepositEtcItemListResponseData;
import io.spring.model.deposit.response.DepositEtcItemResponseData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.deposit.response.DepositSelectDetailResponseData;
import io.spring.model.deposit.response.DepositSelectListResponseData;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.Ititmt;
import io.spring.model.goods.entity.Tmmapi;
import io.spring.model.goods.idclass.ItitmtId;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.purchase.entity.Lspchd;
import io.spring.model.purchase.entity.Lspchm;
import io.spring.service.common.MyBatisCommonService;
import io.spring.service.move.JpaMoveService;
import io.spring.service.order.JpaOrderService;
import io.spring.service.purchase.JpaPurchaseService;
import io.spring.service.ship.JpaShipService;
import io.spring.service.stock.JpaStockService;
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

	private final JpaTmmapiRepository jpaTmmapiRepository;

    private final JpaPurchaseService jpaPurchaseService;
	private final JpaMoveService jpaMoveService;
	private final JpaShipService jpaShipService;

    private final JpaOrderService jpaOrderService;
    private final EntityManager em;

	private final JpaStockService jpaStockService;

	private final MyBatisCommonService myBatisCommonService;

//    @Transactional
//    public String sequenceInsertDeposit(DepositInsertRequestData depositInsertRequestData){
//        Lsdpsm lsdpsm = this.saveLsdpsm(depositInsertRequestData);// lsdpsm (?????? ?????????)
//        List<Lsdpsd> lsdpsdList = this.saveLsdpsd(depositInsertRequestData);// lsdpsd (?????? ?????????)
//        this.insertLsdpss(depositInsertRequestData);// lsdpss (?????? ????????? ??????)
//        this.ssaveLsdpds(depositInsertRequestData);// lsdpds (?????? ????????? ??????)
//        List<Lsdpsp> lsdpspList = this.saveLsdpsp(depositInsertRequestData);// lsdpsp (?????? ??????)
//        List<Ititmc> ititmcList = this.saveItitmc(depositInsertRequestData);// ititmc (?????? ??????)
//        List<Ititmt> ititmtList = this.saveItitmt(depositInsertRequestData);// ititmt (??????????????????)
//        return depositInsertRequestData.getDepositNo();
//    }

    /**
	 * ???????????? ???????????? ???????????? ??? ?????? ????????? ??????
	 * 
	 * @throws Exception
	 */
	@Transactional
	public boolean sequenceCreateDeposit(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			List<String> messageList, String userId) throws Exception {

		// todo:??????????????? ?????? ????????? ??????????????? ??????????????? ?????? ?????? ??????

        // 0. lsdpsp, ititmc, ititmt??? ?????? ????????? ??????
		List<Lsdpsp> lsdpspList = this.updateDepositQty(depositListWithPurchaseInfoData, messageList, userId);
        if(lsdpspList.size() == 0){
            log.debug("?????? ???????????? ????????? ??? ????????????.");
            return false;
        }
        // 1. lsdpsm ??????
		Lsdpsm lsdpsm = this.insertLsdpsm(depositListWithPurchaseInfoData, userId);
        // 2. lsdpss ?????? (?????? ????????? ??????)
		this.saveLsdpss(lsdpsm, depositListWithPurchaseInfoData, userId);
        // 3. lsdpsd ?????? (?????? ?????????)
		List<Lsdpsd> lsdpsdList = this.insertLsdpsd(depositListWithPurchaseInfoData, lsdpsm, lsdpspList, userId);
        // 4. lsdpds ?????? (?????? ????????? ??????)
		this.saveLsdpds(lsdpsdList, depositListWithPurchaseInfoData, userId);
        // 5. lsdpsp??? ??????????????? ?????? ???????????? ????????? ?????????????????? ?????????????????? ????????? lspchm,b,s??? purchaseStatus ??????
		jpaPurchaseService.changePurchaseStatus(depositListWithPurchaseInfoData.getPurchaseNo(), lsdpspList, userId);
        // 8. tbOrderdetail ???????????? ?????? (lspchm.dealtypeCd = 01(????????????) ??? ???)

		// ?????????????????? ?????? ??????????????? ???????????? ?????? ???????????? ??????

//		 jpaMoveService.saveOrderMoveByDeposit(lsdpsdList);
		List<HashMap<String, Object>> retList = this.saveMoveOrShip(lsdpsdList, userId);

		this.changeStatusCdOfTbOrderDetail(lsdpspList, userId);

        messageList.add(lsdpsm.getDepositNo());
        return true;
    }

    /**
     * ???????????? ???????????? ???????????? ??? ?????? ????????? ??????
     */
    @Transactional
    public void sequenceUpdateDeposit(DepositInsertRequestData depositInsertRequestData) {
    }

	private void changeStatusCdOfTbOrderDetail(List<Lsdpsp> lsdpspList, String userId) {
        for(Lsdpsp lsdpsp : lsdpspList){
            if(lsdpsp.getDealtypeCd().equals(StringFactory.getGbOne())){ // dealtypeCd??? 01(????????????)??? ????????? ??????
                Lspchd lspchd = lsdpsp.getLspchd();
                String orderId = lsdpsp.getOrderId();
                String orderSeq = lsdpsp.getOrderSeq();
                Lspchm lspchm = lspchd.getLspchm();
                TbOrderDetail tbOrderDetail = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,orderSeq);
                String statusCd;

				Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsp.getAssortId());

				String assortId2;

				System.out.println(tbOrderDetail.getAssortGb());

				if (tbOrderDetail.getAssortGb().equals("002")) { // add_goods
					TbOrderDetail tbOrderDetail2 = jpaTbOrderDetailRepository.findByOrderIdAndOrderSeq(orderId,
							tbOrderDetail.getParentOrderSeq());

					assortId2 = tbOrderDetail2.getAssortId();

				} else {
					assortId2 = lsdpsp.getAssortId();
				}

				Itasrt itasrt2 = jpaItasrtRepository.findByAssortId(assortId2);

				if (itasrt2.getAssortGb().equals(StringFactory.getGbOne())) { // ??????
                    statusCd = TrdstOrderStatus.C04.toString();
                }
                else{ //if(tbOrderDetail.getAssortGb().equals(StringFactory.getGbTwo())){ // ??????
                    if (tbOrderDetail.getStorageId().equals(lspchm.getStoreCd())){
                        statusCd = TrdstOrderStatus.C04.toString(); // ??????????????????
                    }
                    else {
                        statusCd = TrdstOrderStatus.C01.toString(); // ??????????????????
                    }
                }
				jpaOrderService.updateOrderStatusCd(orderId, orderSeq, statusCd, userId);
            }
        }
    }

	// userId
	private Lsdpsm insertLsdpsm(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, String userId) {

        // depositNo ??????
        String no = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
        String depositNo = Utilities.getStringNo('D', no,9);
        Lsdpsm lsdpsm = new Lsdpsm(depositNo, depositListWithPurchaseInfoData);

		lsdpsm.setRegId(userId);
		lsdpsm.setUpdId(userId);
        jpaLsdpsmRepository.save(lsdpsm);
        return lsdpsm;
    }

	private Lsdpsm insertEtcLsdpsm(InsertDepositEtcRequestData p, String userId) {
		// depositNo ??????
		String no = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
		String depositNo = Utilities.getStringNo('D', no, 9);
		Lsdpsm lsdpsm = new Lsdpsm(depositNo, p);
		lsdpsm.setRegId(userId);
		lsdpsm.setUpdId(userId);
		jpaLsdpsmRepository.save(lsdpsm);
		return lsdpsm;
	}

	private Lsdpsm saveLsdpsm(DepositInsertRequestData depositInsertRequestData, String userId) {
        Lsdpsm lsdpsm = new Lsdpsm(depositInsertRequestData);
		lsdpsm.setRegId(userId);
		lsdpsm.setUpdId(userId);

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

	private List<Lsdpsd> insertLsdpsd(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, Lsdpsm lsdpsm,
			List<Lsdpsp> lsdpspList, String userId) {
        List<Lsdpsd> lsdpsdList = new ArrayList<>();
        List<Lsdpsp> imsiLsdpsp = new ArrayList<>();
        int index = 1;
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
            imsiLsdpsp = lsdpspList.stream().filter(x->x.getPurchaseNo().equals(deposit.getPurchaseNo()) && x.getPurchaseSeq().equals(deposit.getPurchaseSeq()))
                    .collect(Collectors.toList());
            if(deposit.getAvailableQty() < deposit.getDepositQty()){
                log.debug("input deposit qty is bigger than deposit available qty.");
                continue;
            }


            String depositSeq = StringUtils.leftPad(Integer.toString(index), 4, '0');
            Lsdpsd lsdpsd = new Lsdpsd(depositListWithPurchaseInfoData, lsdpsm, depositSeq, deposit, imsiLsdpsp.get(0));

			String rackNo = this.getDefaultRack(depositListWithPurchaseInfoData.getStorageId(), deposit.getRackNo()); //

			// lsdpsd.setRackNo(deposit.getRackNo()); // ????????? 2021-12-13
			lsdpsd.setRackNo(rackNo); // ????????? 2021-12-13

            Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(), lsdpsd.getInputSeq());
			// lspchd.setDepositNo(lsdpsd.getDepositNo());
			// lspchd.setDepositSeq(lsdpsd.getDepositSeq());

			lspchd.setUpdId(userId);

            jpaLspchdRepository.save(lspchd);
            
			lsdpsd.setOrderId(lspchd.getOrderId() == null ? null : lspchd.getOrderId());
			lsdpsd.setOrderSeq(lspchd.getOrderSeq() == null ? null : lspchd.getOrderSeq());
            lsdpsd.setDefectYn(deposit.getDefectYn() == null || deposit.getDefectYn().trim().equals("")? StringFactory.getGbTwo() : deposit.getDefectYn());

			lsdpsdList.add(lsdpsd);

			lsdpsd.setRegId(userId);
			lsdpsd.setUpdId(userId);

			jpaLsdpsdRepository.save(lsdpsd);

            index++;
        }
        return lsdpsdList;
    }

	private List<Lsdpsd> insertEtcLsdpsd(InsertDepositEtcRequestData p, Lsdpsm lsdpsm, String userId) {
		int index = 1;


		List<Lsdpsd> lsdpsdList = new ArrayList<>();
		
		for (InsertDepositEtcRequestData.Item deposit : p.getItems()) {


			String depositSeq = StringUtils.leftPad(Integer.toString(index), 4, '0');
			Lsdpsd lsdpsd = new Lsdpsd(lsdpsm, depositSeq, deposit);

			String rackNo = this.getDefaultRack(lsdpsm.getStoreCd(), deposit.getRackNo()); //
			lsdpsd.setRackNo(rackNo);

			lsdpsd.setRegId(userId);
			lsdpsd.setUpdId(userId);

			jpaLsdpsdRepository.save(lsdpsd);

			lsdpsdList.add(lsdpsd);
			
			index++;
		}
		return lsdpsdList;
	}

	private void insertLsdpss(DepositInsertRequestData depositInsertRequestData, String userId) {
        Lsdpss lsdpss = new Lsdpss(depositInsertRequestData);

		lsdpss.setRegId(userId);
		lsdpss.setUpdId(userId);

        jpaLsdpssRepository.save(lsdpss);
    }

	private Lsdpss saveLsdpss(Lsdpsm lsdpsm, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			String userId) {
        Lsdpss lsdpss = jpaLsdpssRepository.findByDepositNoAndEffEndDt(lsdpsm.getDepositNo(), Utilities.getStringToDate(StringFactory.getDoomDay()));
        if(lsdpss == null){
            lsdpss = new Lsdpss(lsdpsm);
			lsdpss.setRegId(userId);
        }
        else{
            Lsdpss newLsdpss = new Lsdpss(lsdpsm);

			newLsdpss.setRegId(userId);
			newLsdpss.setUpdId(userId);

            jpaLsdpssRepository.save(newLsdpss);
        }

		lsdpss.setUpdId(userId);
        jpaLsdpssRepository.save(lsdpss);
        return lsdpss;
    }

	private Lsdpss saveEtcLsdpss(Lsdpsm lsdpsm, String depositStatus, String userId) {
		Lsdpss lsdpss = jpaLsdpssRepository.findByDepositNoAndEffEndDt(lsdpsm.getDepositNo(),
				Utilities.getStringToDate(StringFactory.getDoomDay()));

		if (lsdpss == null) {
			Lsdpss newLsdpss = new Lsdpss(lsdpsm, depositStatus);

			newLsdpss.setRegId(userId);
			newLsdpss.setUpdId(userId);

			jpaLsdpssRepository.save(newLsdpss);

		} else {

			lsdpss.setUpdId(userId);

			jpaLsdpssRepository.save(lsdpss);

			Lsdpss newLsdpss = new Lsdpss(lsdpsm, depositStatus);

			newLsdpss.setRegId(userId);
			newLsdpss.setUpdId(userId);
			jpaLsdpssRepository.save(newLsdpss);
		}

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

	private void saveEtcLsdpds(List<Lsdpsd> lsdpsdList, String depositStatus, String userId) {
		int ind = lsdpsdList.size();
		List<Lsdpds> lsdpdsList = new ArrayList<>();
		for (int i = 0; i < ind; i++) {
			Lsdpsd lsdpsd = lsdpsdList.get(i);

			Lsdpds lsdpds = jpaLsdpdsRepository.findByDepositNoAndDepositSeqAndEffEndDt(lsdpsd.getDepositNo(),
					lsdpsd.getDepositSeq(), Utilities.getStringToDate(StringFactory.getDoomDay()));
			if (lsdpds == null) {
				Lsdpds newLsdpds = new Lsdpds(lsdpsd, depositStatus);
				newLsdpds.setRegId(userId);
				newLsdpds.setUpdId(userId);

				jpaLsdpdsRepository.save(newLsdpds);
			} else {

				lsdpds.setUpdId(userId);

				jpaLsdpdsRepository.save(lsdpds);

				Lsdpds newLsdpds = new Lsdpds(lsdpsd, depositStatus);

				newLsdpds.setRegId(userId);
				newLsdpds.setUpdId(userId);

				jpaLsdpdsRepository.save(newLsdpds);
			}

		}
	}

	private void saveLsdpds(List<Lsdpsd> lsdpsdList, DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			String userId) {
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

				newLsdpds.setRegId(userId);

				newLsdpds.setUpdId(userId);

                jpaLsdpdsRepository.save(newLsdpds);
            }

			lsdpds.setUpdId(userId);

            jpaLsdpdsRepository.save(lsdpds);
        }
    }

	private List<Lsdpsp> saveLsdpsp(DepositInsertRequestData depositInsertRequestData, String userId) {
        List<DepositInsertRequestData.Item> itemList = depositInsertRequestData.getItems();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        for(DepositInsertRequestData.Item item : itemList){
            Lsdpsp lsdpsp = jpaLsdpspRepository.findByPurchaseNoAndPurchaseSeq(item.getPurchaseNo(), item.getPurchaseSeq());
            if(lsdpsp.getPurchasePlanQty() < item.getDepositQty()){
                log.debug("puchase_take_qty is bigger than purchase_plan_qty.");
                throw new NumberFormatException();
            }
            lsdpsp.setPurchaseTakeQty(item.getDepositQty());

			lsdpsp.setUpdId(userId);

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

	private List<Ititmt> saveItitmt(DepositInsertRequestData depositInsertRequestData, String userId) {
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

			ititmt.setUpdId(userId);

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
     * ???????????? : ??????????????? ?????? ???????????? ?????? ????????? ???????????? ??????
     */
    public DepositSelectDetailResponseData getDetail(String depositNo){
        TypedQuery<Lsdpsd> query = em.createQuery("select d from Lsdpsd d " +
                "left join fetch d.lspchd lspchd " +
				"join fetch d.ititmm itm " + "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
				+ "left join fetch itm.itvari3 itv3 " +

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
            Itasrt itasrt = lsdpsd.getItitmm().getItasrt();
            item.setItemNm(itasrt.getAssortNm());
			// Utilities.setOptionNames(item,itasrt.getItvariList());
            item.setPurchaseNo(lsdpsd.getLspchd().getPurchaseNo());
            item.setPurchaseSeq(lsdpsd.getLspchd().getPurchaseSeq());
            item.setDepositQty(lsdpsd.getDepositQty());
			item.setRackNo(lsdpsd.getRackNo());
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
     * ?????? ?????? ???????????? ?????? ?????? ?????????????????? ????????? ?????? ????????? ?????? ???????????? ??????
     */
	public DepositSelectDetailRequestData updateDetail(DepositSelectDetailRequestData depositSelectDetailRequestData,
			String userId) {
        List<DepositSelectDetailRequestData.Item> itemList = depositSelectDetailRequestData.getItems();
        List<Lsdpsd> lsdpsdList = jpaLsdpsdRepository.findByDepositNo(depositSelectDetailRequestData.getDepositNo());
        for (int i = 0; i < lsdpsdList.size(); i++) {
            lsdpsdList.get(i).setMemo(itemList.get(i).getMemo());

			lsdpsdList.get(i).setUpdId(userId);

            jpaLsdpsdRepository.save(lsdpsdList.get(i));
        }
        return depositSelectDetailRequestData;
    }

    /**
     * ?????? ???????????? ???????????? ?????? (?????? - ???????????????)
     * assortId??? null????????? ""??? ?????? ????????? ?????????
     * assortNm??? like ??????
     */
	public DepositSelectListResponseData getList(String vendorId, String assortId, String assortNm, LocalDate startDt,
			LocalDate endDt, String storageId, String memo) {
        LocalDateTime start = startDt.atStartOfDay();
        LocalDateTime end = endDt.atTime(23,59,59);
        List<DepositSelectListResponseData.Deposit> depositList = new ArrayList<>();
        List<Lsdpsd> lsdpsdList = jpaLsdpsdRepository.findDepositList(start, end, assortId, assortNm, vendorId, storageId);

        for(Lsdpsd lsdpsd : lsdpsdList){
            DepositSelectListResponseData.Deposit deposit = new DepositSelectListResponseData.Deposit(lsdpsd);
			deposit.setVendorId(lsdpsd.getLsdpsm().getVendorId());
            deposit.setVdNm(lsdpsd.getLsdpsm().getCmvdmr() == null? "":lsdpsd.getLsdpsm().getCmvdmr().getVdNm());
            Itasrt itasrt = lsdpsd.getItitmm().getItasrt();
            deposit.setAssortNm(itasrt.getAssortNm());
            // 21-11-11 ?????? ??????
            deposit.setWeight(itasrt.getWeight());
			// Utilities.setOptionNames(deposit, itasrt.getItvariList()); //????????????????????????
			// 2022-02-09

//            List<Lsdpsp> lsdpspList = lsdpsd.getLspchd().getLsdpsp();
//            lsdpspList.stream().filter(x->x.getPlanStatus().equals(StringFactory.getGbOne())).map(x->x.getq).reduce((a,b)->a+b).get();
            deposit.setDepositQty(lsdpsd.getDepositQty());
            //
            depositList.add(deposit);
        }
		DepositSelectListResponseData depositSelectListResponseData = new DepositSelectListResponseData(startDt, endDt,
				assortId, assortNm, vendorId, memo);
        depositSelectListResponseData.setDepositList(depositList);
        return depositSelectListResponseData;
    }

    /**
     * ??????????????? ?????? ?????? ??????????????? ???????????? ?????????????????? ???????????? ?????? 
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
     * lsdpsp??? DepositSelectListResponseData ????????? ????????? ??????
     */
    private DepositListWithPurchaseInfoData.Deposit makeDepositSelectListResponseData(Lsdpsp lsdpsp) {
        Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsp.getAssortId());
        DepositListWithPurchaseInfoData.Deposit deposit = new DepositListWithPurchaseInfoData.Deposit(itasrt, lsdpsp);
        return deposit;
    }

    /**
	 * ?????? ????????????????????? ???????????? ??? ???????????? ??????
	 * 
	 * @throws Exception
	 */
	private List<Lsdpsp> updateDepositQty(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			List<String> messageList, String userId) throws Exception {
        String storageId = depositListWithPurchaseInfoData.getStorageId();
        List<Lsdpsp> lsdpspList = new ArrayList<>();
        List<DepositListWithPurchaseInfoData.Deposit> depositList = new ArrayList<>();
        for(DepositListWithPurchaseInfoData.Deposit deposit : depositListWithPurchaseInfoData.getDeposits()){
			System.out.println("*****************************************************");
			System.out.println(deposit.getRackNo());

            Lsdpsp lsdpsp = this.getLsdpspWithLspchm(deposit.getDepositPlanId());//jpaLsdpspRepository.findByDepositPlanId(deposit.getDepositPlanId());
            Long purchasePlanQty = lsdpsp.getPurchasePlanQty() == null? 0l : lsdpsp.getPurchasePlanQty();
            Long purchaseTakeQty = lsdpsp.getPurchaseTakeQty() == null? 0l : lsdpsp.getPurchaseTakeQty();;
            Long availableQty = purchasePlanQty - purchaseTakeQty;
            String dealtypeCd = lsdpsp.getDealtypeCd();
//            boolean isOrderPurchase = dealtypeCd.equals(StringFactory.getGbOne()); // ????????????
//            boolean isPartDeposit = availableQty >= deposit.getDepositQty(); // ????????????
            boolean isCompleteDeposit = availableQty == deposit.getDepositQty(); // ????????????
            boolean notGoodsPurchaseAndAvailableQty = availableQty >= deposit.getDepositQty() && !StringFactory.getGbOne().equals(dealtypeCd); // ?????????????????? >= ????????? && ???????????? ??????
            boolean orderPurchaseAndCompleteDeposit = isCompleteDeposit && StringFactory.getGbOne().equals(dealtypeCd);
            if(deposit.getDepositQty() == 0){
                log.debug(StringFactory.getStrDepositQtyZero());
                messageList.add(StringFactory.getStrDepositQtyZero());
                continue;
            }
            if(notGoodsPurchaseAndAvailableQty || orderPurchaseAndCompleteDeposit){ // '??????????????? ????????? ????????????or????????????' or '?????????????????? ????????????'
                lsdpsp.setPurchaseTakeQty(lsdpsp.getPurchaseTakeQty() + deposit.getDepositQty());

				lsdpsp.setUpdId(userId);

                jpaLsdpspRepository.save(lsdpsp);
            }
            else if(availableQty != deposit.getDepositQty() && dealtypeCd.equals(StringFactory.getGbOne())){ // ?????????????????? ????????????
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
            // lspchd.purchaseGb=02(????????????), lspchd.dealtypeCd = 03 (???????????? ????????????) ?????? ?????? lsdpsp??? ????????? ??????????????? (tbOrderDetail.statusCd = C03??? ????????? ????????????)
            if(lsdpsp.getPurchaseGb().equals(StringFactory.getGbTwo()) && lsdpsp.getDealtypeCd().equals(StringFactory.getGbThree())){
                List<TbOrderDetail> tbOrderDetailList = jpaTbOrderDetailRepository
                        .findByAssortIdAndItemIdAndQtyAndStatusCd(lsdpsp.getAssortId(), lsdpsp.getItemId(), lsdpsp.getPurchasePlanQty(), StringFactory.getStrC03());
                TbOrderDetail to = tbOrderDetailList.get(0);
                lsdpsp.setOrderId(to.getOrderId());
                lsdpsp.setOrderSeq(to.getOrderSeq());
                lsdpsp.setDealtypeCd(StringFactory.getGbOne()); // 03(????????????????????????) -> 01(????????????) ??? ??????
            }
            LocalDateTime purchaseDt = lspchm.getPurchaseDt();
			lsdpsp = this.changeLsdpspStatus(lsdpsp, isCompleteDeposit, userId);
            lsdpspList.add(lsdpsp);
            depositList.add(deposit);

			this.saveItitmt(purchaseDt, storageId, deposit, dealtypeCd, userId);

			// System.out.println(depositListWithPurchaseInfoData.getDepositDt());

			LocalDateTime localDateTime = LocalDateTime.parse(depositListWithPurchaseInfoData.getDepositDt(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


//			String storageId = p.get("storageId").toString();
//			LocalDateTime depositDt = (LocalDateTime) p.get("effStaDt");
//			String assortId = p.get("assortId").toString();
//			String itemId = p.get("itemId").toString();
//			String itemGrade = p.get("itemGrade").toString();
//			long qty = (long) p.get("depositQty");
//			float price = (float) p.get("price");
//
//			String rackNo = p.get("rackNo").toString();

			// ??????????????? ?????? ??? ??????

			HashMap<String, Object> p = new HashMap<String, Object>();

			p.put("storageId", storageId);
			p.put("effStaDt", localDateTime);
			p.put("assortId", deposit.getAssortId());
			p.put("itemId", deposit.getItemId());
			p.put("itemGrade", "11"); // ?????? ???????????? ??????
			p.put("depositQty", deposit.getDepositQty());
			p.put("price", deposit.getPurchaseCost());
			p.put("vendorId", depositListWithPurchaseInfoData.getVendorId());

			String rackNo = this.getDefaultRack(depositListWithPurchaseInfoData.getStorageId(), deposit.getRackNo()); //
			System.out.println("*************************************-----------------------------------------");
			System.out.println(rackNo);
			p.put("rackNo", rackNo);

			jpaStockService.plusDepositStock(p, userId);




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
     * ??????????????? ?????? lsdpsp??? ????????? ??????????????? ???????????? ????????? ????????? ????????? ?????????????????? ??????
     */
	private Lsdpsp changeLsdpspStatus(Lsdpsp lsdpsp, boolean isCompleteDeposit, String userId) {
        if(isCompleteDeposit){ // ?????? ????????? ??????
            lsdpsp.setPlanStatus(StringFactory.getGbFour()); // 04 ????????????
        }
        else{
            // ??????????????? ?????? : lsdpsp??? ????????? ?????? ??????, ????????? ????????? ????????????
            long remainQty = lsdpsp.getPurchasePlanQty() - lsdpsp.getPurchaseTakeQty();
            lsdpsp.setPurchasePlanQty(lsdpsp.getPurchaseTakeQty());
            Lsdpsp newLsdpsp = new Lsdpsp(this.getDepositPlanId(), lsdpsp);
            String newDepositPlanId = StringUtils.leftPad(jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsp()),9,'0');
            newLsdpsp.setDepositPlanId(newDepositPlanId);
            newLsdpsp.setPurchasePlanQty(remainQty);
            newLsdpsp.setPurchaseTakeQty(0l);
            lsdpsp.setPlanStatus(StringFactory.getGbFour());

			newLsdpsp.setRegId(userId);
			newLsdpsp.setUpdId(userId);

            jpaLsdpspRepository.save(newLsdpsp);
            return newLsdpsp;
        }

		lsdpsp.setUpdId(userId);

        jpaLsdpspRepository.save(lsdpsp);
        return lsdpsp;
    }

    private Ititmc saveItitmc(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData, LocalDateTime depositDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {

		throw new IllegalArgumentException("saveItitmc use ititmc");
//
//		ItitmcId ititmcId = new ItitmcId(storageId, depositDt, deposit);
//
//		System.out.println(ititmcId);
//
//		Ititmc ititmc = jpaItitmcRepository.findById(ititmcId).orElseGet(() -> null);
//
//
//		if (ititmc == null) {
//			ititmc = new Ititmc(storageId, depositDt, deposit);
//			ititmc.setVendorId(depositListWithPurchaseInfoData.getVendorId());
//
//			ititmc.setShipIndicateQty(0L);
////			ititmc.setShipIndicateQty(0);
//			Itasrt itasrt = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
//			ititmc.setOwnerId(itasrt.getOwnerId());
//			ititmc.setQty(deposit.getDepositQty());
//		} else {
//			ititmc.setQty(ititmc.getQty() + deposit.getDepositQty());
//			ititmc.setUpdId(depositListWithPurchaseInfoData.getRegId());
//
//			// ititmc.setUpdDt(new Date());
//
//		}
//
//		/*
//		 * ititmc.setVendorId(depositListWithPurchaseInfoData.getVendorId());
//		 * 
//		 * ititmc.setShipIndicateQty(0L); // ititmc.setShipIndicateQty(0); Itasrt itasrt
//		 * = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
//		 * ititmc.setOwnerId(itasrt.getOwnerId());
//		 * ititmc.setQty(deposit.getDepositQty());
//		 */
//        jpaItitmcRepository.save(ititmc);
//        return ititmc;
    }

	private Ititmc saveRactItitmc(DepositListWithPurchaseInfoData depositListWithPurchaseInfoData,
			LocalDateTime depositDt, String storageId, DepositListWithPurchaseInfoData.Deposit deposit) {

		throw new IllegalArgumentException("saveItitmc use ititmc");

//		ItitmcId ititmcId = new ItitmcId(storageId, depositDt, deposit);
//
//		System.out.println(ititmcId);
//
//		Ititmc ititmc = jpaItitmcRepository.findById(ititmcId).orElseGet(() -> null);
//
//		if (ititmc == null) {
//			ititmc = new Ititmc(storageId, depositDt, deposit);
//			ititmc.setVendorId(depositListWithPurchaseInfoData.getVendorId());
//
//			ititmc.setShipIndicateQty(0L);
////			ititmc.setShipIndicateQty(0);
//			Itasrt itasrt = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
//			ititmc.setOwnerId(itasrt.getOwnerId());
//			ititmc.setQty(deposit.getDepositQty());
//			ititmc.setStockGb("02");
//		} else {
//			ititmc.setQty(ititmc.getQty() + deposit.getDepositQty());
//			ititmc.setUpdId(depositListWithPurchaseInfoData.getRegId());
//
//			// ititmc.setUpdDt(new Date());
//
//		}
//
//		/*
//		 * ititmc.setVendorId(depositListWithPurchaseInfoData.getVendorId());
//		 * 
//		 * ititmc.setShipIndicateQty(0L); // ititmc.setShipIndicateQty(0); Itasrt itasrt
//		 * = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
//		 * ititmc.setOwnerId(itasrt.getOwnerId());
//		 * ititmc.setQty(deposit.getDepositQty());
//		 */
//		jpaItitmcRepository.save(ititmc);
//		return ititmc;
	}

	private Ititmt saveItitmt(LocalDateTime purchaseDt, String storageId,
			DepositListWithPurchaseInfoData.Deposit deposit, String dealTypeCd, String userId) throws Exception {
        Ititmt ititmt = jpaItitmtRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt
                        (deposit.getAssortId(), deposit.getItemId(), storageId, StringFactory.getStrEleven(), purchaseDt); // dealtypeCd = '01'??? ??????(??????)
        if(ititmt == null){
//            ititmt = new Ititmt(purchaseDt, storageId, deposit);
            log.debug("There is no proper ititmt. Check data. assortId : " + deposit.getAssortId() + ", itemId : " + deposit.getItemId());

			// throw new Exception();
			// throw new RuntimeException(e);
			throw new RuntimeException("There is no proper ititmt. Check data.");

			// return null;
        }
        else {
            ititmt.setTempQty(ititmt.getTempQty() - deposit.getDepositQty());
        }

		if (!dealTypeCd.equals(StringFactory.getGbTwo())) { // ??????????????? ???
			if (ititmt.getTempIndicateQty() - deposit.getDepositQty() > 0) {
				ititmt.setTempIndicateQty(ititmt.getTempIndicateQty() - deposit.getDepositQty());
			} else {
				ititmt.setTempIndicateQty(0L);
			}

		}

		ititmt.setUpdId(userId);

        jpaItitmtRepository.save(ititmt);
        return ititmt;
    }
    /**
     * depositPlanId ?????? ??????
     */
    private String getDepositPlanId(){
        String depositPlanId = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsp());
        depositPlanId = StringUtils.leftPad(depositPlanId,9,'0');
        return depositPlanId;
    }

	private String getDefaultRack(String storageId, String rackNo) {

		String r = "";

		if (rackNo.equals("999999")) {
			HashMap<String, Object> p = new HashMap<String, Object>();

			p.put("storageId", storageId);

			HashMap<String, Object> o = myBatisCommonService.getCommonDefaultRack(p);

			r = o.get("storageId").toString();

		} else {
			r = rackNo;
		}
		return r;

	}

	private List<HashMap<String, Object>> saveMoveOrShip(List<Lsdpsd> list, String userId) {

		List<HashMap<String, Object>> ret = new ArrayList<HashMap<String, Object>>();

		for (Lsdpsd lsdpsd : list) {

//			Lspchd jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(), lsdpsd.getInputSeq());

			Lspchd lspchd = jpaLspchdRepository.findByPurchaseNoAndPurchaseSeq(lsdpsd.getInputNo(),
					lsdpsd.getInputSeq());

			// ??????????????? ????????????????????? ??????
			if (lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbOne())
					|| lspchd.getLspchm().getDealtypeCd().equals(StringFactory.getGbThree())) {


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

//					Itasrt itasrt = jpaItasrtRepository.findByAssortId(lsdpsd.getAssortId());
					
					Itasrt itasrt2 = jpaItasrtRepository.findByAssortId(assortId2); // ????????????????????? ????????? ??????????????????

					if (itasrt2.getAssortGb().equals(StringFactory.getGbOne())) { // ??????



						// ??????????????? ????????? ????????? ???????????? ????????????
						List<String> r = jpaShipService.saveShipIndicateByDeposit(lsdpsd, userId);
						if (r.size() > 0) {
							HashMap<String, Object> p = new HashMap<String, Object>();

							p.put("type", "ship");
							p.put("shipId", r.get(0));
							ret.add(p);

						}

					} else { // if(tbOrderDetail.getAssortGb().equals(StringFactory.getGbTwo())){ // ??????

						System.out.println("-----------------------??????------------------------------");

						if (tbOrderDetail.getStorageId().equals(lspchm.getStoreCd())) {
							// ??????????????? ????????? ????????? ???????????? ????????????
							System.out.println(
									"----------------------22 saveShipIndicateByDeposit----------------------");

							List<String> r = jpaShipService.saveShipIndicateByDeposit(lsdpsd, userId);
							if (r.size() > 0) {
								HashMap<String, Object> p = new HashMap<String, Object>();

								p.put("type", "ship");
								p.put("shipId", r.get(0));
								ret.add(p);

							}
						} else {
							// ??????????????? ????????? ????????? ???????????? ????????????
							System.out.println("----------------------33 saveOrderMoveByDeposit----------------------");

							List<String> r = jpaMoveService.saveOrderMoveByDeposit(lsdpsd, userId);

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

	@Transactional
	public String insertEtcDeposit(InsertDepositEtcRequestData p, String userId) throws Exception {




		// 1. lsdpsm ??????
		Lsdpsm lsdpsm = this.insertEtcLsdpsm(p, userId);
		// 2. lsdpss ?????? (?????? ????????? ??????)
		this.saveEtcLsdpss(lsdpsm, "01", userId);
		// 3. lsdpsd ?????? (?????? ?????????)
		List<Lsdpsd> lsdpsdList = this.insertEtcLsdpsd(p, lsdpsm, userId);
		// 4. lsdpds ?????? (?????? ????????? ??????)
		this.saveEtcLsdpds(lsdpsdList, "01", userId);

		// 5.????????????
		for (Lsdpsd o : lsdpsdList) {

			HashMap<String, Object> m = new HashMap<String, Object>();


			m.put("storageId", p.getStorageId());
			m.put("effStaDt",
					LocalDateTime.parse(p.getDepositDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			m.put("assortId", o.getAssortId());
			m.put("itemId", o.getItemId());
			m.put("itemGrade", o.getItemGrade()); // ?????? ???????????? ??????
			m.put("depositQty", o.getDepositQty());
			m.put("price", o.getExtraUnitcost());
			m.put("vendorId", p.getVendorId());

			// String rackNo = this.getDefaultRack(p.getStorageId(), o.getRackNo()); //
			// System.out.println("*************************************-----------------------------------------");
			// System.out.println(rackNo);

			m.put("rackNo", o.getRackNo());

			jpaStockService.plusDepositStock(m, userId);

		}

		return lsdpsm.getDepositNo();
	}

	public DepositEtcItemResponseData getDepositEtcItem(String etcId, String depositGb) {
		Lsdpsm lsdpsm = jpaLsdpsmRepository.findByDepositNoAndDepositGb(etcId, depositGb);
		List<Lsdpsd> l = jpaLsdpsdRepository.findEtcItem(etcId, depositGb);

		if (lsdpsm == null) {
			return null;
		}

		DepositEtcItemResponseData r = new DepositEtcItemResponseData(lsdpsm);

		List<DepositEtcItemResponseData.Item> items = new ArrayList<>();

		for (Lsdpsd o : l) {

			Tmmapi tmmapi = jpaTmmapiRepository.findByChannelGbAndAssortId(StringFactory.getGbOne(), o.getAssortId())
					.orElseGet(() -> null);

			DepositEtcItemResponseData.Item item = new DepositEtcItemResponseData.Item(o);

			String channelGoodsNo = tmmapi == null ? null : tmmapi.getChannelGoodsNo();

			item.setChannelGoodsNo(channelGoodsNo);

			items.add(item);
		}

		r.setItems(items);

		return r;

	}

	public DepositEtcItemListResponseData getDepositEtcItems(LocalDate startDt, LocalDate endDt, String depositNo,
			String assortId, String assortNm, String storageId, String depositGb) {

		LocalDateTime start = startDt.atStartOfDay();
		LocalDateTime end = endDt.atTime(23, 59, 59);

		List<Lsdpsd> l = jpaLsdpsdRepository.findEtcItems(start, end, depositNo, depositGb, assortId, assortNm,
				storageId);

		List<DepositEtcItemListResponseData.Item> items = new ArrayList<>();

		DepositEtcItemListResponseData r = new DepositEtcItemListResponseData(startDt, endDt, assortId,
				assortNm, depositNo, depositGb, storageId);

		for (Lsdpsd o : l) {

			Tmmapi tmmapi = jpaTmmapiRepository.findByChannelGbAndAssortId(StringFactory.getGbOne(), o.getAssortId())
					.orElseGet(() -> null);

			DepositEtcItemListResponseData.Item item = new DepositEtcItemListResponseData.Item(o);

			String channelGoodsNo = tmmapi == null ? null : tmmapi.getChannelGoodsNo();

			item.setChannelGoodsNo(channelGoodsNo);

			items.add(item);
		}



		r.setItems(items);

		return r;

	}

}
