package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.jparepos.common.JpaSequenceDataRepository;
import io.spring.model.deposit.request.DepositInsertRequestData;
import io.spring.model.deposit.response.DepositListWithPurchaseInfoData;
import io.spring.model.deposit.response.DepositSelectDetailResponseData;
import io.spring.model.deposit.response.DepositSelectListResponseData;
import io.spring.service.common.JpaCommonService;
import io.spring.service.deposit.JpaDepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value="/deposit")
@RequiredArgsConstructor
public class DepositController {
    private final JpaSequenceDataRepository jpaSequenceDataRepository;
    private final JpaDepositService jpaDepositService;
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
     * 입고처리 화면에서 입고수량 입력 후 저장을 눌렀을 때 타는 api (create)
     */
    @PostMapping(path="")
    public ResponseEntity createDepositListJpa(@RequestBody DepositListWithPurchaseInfoData depositListWithPurchaseInfoData){
        String depositNo = jpaDepositService.sequenceCreateDeposit(depositListWithPurchaseInfoData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
        return ResponseEntity.ok(res);
    }

    /**
     * 입고처리 화면에서 입고수량 수정 후 저장을 눌렀을 때 타는 api (update)
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

    @GetMapping(path="/depositdetailjpa")
    public ResponseEntity getDepositDetailPage(@RequestParam String depositNo){
        DepositSelectDetailResponseData depositSelectDetailResponseData = jpaDepositService.getDetail(depositNo);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectDetailResponseData);
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


    @PostMapping(path="/depositlistjpa")
    public ResponseEntity getDepositListJpa(@RequestParam String depositVendorId,@RequestParam String assortId,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDt,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDt){
        HashMap<String, Object> param = new HashMap<>();
        param.put("depositVendorId", depositVendorId);
        param.put("assortId", assortId);
        param.put("startDt", startDt);
        param.put("endDt", endDt);
        List<DepositSelectListResponseData> depositSelectListResponseDataList = jpaDepositService.getList(param);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), depositSelectListResponseDataList);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path="/init")
    public void initTabled(){
        jpaDepositService.init();
    }

    /**
     * depositNo 채번 함수
     */
    private String getDepositNo(){
        String depositNo = jpaSequenceDataRepository.nextVal(StringFactory.getStrSeqLsdpsm());
        depositNo = Utilities.getStringNo('D',depositNo,9);
        return depositNo;
    }
}
