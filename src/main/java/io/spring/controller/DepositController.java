package io.spring.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.spring.model.deposit.request.DepositSelectDetailRequestData;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.deposit.response.DepositSelectDetailResponseData;
import io.spring.model.deposit.response.DepositSelectListResponseData;
import io.spring.model.deposit.response.PurchaseListInDepositModalData;
import io.spring.model.purchase.response.PurchaseSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.deposit.JpaDepositService;
import io.spring.service.purchase.JpaPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="/deposit")
@RequiredArgsConstructor
public class DepositController {
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaDepositService jpaDepositService;
    private final JpaPurchaseService jpaPurchaseService;
    private final JpaCommonService jpaCommonService;


//    @PostMapping(path="") // create
//    public ResponseEntity createDepositJpa(@RequestBody DepositInsertRequestData depositInsertRequestData){
//        String depositNo = jpaCommonService.getStrNumberId(StringFactory.getDUpperStr(), depositInsertRequestData.getDepositNo(), StringFactory.getStrSeqLsdpsm(), StringFactory.getIntEight());
//        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
//        depositNo = jpaDepositService.sequenceInsertDeposit(depositInsertRequestData);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
//        return ResponseEntity.ok(res);
//    }


    /**
     * 입고 - 발주선택창 (입고처리 -> 발주조회 > 조회) : 발주일과 구매처를 보내고 조회를 누르면 그에 맞는 발주 data를 보내줌.
     */
    @GetMapping(path = "/purchase/items")
    public ResponseEntity getChoosePurchaseModalList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
			@RequestParam @Nullable String vendorId, @RequestParam @Nullable String storageId) {
		PurchaseListInDepositModalData purchaseListInDepositModalData = jpaPurchaseService
				.getPurchaseMasterList(startDt, endDt, vendorId, storageId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),purchaseListInDepositModalData);
        return ResponseEntity.ok(res);
    }

    /**
     * 입고처리 : 가능한 발주 list get (입고처리 화면에서 발주 번호를 넣고 검색했을 때 나오는 리스트)
     */
    @GetMapping(path="/indicate/{purchaseNo}")
    public ResponseEntity getPurchaseListJpa(@PathVariable String purchaseNo){
        log.debug("get deposit plan purchase list");

        PurchaseSelectListResponseData purchaseSelectListResponseData = jpaPurchaseService.getDepositPlanList(purchaseNo);

        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), purchaseSelectListResponseData);
        return ResponseEntity.ok(res);
    }

    /**
	 * 입고처리 : 화면에서 입고수량 입력 후 저장을 눌렀을 때 타는 api (create)
	 * 
	 * @throws Exception
	 */
    @PostMapping(path="")
	public ResponseEntity createDepositListJpa(
			@RequestBody DepositListWithPurchaseInfoData depositListWithPurchaseInfoData) throws Exception {
        log.debug("입고처리 호출");
        List<String> messageList = new ArrayList<>();
        boolean flag = jpaDepositService.sequenceCreateDeposit(depositListWithPurchaseInfoData, messageList);
        ApiResponseMessage res = null;
        if(flag){
            res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), messageList.get(0));
        }
        else{
            res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), messageList);
        }
        return ResponseEntity.ok(res);
    }

    /**
     * 입고처리 : 화면에서 입고수량 수정 후 저장을 눌렀을 때 타는 api (update)
     */
    @PostMapping(path="/{depositNo}/update") // update
    public ResponseEntity updateDepositJpa(@PathVariable String depositNo, @RequestBody DepositInsertRequestData depositInsertRequestData){
        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
        jpaDepositService.sequenceUpdateDeposit(depositInsertRequestData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
        return ResponseEntity.ok(res);
    }

//    @PostMapping(path="") // create
//    public ResponseEntity saveDepositJpa(@RequestBody DepositInsertRequestData depositInsertRequestData){
//        String depositNo = jpaCommonService.getStrNumberId(StringFactory.getDUpperStr(), depositInsertRequestData.getDepositNo(), StringFactory.getStrSeqLsdpsm(), StringFactory.getIntEight());
//        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
//        depositNo = jpaDepositService.sequenceInsertDeposit(depositInsertRequestData);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
//        return ResponseEntity.ok(res);
//    }

    /**
     *  입고 - 입고내역 : depositNo로 검색하면 입고내역 리스트를 검색하는 api
     */
    @GetMapping(path="/items/{depositNo}")
    public ResponseEntity getDepositDetailPage(@PathVariable String depositNo){
        DepositSelectDetailResponseData depositSelectDetailResponseData = jpaDepositService.getDetail(depositNo);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectDetailResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     *  입고 - 입고내역 : 저장 (메모 쓰고 저장하기)
     */
    @PostMapping(path="/items/update/{depositNo}")
    public ResponseEntity updateDepositDetail(@PathVariable String depositNo, @RequestBody DepositSelectDetailRequestData depositSelectDetailRequestData){
        depositSelectDetailRequestData.setDepositNo(depositNo);
        depositSelectDetailRequestData = jpaDepositService.updateDetail(depositSelectDetailRequestData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectDetailRequestData);
        return ResponseEntity.ok(res);
    }

    /**
     * 입고 - 입고리스트 : 입고일자와 상품코드(빈칸이면 없이 검색)or상품명(like 검색)과 구매처 아이디를 받아 입고 리스트를 검색하는 api
     */
    @GetMapping(path = "/items")
    public ResponseEntity getDepositList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate startDt,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate endDt,
                                         @RequestParam @Nullable String assortId,
                                         @RequestParam @Nullable String assortNm,
			@RequestParam @Nullable String vendorId, @RequestParam @Nullable String storageId) {

		DepositSelectListResponseData depositSelectListResponseData = jpaDepositService.getList(vendorId, assortId,
				assortNm, startDt, endDt, storageId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectListResponseData);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path="/purchaseno/{purchaseNo}")
    public ResponseEntity getDepositListByPurchaseNo(@PathVariable String purchaseNo){
        DepositListWithPurchaseInfoData depositListWithPurchaseInfoData = jpaDepositService.getDepositListByPurchaseNo(purchaseNo);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositListWithPurchaseInfoData);
        return ResponseEntity.ok(res);
    }

//    @PostMapping(path="/updatedepositqty")
//    public ResponseEntity updateDepositQty(@RequestBody DepositListWithPurchaseInfoData depositListWithPurchaseInfoData){
//        DepositListWithPurchaseInfoData returnDep = jpaDepositService.updateDepositQty(depositListWithPurchaseInfoData);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), returnDep);
//        return ResponseEntity.ok(res);
//    }


//    @PostMapping(path="/depositlistjpa")
//    public ResponseEntity getDepositListJpa(@RequestParam String depositVendorId,
//                                            @RequestParam String assortId,
//                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date depositDt){
////                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDt){
////        HashMap<String, Object> param = new HashMap<>();
////        param.put("depositVendorId", depositVendorId);
////        param.put("assortId", assortId);
////        param.put("startDt", startDt);
////        param.put("endDt", endDt);
//        List<DepositSelectListResponseData> depositSelectListResponseDataList = jpaDepositService.getList(depositVendorId, assortId, depositDt);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectListResponseDataList);
//        return ResponseEntity.ok(res);
//    }

    /**
     * depositNo 채번 함수
     */
    private String getDepositNo(){
        String depositNo = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
        depositNo = Utilities.getStringNo('D',depositNo,9);
        return depositNo;
    }
}
