package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.OrderMoveListData;
import io.spring.service.move.JpaMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/move")
@RequiredArgsConstructor
@Slf4j
public class MoveController {
    private final JpaMoveService jpaMoveService;

    /**
     * 주문이동지시 화면에서 검색시 가져오는 주문 list를 return
     */
    @GetMapping(path="/list/order")
    public ResponseEntity getOrderMoveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Nullable Date startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Nullable Date endDt,
                                           @RequestParam @Nullable String storageId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String itemId,
                                           @RequestParam @Nullable String deliMethod){
        Map<String, Object> map = new HashMap<>();
        map.put("startDt", startDt);
        map.put("endDt", endDt);
        map.put("storageId", storageId);
        map.put("assortId", assortId);
        map.put("itemId", itemId);
        map.put("deliMethod", deliMethod);

        List<OrderMoveListData> orderMoveListData = jpaMoveService.getOrderMoveList(map);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), orderMoveListData);
        return ResponseEntity.ok(res);
    }

    /**
     * 상품이동지시 화면에서 검색시 가져오는 상품 list를 return
     */
//    @GetMapping(path="/list/goods")
//    public ResponseEntity getGoodsMoveList(@RequestBody DepositInsertRequestData depositInsertRequestData){
//        String depositNo = jpaMoveService.getGoodsMoveList(StringFactory.getDUpperStr(), depositInsertRequestData.getDepositNo(), StringFactory.getStrDepositNo(), StringFactory.getIntEight());
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
//        return ResponseEntity.ok(res);
//    }

    /**
     * 주문이동지시 저장
     */
    @PostMapping(path="/save/order")
    public ResponseEntity saveOrderMove(@RequestBody List<OrderMoveSaveData> orderMoveSaveDataList){
        List<String> shipIdList = jpaMoveService.saveOrderMove(orderMoveSaveDataList);
//        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 상품이동지시 저장
     */
//    @PostMapping(path="/save/goods")
//    public ResponseEntity saveGoodsMove(@RequestBody GoodsMoveSaveData goodsMoveSaveData){
//        String depositNo = jpaMoveService.saveGoodsMove(goodsMoveSaveData);
//        depositNo = jpaDepositService.sequenceInsertDeposit(depositInsertRequestData);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), depositNo);
//        return ResponseEntity.ok(res);
//    }
}
