package io.spring.service.stock;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

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

	private final JpaTbOrderMasterRepository tbOrderMasterRepository;
	private final JpaTbOrderDetailRepository tbOrderDetailRepository;
	private final JpaTbOrderHistoryRepository tbOrderHistoryrRepository;

	private final EntityManager em;

	public int minusIndicateStockByOrder(HashMap<String, Object> p) {

		System.out.println("----------------------minusStockByOrder----------------------");

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
		// 의 재고를 조회함
		Ititmc imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("rackNo").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);
		
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

		long shipQty = (Long) p.get("qty");

		Ititmc imc_storage = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("storageId").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);
		// 의 재고를 조회함
		Ititmc imc_rack = jpaItitmcRepository.findByAssortIdAndItemIdAndStorageIdAndItemGradeAndEffStaDt(
				p.get("assortId").toString(), p.get("itemId").toString(), p.get("rackNo").toString(),
				p.get("itemGrade").toString(), (LocalDateTime) p.get("effStaDt")

		);

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

		return 1;

	}

}
