package io.spring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.spring.model.vendor.request.VendorInsertRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.model.common.entity.Suppliers;
import io.spring.model.common.entity.Testenum2;
import io.spring.service.common.JpaCommonService;
import io.spring.service.common.MyBatisCommonService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/common")
@RequiredArgsConstructor
public class CommonController {
	private final MyBatisCommonService myBatisCommonService;
	private final JpaCommonService jpaCommonService;

	@GetMapping(path="/brand_search")
		public ResponseEntity selectBrandSearchList(@RequestParam(required = false) String codeId,@RequestParam(required = false) String codeNm) {
	//public ResponseEntity selectBrandSearchList() {
		
		
		System.out.println(codeId);
		System.out.println(codeNm);
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		if(codeId==null) {
			codeId="%";
		}
		if(codeNm==null) {
			codeNm="%";
		}		
		
		
		param.put("codeId", codeId);
		param.put("codeNm", codeNm);
		
		List<HashMap<String, Object>> r = myBatisCommonService.getBrandSearchList(param);
		
		ApiResponseMessage res = null;
		
		if(r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS","", r);
		}
		else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}
		
		return ResponseEntity.ok(res);
	}	
	
	@GetMapping(path = "/purchase_vendor_search")
	public ResponseEntity selectPurchaseVendorSearchList(@RequestParam(required = false) String codeId,
			@RequestParam(required = false) String codeNm) {
//public ResponseEntity selectBrandSearchList() {

//		System.out.println(codeId);
		// System.out.println(codeNm);

		HashMap<String, Object> param = new HashMap<String, Object>();

		if (codeId == null) {
			codeId = "%";
		}
		if (codeNm == null) {
			codeNm = "%";
		}

		param.put("codeId", codeId);
		param.put("codeNm", codeNm);

		List<HashMap<String, Object>> r = myBatisCommonService.getPurchaseVendorSearchList(param);

		ApiResponseMessage res = null;

		if (r.size() > 0) {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("SUCCESS", "", r);
		} else {
			res = new ApiResponseMessage<List<HashMap<String, Object>>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/v3/suppliers")
	public ResponseEntity getSuppliers() {

		List<Suppliers> suppliers = jpaCommonService.getAllSuppliers();

		List<HashMap<String, Object>> r = new ArrayList<>();

		for (Suppliers o : suppliers) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("id", o.getSupplierId());
			m.put("name", o.getSupplierNm());

			r.add(m);
		}

		ApiResponseMessage res = new ApiResponseMessage<>("SUCCESS", "", r);

		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/storages")
	public ResponseEntity getStorages(@RequestParam @Nullable String storageType,
			@RequestParam @Nullable String storageId) {
//public ResponseEntity selectBrandSearchList() {

		HashMap<String, Object> map = new HashMap<>();

		HashMap<String, Object> c2 = new HashMap<String, Object>();

		if (storageId != null && !storageId.equals("")) {
			map.put("storageId", storageId);
		}

		if (storageType != null && !storageType.equals("")) {
			map.put("storageType", storageType);
		}


		List<HashMap<String, Object>> l = myBatisCommonService.getCommonStorage(map);

		for (HashMap<String, Object> o2 : l) {
			c2.put(o2.get("value").toString(), o2.get("label").toString());
		}

		HashMap<String, Object> m = new HashMap<String, Object>();

		if (l.size() > 0) {
			m.put("Storages", l);
		}

		m.put("storagesGridKey", c2);

		ApiResponseMessage res = null;

		if (m.size() > 0) {
			res = new ApiResponseMessage<HashMap<String, Object>>("SUCCESS", "", m);
		} else {
			res = new ApiResponseMessage<HashMap<String, Object>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/order_status")
	public ResponseEntity getOrderStatus(@RequestParam String cdMajor,
			@RequestParam @Nullable String cdMinor) {

		HashMap<String, Object> map = new HashMap<>();

		HashMap<String, Object> c2 = new HashMap<String, Object>();

		if (cdMajor != null && !cdMajor.equals("")) {
			map.put("cdMajor", cdMajor);
		}

		if (cdMinor != null && !cdMinor.equals("")) {
			map.put("cdMinor", cdMinor);
		}

		List<HashMap<String, Object>> l = myBatisCommonService.getCommonOrderStatus(map);

		for (HashMap<String, Object> o2 : l) {
			c2.put(o2.get("value").toString(), o2.get("label").toString());
		}

		HashMap<String, Object> m = new HashMap<String, Object>();

		if (l.size() > 0) {
			m.put("OrderStatus", l);
		}

		m.put("OrderStatusGridKey", c2);

		ApiResponseMessage res = null;

		if (m.size() > 0) {
			res = new ApiResponseMessage<HashMap<String, Object>>("SUCCESS", "", m);
		} else {
			res = new ApiResponseMessage<HashMap<String, Object>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);

	}

	@GetMapping(path = "/vendors")
	public ResponseEntity getVendors() {
//public ResponseEntity selectBrandSearchList() {

		HashMap<String, Object> p1 = new HashMap<String, Object>();
		HashMap<String, Object> p2 = new HashMap<String, Object>();

		HashMap<String, Object> c1 = new HashMap<String, Object>();
		HashMap<String, Object> c2 = new HashMap<String, Object>();

		List<HashMap<String, Object>> r = myBatisCommonService.getCommonPurchaseVendor(p1);

		// List<HashMap<String, Object>> l = myBatisCommonService.getCommonStorage(p2);

		for (HashMap<String, Object> o1 : r) {
			c1.put(o1.get("value").toString(), o1.get("label").toString());
		}

		// for (HashMap<String, Object> o2 : l) {
		// c2.put(o2.get("value").toString(), o2.get("label").toString());
		// }

		HashMap<String, Object> m = new HashMap<String, Object>();

		if (r.size() > 0) {
			m.put("PurchaseVendors", r);
		}

		// if (l.size() > 0) {
		// m.put("Storages", l);
		// }

		m.put("purchaseVendorsGridKey", c1);
		// m.put("storagesGridKey", c2);

		ApiResponseMessage res = null;

		if (m.size() > 0) {
			res = new ApiResponseMessage<HashMap<String, Object>>("SUCCESS", "", m);
		} else {
			res = new ApiResponseMessage<HashMap<String, Object>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);
	}

	/**
	 * new 거래처 정보 추가
	 */
	@PostMapping(path = "/vendor")
	public ResponseEntity createVendor(@RequestBody VendorInsertRequest vendorInsertRequest){
		String id = jpaCommonService.createVendor(vendorInsertRequest);
		ApiResponseMessage res = new ApiResponseMessage("SUCCESS", "", id);

		return ResponseEntity.ok(res);
	}

	@GetMapping(path = "/get_purchase_goods_init_data")
	public ResponseEntity selectPurchaseGoodsInitData() {
//public ResponseEntity selectBrandSearchList() {

		HashMap<String, Object> p1 = new HashMap<String, Object>();
		HashMap<String, Object> p2 = new HashMap<String, Object>();

		HashMap<String, Object> c1 = new HashMap<String, Object>();
		HashMap<String, Object> c2 = new HashMap<String, Object>();

		List<HashMap<String, Object>> r = myBatisCommonService.getCommonPurchaseVendor(p1);

		List<HashMap<String, Object>> l = myBatisCommonService.getCommonStorage(p2);

		for (HashMap<String, Object> o1 : r) {
			c1.put(o1.get("value").toString(), o1.get("label").toString());
		}

		for (HashMap<String, Object> o2 : l) {
			c2.put(o2.get("value").toString(), o2.get("label").toString());
		}

		HashMap<String, Object> m = new HashMap<String, Object>();

		if (r.size() > 0) {
			m.put("PurchaseVendors", r);
		}

		if (l.size() > 0) {
			m.put("Storages", l);
		}

		m.put("purchaseVendorsGridKey", c1);
		m.put("storagesGridKey", c2);

		ApiResponseMessage res = null;

		if (m.size() > 0) {
			res = new ApiResponseMessage<HashMap<String, Object>>("SUCCESS", "", m);
		} else {
			res = new ApiResponseMessage<HashMap<String, Object>>("ERROR", "ERROR", null);
		}

		return ResponseEntity.ok(res);
	}

	@PostMapping(path="/save_testenum2")
	public ResponseEntity saveTestenum2() {
		
		Testenum2 a = new Testenum2();
		System.out.println(a);
		
		//a.setAssortGb("03");
		jpaCommonService.saveTestEnum2(a);
		
		System.out.println(a);
		ApiResponseMessage res = null;
		
		if(a != null) {
			res = new ApiResponseMessage<Testenum2>("SUCCESS","", a);
		}
		else {
			res = new ApiResponseMessage<Testenum2>("ERROR", "ERROR", null);
		}
		
		return ResponseEntity.ok(res);
		
	}
		
	
}
