package io.spring.service.stock;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Service;

import io.spring.jparepos.common.JpaCmstgmRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItasrtRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.goods.JpaXmlTestRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.common.entity.Cmstgm;
import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.entity.Ititmc;
import io.spring.model.goods.entity.XmlTest;
import io.spring.model.goods.idclass.ItitmcId;
import io.spring.model.stock.reponse.GoodsStockXml;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaStockService {

	private final JpaCommonService jpaCommonService;
	private final JpaLsdpspRepository jpaLsdpspRepository;
	private final JpaLspchdRepository jpaLspchdRepository;
	private final JpaSequenceDataRepository jpaSequenceDataRepository;
	private final JpaTbOrderDetailRepository jpaTbOrderDetailRepository;
	private final JpaLsshpmRepository jpaLsshpmRepository;
	private final JpaLsshpsRepository jpaLsshpsRepository;
	private final JpaLsshpdRepository jpaLsshpdRepository;
	private final JpaItitmcRepository jpaItitmcRepository;
	private final JpaLsdpsmRepository jpaLsdpsmRepository;
	private final JpaCmstgmRepository jpaCmstgmRepository;
	private final JpaXmlTestRepository jpaXmlTestRepository;


	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

	private final MyBatisCommonService myBatisCommonService;

	private final JpaItasrtRepository jpaItasrtRepository;

	private final EntityManager em;

	public int plusDepositStock(HashMap<String, Object> p, String userId) {
		
		String storageId = p.get("storageId").toString();
		LocalDateTime depositDt = (LocalDateTime) p.get("effStaDt");
		String assortId = p.get("assortId").toString();
		String itemId = p.get("itemId").toString();
		String itemGrade = p.get("itemGrade").toString();

		// String userId = p.get("userId") == null ? "plusDepositStock did" :
		// p.get("userId").toString();
		// String userId = p.get("userId") == null ? "minusShipStockByOrder did" :
		// p.get("userId").toString();

		long qty = (long)p.get("depositQty");
		float price = (float)p.get("price");

		String rackNo = this.getDefaultRack(storageId, p.get("rackNo").toString());


		//int qty = Integer.parseInt(p.get("depositQty").toString());
		

		String vendorId = p.get("vendorId").toString();
		
		// (String storageId, LocalDateTime depositDt,
		// DepositListWithPurchaseInfoData.Deposit deposit)

		// (String storageId, LocalDateTime effStaDt, String assortId, String itemId,
		// String itemGrade) {
		ItitmcId ititmcId = new ItitmcId(storageId, depositDt, assortId, itemId, itemGrade);

		Ititmc ititmc = jpaItitmcRepository.findById(ititmcId).orElseGet(() -> null);

		if (ititmc == null) {

//		 Ititmc(String storageId, LocalDateTime effStaDt, String assortId, String itemId, String itemGrade,
			// Float localPrice, Long qty)

			ititmc = new Ititmc(storageId, depositDt, assortId, itemId, itemGrade, price, qty);

			ititmc.setRegId(userId);

			ititmc.setVendorId(vendorId);
			Itasrt itasrt = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
			ititmc.setOwnerId(itasrt.getOwnerId());

		} else {
			ititmc.setQty(ititmc.getQty() + qty);

		}

		ititmc.setUpdId(userId);

		jpaItitmcRepository.save(ititmc);

		if (rackNo != null) {
			// 의 재고를 조회함
			Ititmc imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(assortId,
					itemId, rackNo, itemGrade, depositDt);

			if (imc_rack == null) {

//				 Ititmc(String storageId, LocalDateTime effStaDt, String assortId, String itemId, String itemGrade,
				// Float localPrice, Long qty)

				imc_rack = new Ititmc(rackNo, depositDt, assortId, itemId, itemGrade, price, qty);

				imc_rack.setRegId(userId);

				imc_rack.setVendorId(vendorId);
				Itasrt itasrt = jpaItasrtRepository.findByAssortId(ititmc.getAssortId());
				imc_rack.setOwnerId(itasrt.getOwnerId());

			} else {
				imc_rack.setQty(imc_rack.getQty() + qty);

			}

			imc_rack.setUpdId(userId);

			jpaItitmcRepository.save(imc_rack);
		}


		return 1;

//		p.put("assortId", lsshpd.getAssortId());
//		p.put("itemId", lsshpd.getItemId());
//		p.put("effStaDt", lsshpd.getExcAppDt());
//		p.put("itemGrade", "11");
//		p.put("storageId", lsshpm.getStorageId());
//		p.put("rackNo", lsshpd.getRackNo());
//		p.put("depositQty", shipIndQty);

		
		// 입고처리만들어야함.

	}

	public int minusIndicateStockByOrder(HashMap<String, Object> p, String userId) {

		System.out.println("----------------------minusIndicateStockByOrder----------------------");

		System.out.println(p);

		/*
		 * p.get("assortId").toString() p.get("itemId").toString() p.get("effStaDt")
		 * p.get("itemGrade").toString() p.get("storageId").toString()
		 * p.get("rackNo").toString() p.get("qty").toString()
		 * 
		 */
		// ititmc 조회
		// 창고의 재고를 조회함
		
		long shipQty = (Long) p.get("qty");
		// String userId = p.get("userId") == null ? "minusIndicateStockByOrder did" :
		// p.get("userId").toString();
		
		Ititmc imc_storage = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("storageId").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);

		Ititmc imc_rack = null;

		if (p.get("rackNo") != null) {
			// 의 재고를 조회함
			imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
					p.get("assortId").toString(), p.get("itemId").toString(), p.get("rackNo").toString(),
					p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

			);
		}
		
		if(imc_storage==null) {
			throw new IllegalArgumentException("no stockQty check..");
			// return -1;
		}else {
			// long shipAvailQty = imc_storage.getQty() - imc_storage.getShipIndicateQty();
			// if (shipAvailQty < qty) { // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
			// log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " +
			// qty);
			// return -1;
			// }

			long qty = imc_storage.getQty() == null ? 0l : imc_storage.getQty(); // ititmc 재고량
			long shipIndQty = imc_storage.getShipIndicateQty() == null ? 0l : imc_storage.getShipIndicateQty(); // ititmc
																												// 출고예정량
			long canShipQty = qty - shipIndQty; // 출고가능량
			if (canShipQty <= 0) { // 출고 불가
				log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + canShipQty + ", 주문량 : " + shipQty);
				log.debug("출고 또는 이동이 불가합니다.");
				throw new IllegalArgumentException("no stockQty check..");
				// return -1;
			}
			if (shipQty <= canShipQty) { // 이 차례에서 출고 완료 가능
				imc_storage.setShipIndicateQty(shipIndQty + shipQty);

				imc_storage.setUpdId(userId);

				jpaItitmcRepository.save(imc_storage);

			} else {
				throw new IllegalArgumentException("stockQty check..");
			}

		}

		// todo:rackno가 무조건 들어가야하는가??
		// 들어가야한다면 로직이 수정되야할듯

		if (imc_rack == null) {
			log.debug("해당건의 rackNo 정보없음");
		} else {
			// long shipAvailQty = imc_storage.getQty() - imc_storage.getShipIndicateQty();
			// if (shipAvailQty < qty) { // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
			// log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " +
			// qty);
			// return -1;
			// }

			long qty = imc_rack.getQty() == null ? 0l : imc_rack.getQty(); // ititmc 재고량
			long shipIndQty = imc_rack.getShipIndicateQty() == null ? 0l : imc_rack.getShipIndicateQty(); // ititmc
																											// 출고예정량
			long canShipQty = qty - shipIndQty; // 출고가능량
			if (canShipQty <= 0) { // 출고 불가
				log.debug("rack 주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + canShipQty + ", 주문량 : " + shipQty);
				log.debug("rack 출고 또는 이동이 불가합니다.");

			}
			if (shipQty <= canShipQty) { // 이 차례에서 출고 완료 가능
				imc_rack.setShipIndicateQty(shipIndQty + shipQty);

				imc_rack.setUpdId(userId);

				jpaItitmcRepository.save(imc_rack);

			}

			// 재고차감로직
			//

		}


		// p안에 내용
		// assortId
		// itemId
		// effStaDt
		// itemGrade
		// storageId
		// rackNo
		// qty

		// 1.스토리지의 재고차감
		// 2.랙의 재고차감

		return 1;
	}

	// 출고처리하는로직을 만들어야함.
	public int minusShipStockByOrder(HashMap<String, Object> p, String userId) {
		System.out.println("----------------------minusShipStockByOrder----------------------");

		long shipQty = (Long) p.get("shipQty");

		// String userId = p.get("userId") == null ? "minusShipStockByOrder did" :
		// p.get("userId").toString();

		Ititmc imc_storage = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("storageId").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);

		Ititmc imc_rack = null;

		if (p.get("rackNo") != null) {
			// imc_rack의 재고를 조회함
			imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
					p.get("assortId").toString(), p.get("itemId").toString(), p.get("rackNo").toString(),
					p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

			);
		}


		if (imc_storage == null) {
			throw new IllegalArgumentException("no ShipStockQty check..");
			// return -1;
		} else {
			// long shipAvailQty = imc_storage.getQty() - imc_storage.getShipIndicateQty();
			// if (shipAvailQty < qty) { // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
			// log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " +
			// qty);
			// return -1;
			// }

			long qty = imc_storage.getQty() == null ? 0l : imc_storage.getQty(); // ititmc 재고량
			long shipIndQty = imc_storage.getShipIndicateQty() == null ? 0l : imc_storage.getShipIndicateQty(); // ititmc

			if (shipIndQty < shipQty) {
				log.debug("출고량이 출고지시수량보다 많습니다. 출고가능 재고량 : " + shipIndQty + ", 주문량 : " + shipQty);
				log.debug("출고 또는 이동이 불가합니다.");
				throw new IllegalArgumentException("no stockQty check..");
			}

			if (qty < shipQty) {
				log.debug("출고량이 재고수량보다 많습니다. 재고 재고량 : " + qty + ", 주문량 : " + shipQty);
				log.debug("출고 또는 이동이 불가합니다.");
				throw new IllegalArgumentException("no stockQty check..");
			}

			imc_storage.setShipIndicateQty(shipIndQty - shipQty);
			imc_storage.setQty(qty - shipQty);
			imc_storage.setUpdId(userId);

			jpaItitmcRepository.save(imc_storage);

		}

		if (imc_rack == null) {
			log.debug("해당건의 rackNo 정보없음");
		} else {

			long qty = imc_rack.getQty() == null ? 0l : imc_rack.getQty(); // ititmc 재고량
			long shipIndQty = imc_rack.getShipIndicateQty() == null ? 0l : imc_rack.getShipIndicateQty(); // ititmc

			if (shipIndQty < shipQty) {
				log.debug("rack 출고량이 출고지시수량보다 많습니다. 출고가능 재고량 : " + shipIndQty + ", 주문량 : " + shipQty);
				log.debug("출고 또는 이동이 불가합니다.");
				throw new IllegalArgumentException("no stockQty check..");
			}

			if (qty < shipQty) {
				log.debug("rack 출고량이 재고수량보다 많습니다. 재고 재고량 : " + qty + ", 주문량 : " + shipQty);
				log.debug("출고 또는 이동이 불가합니다.");
				throw new IllegalArgumentException("no stockQty check..");
			}

			imc_rack.setShipIndicateQty(shipIndQty - shipQty);
			imc_rack.setQty(qty - shipQty);

			imc_rack.setUpdId(userId);

			jpaItitmcRepository.save(imc_rack);

		}

		return 1;

	}
	
	// 출고처리하는로직을 만들어야함.
	public int minusEtcShipStockByGoods(HashMap<String, Object> p, String userId) {
		System.out.println("----------------------minusEtcShipStockByGoods----------------------");

		long shipQty = (Long) p.get("shipQty");

		// String userId = p.get("userId") == null ? "minusEtcShipStockByGoods did" :
		// p.get("userId").toString();

		Ititmc imc_storage = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("storageId").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);

		Ititmc imc_rack = null;

		if (p.get("rackNo") != null) {
			// imc_rack의 재고를 조회함
			imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
					p.get("assortId").toString(), p.get("itemId").toString(), p.get("rackNo").toString(),
					p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

			);
		}


		if (imc_storage == null) {
			throw new IllegalArgumentException("no ShipStockQty check..");
			// return -1;
		} else {
			// long shipAvailQty = imc_storage.getQty() - imc_storage.getShipIndicateQty();
			// if (shipAvailQty < qty) { // ititmc에 있는 해당 상품 총량보다 주문량이 많은 경우
			// log.debug("주문량이 출고가능 재고량보다 많습니다. 출고가능 재고량 : " + shipAvailQty + ", 주문량 : " +
			// qty);
			// return -1;
			// }

			long qty = imc_storage.getQty() == null ? 0l : imc_storage.getQty(); // ititmc 재고량
			long shipIndQty = imc_storage.getShipIndicateQty() == null ? 0l : imc_storage.getShipIndicateQty(); // ititmc

			long ableQty = qty - shipIndQty; // 출고가능수량

			if (ableQty < shipQty) {
				log.debug("출고수량이 출고가능 수량보다 많습니다. 출고가능수량 : " + ableQty + ", 출고량 : " + shipQty);
				log.debug("기타출고 불가.");
				throw new IllegalArgumentException("no stockQty check..");
			}

			// imc_storage.setShipIndicateQty(shipIndQty - shipQty);
			imc_storage.setQty(qty - shipQty);

			imc_storage.setUpdId(userId);

			jpaItitmcRepository.save(imc_storage);

		}

		if (imc_rack == null) {
			log.debug("해당건의 rackNo 정보없음");
		} else {

			long qty = imc_rack.getQty() == null ? 0l : imc_rack.getQty(); // ititmc 재고량
			long shipIndQty = imc_rack.getShipIndicateQty() == null ? 0l : imc_rack.getShipIndicateQty(); // ititmc

			long ableQty = qty - shipIndQty; // 출고가능수량

			if (ableQty < shipQty) {
				log.debug("rack 출고수량이 출고가능 수량보다 많습니다. 출고가능수량 : " + ableQty + ", 출고량 : " + shipQty);
				log.debug("rack 기타출고 불가.");
				throw new IllegalArgumentException("no stockQty check..");
			}



			imc_rack.setQty(qty - shipQty);

			imc_rack.setUpdId(userId);

			jpaItitmcRepository.save(imc_rack);

		}

		return 1;

	}	

	public HashMap<String, Object> checkStockWhenDirect(String storageId, String assortId, String itemId, Long orderQty,
			String userId) {

		System.out.println("checkStockWhenDirect");

		Query query = em.createQuery("select ic from Ititmc ic " + "join fetch ic.cmstgm cm " + "where "
				+ "(?1 is null or trim(?1)='' or cm.upStorageId=?1) "

				+ "and (?2 is null or trim(?2)='' or ic.assortId=?2) "
				+ "and (?3 is null or trim(?3)='' or ic.itemId=?3) "

				+ "and ic.qty > 0 and ic.itemGrade='11'" + "order by ic.assortId,ic.itemId,ic.effStaDt ");
		query.setParameter(1, storageId).setParameter(2, assortId).setParameter(3, itemId);
		List<Ititmc> ititmcList = query.getResultList();

		Ititmc ititmc =null;
		
		for (Ititmc o : ititmcList) {

			long qty = o.getQty() == null ? 0 : o.getQty();
			long indicateQty = o.getShipIndicateQty() == null ? 0 : o.getShipIndicateQty();



			if (qty >= orderQty + indicateQty) {

				o.setShipIndicateQty(orderQty + indicateQty);
				o.setUpdId(userId);

				jpaItitmcRepository.save(o);

				ititmc = o;
				
				break;
				
				// this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne());
				// // 01 (출고지시) 하드코딩
				// return o;
			}

		}
		
		if (ititmc == null) {
			log.debug("20201 rack 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
			// return null;
		}

		Ititmc ititmc_store = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(assortId,
				itemId, storageId, "11", ititmc.getEffEndDt());
		
		if (ititmc_store == null) {
			log.debug("20202 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
			// return null;
		}


		long qty = ititmc_store.getQty() == null ? 0 : ititmc_store.getQty();
		long indicateQty = ititmc_store.getShipIndicateQty() == null ? 0 : ititmc_store.getShipIndicateQty();

		if (qty >= orderQty + indicateQty) {

			ititmc_store.setShipIndicateQty(orderQty + indicateQty);

			ititmc_store.setUpdId(userId);
			jpaItitmcRepository.save(ititmc_store);
		} else {
			log.debug("20203 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
		}

		HashMap<String, Object> r = new HashMap<String, Object>();
		r.put("store", ititmc_store);
		r.put("rack", ititmc);

		return r;
	}

	public HashMap<String, Object> checkStockWhenImport(String storageId, String assortId, String itemId, Long orderQty,
			String userId) {

		System.out.println("checkStockWhenImport");
		System.out.println("storageId =>" + storageId);
		System.out.println("assortId =>" + assortId);
		System.out.println("itemId =>" + itemId);

		Query query = em.createQuery("select ic from Ititmc ic " + "join fetch ic.cmstgm cm " + "where "
				+ "(?1 is null or trim(?1)='' or cm.upStorageId=?1) "

				+ "and (?2 is null or trim(?2)='' or ic.assortId=?2) "
				+ "and (?3 is null or trim(?3)='' or ic.itemId=?3) "

				+ "and ic.qty > 0 and ic.itemGrade='11'" + "order by ic.assortId,ic.itemId,ic.effStaDt ");
		query.setParameter(1, storageId).setParameter(2, assortId).setParameter(3, itemId);
		List<Ititmc> ititmcList = query.getResultList();

		Ititmc ititmc = null;

		for (Ititmc o : ititmcList) {
			System.out.println(o);
			long qty = o.getQty() == null ? 0 : o.getQty();
			long indicateQty = o.getShipIndicateQty() == null ? 0 : o.getShipIndicateQty();


			if (qty >= orderQty + indicateQty) {

				o.setShipIndicateQty(orderQty + indicateQty);

				o.setUpdId(userId);
				jpaItitmcRepository.save(o);

				ititmc = o;

				break;

				// this.makeShipDataByDeposit(ititmc, tbOrderDetail, StringFactory.getGbOne());
				// // 01 (출고지시) 하드코딩
				// return o;
			}

		}

		if (ititmc == null) {
			log.debug("20301 rack 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
			// return null;
		}

		Ititmc ititmc_store = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(assortId,
				itemId, storageId, "11", ititmc.getEffStaDt());

		if (ititmc_store == null) {
			log.debug("20302 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
			// return null;
		}


		long qty = ititmc_store.getQty() == null ? 0 : ititmc_store.getQty();
		long indicateQty = ititmc_store.getShipIndicateQty() == null ? 0 : ititmc_store.getShipIndicateQty();

		if (qty >= orderQty + indicateQty) {

			ititmc_store.setShipIndicateQty(orderQty + indicateQty);
			ititmc_store.setUpdId(userId);
			jpaItitmcRepository.save(ititmc_store);
		} else {
			log.debug("20303 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
		}

		HashMap<String, Object> r = new HashMap<String, Object>();
		r.put("store", ititmc_store);
		r.put("rack", ititmc);

		return r;
	}

	public String getUpStorageId(String rackNo) {
		Cmstgm cm = jpaCmstgmRepository.findById(rackNo).orElse(null);
		return cm == null ? "" : cm.getUpStorageId();

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

	public boolean checkRack(String storageId, String rackNo) {

		boolean isCheck = false;
		String r = "";

		if (rackNo.equals("999999")) {
			isCheck = true;
		} else {
			HashMap<String, Object> p = new HashMap<String, Object>();
			p.put("storageId", storageId);
			p.put("rackNo", rackNo);


			HashMap<String, Object> o = myBatisCommonService.checkRack(p);

			if (o != null) {
				isCheck = true;
			}

		}

		return isCheck;

	}

	public List<Ititmc> getItitmc(String storageId, String purchaseVendorId, String assortId, String assortNm) {

		// 랙재고를 가져옴.

		Query query = em.createQuery("select ic from Ititmc ic " + "join fetch ic.itasrt it "
				+ "left join fetch it.itbrnd ib " + "join fetch ic.cmstgm cm " + "join fetch ic.ititmm itm "
				+ "left join fetch itm.itvari1 itv1 " + "left join fetch itm.itvari2 itv2 "
				+ "left join fetch itm.itvari3 itv3 " + "where " + "(?1 is null or trim(?1)='' or cm.upStorageId=?1) "
				+ "and (?2 is null or trim(?2)='' or it.vendorId=?2) "
				+ "and (?3 is null or trim(?3)='' or ic.assortId=?3) "
				+ "and (?4 is null or trim(?4)='' or it.assortNm like concat('%',?4,'%')) and ic.qty > 0 "
				+ "order by ic.assortId,ic.effStaDt ");
		query.setParameter(1, storageId).setParameter(2, purchaseVendorId).setParameter(3, assortId).setParameter(4,
				assortNm);
		List<Ititmc> ititmcList = query.getResultList();
		return ititmcList;
	}

	/**
	 * 고도몰 goods_stock api로 재고숫자변경하는 함수
	 */
	public String godoGoodsStock(String goodsNo, String optionFl, Long totalStock){
		XmlTest x = jpaXmlTestRepository.findById("0").orElseGet(()->null);
//		GoodsStockXml goodsStockXml = new GoodsStockXml(goodsNo, optionFl, totalStock);
//		return goodsStockXml;
//		return this.makeGoodsStockXml(goodsStockXml,null);
		return x.getXml();
	}

	private String makeGoodsStockXml(GoodsStockXml goodsStockXml, String assortId){
		String xmlContent = null;
		String ret="";
		try {
			// Create JAXB Context
			JAXBContext jaxbContext = JAXBContext.newInstance(GoodsStockXml.class);

			// Create Marshaller
			Marshaller marshaller = jaxbContext.createMarshaller();

			// Print XML String to Console
			StringWriter stringWriter = new StringWriter();

			// Write XML to StringWriter
			marshaller.marshal(goodsStockXml, stringWriter);

			// Verify XML Content
			xmlContent = stringWriter.toString();
			System.out.println("----- : 저장할 xml : \\n"+xmlContent);
			log.debug("----- : 저장할 xml : \\n"+xmlContent);
//			ret =  getXmlUrl(assortId, xmlContent);
//            System.out.println("ret : "+ret);

		} catch (Exception e) {
			e.getMessage();
			System.out.println(e.getMessage());
		}

//		return ret;
		System.out.println("dsfsdsdfs + " + xmlContent);
		return xmlContent;
	}

}
