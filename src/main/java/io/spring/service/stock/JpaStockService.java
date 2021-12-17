package io.spring.service.stock;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import io.spring.jparepos.common.JpaCmstgmRepository;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.jparepos.deposit.JpaLsdpsmRepository;
import io.spring.jparepos.deposit.JpaLsdpspRepository;
import io.spring.jparepos.goods.JpaItitmcRepository;
import io.spring.jparepos.order.JpaTbOrderDetailRepository;
import io.spring.jparepos.order.JpaTbOrderHistoryRepository;
import io.spring.jparepos.order.JpaTbOrderMasterRepository;
import io.spring.jparepos.purchase.JpaLspchdRepository;
import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import io.spring.model.common.entity.Cmstgm;
import io.spring.model.goods.entity.Ititmc;
import io.spring.service.common.JpaCommonService;
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

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

	private final EntityManager em;

	public int minusIndicateStockByOrder(HashMap<String, Object> p) {

		System.out.println("----------------------minusIndicateStockByOrder----------------------");

		/*
		 * p.get("assortId").toString() p.get("itemId").toString() p.get("effStaDt")
		 * p.get("itemGrade").toString() p.get("storageId").toString()
		 * p.get("rackNo").toString() p.get("qty").toString()
		 * 
		 */
		// ititmc 조회
		// 창고의 재고를 조회함
		
		long shipQty = (Long) p.get("qty");
		
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
	public int minusShipStockByOrder(HashMap<String, Object> p) {
		System.out.println("----------------------minusShipStockByOrder----------------------");

		long shipQty = (Long) p.get("shipQty");

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

			jpaItitmcRepository.save(imc_rack);

		}

		return 1;

	}

	public Ititmc checkStockWhenDirect(String storageId, String assortId, String itemId, Long orderQty) {

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
			jpaItitmcRepository.save(ititmc_store);
		} else {
			log.debug("20203 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
		}


		return ititmc_store;
	}

	public Ititmc checkStockWhenImport(String storageId, String assortId, String itemId, Long orderQty) {

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

		Ititmc ititmc_store = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffEndDt(assortId,
				itemId, storageId, "11", ititmc.getEffEndDt());

		if (ititmc_store == null) {
			log.debug("20302 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
			// return null;
		}


		long qty = ititmc_store.getQty() == null ? 0 : ititmc_store.getQty();
		long indicateQty = ititmc_store.getShipIndicateQty() == null ? 0 : ititmc_store.getShipIndicateQty();

		if (qty >= orderQty + indicateQty) {

			ititmc_store.setShipIndicateQty(orderQty + indicateQty);
			jpaItitmcRepository.save(ititmc_store);
		} else {
			log.debug("20303 store 출고지시수량이 주문수량보다 적음");
			throw new IllegalArgumentException("no stockQty check..");
		}

		return ititmc_store;
	}

	public String getUpStorageId(String rackNo) {
		Cmstgm cm = jpaCmstgmRepository.findById(rackNo).orElse(null);
		return cm == null ? "" : cm.getUpStorageId();

	}

}
