package io.spring.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.spring.model.move.request.GoodsMoveSaveData;
import io.spring.model.move.request.MoveListSaveData;
import io.spring.model.move.request.OrderMoveSaveData;
import io.spring.model.move.response.GoodsModalListResponseData;
import io.spring.model.move.response.MoveCompletedLIstReponseData;
import io.spring.model.move.response.MoveIndicateDetailResponseData;
import io.spring.model.move.response.MoveIndicateListResponseData;
import io.spring.model.move.response.MoveListResponseData;
import io.spring.model.move.response.MovedDetailResponseData;
import io.spring.model.move.response.OrderMoveListResponseData;
import io.spring.service.move.JpaMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/move")
@RequiredArgsConstructor
@Slf4j
public class MoveController {
    private final JpaMoveService jpaMoveService;

    /**
     * 주문이동지시 화면에서 검색시 가져오는 주문 list를 return
     */
    @GetMapping(path="/items/indicate/order")
    public ResponseEntity getOrderMoveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Nullable LocalDate endDt,
                                           @RequestParam @Nullable String storageId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String itemId,
                                           @RequestParam @Nullable String deliMethod){
        Map<String, Object> map = new HashMap<>();
        map.put("startDt", startDt);
        map.put("endDt", endDt);
        map.put("storageId", storageId);
        map.put("assortId", assortId);
        map.put("assortNm", assortNm);
        map.put("itemId", itemId);
        map.put("deliMethod", deliMethod);

        List<OrderMoveListResponseData> orderMoveListResponseData = jpaMoveService.getOrderMoveList(map);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), orderMoveListResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 주문이동지시 저장
     */
    @PostMapping(path="/indicate/order")
    public ResponseEntity saveOrderMove(@RequestBody OrderMoveSaveData orderMoveSaveData){
        List<String> shipIdList = jpaMoveService.saveOrderMove(orderMoveSaveData);
//        depositInsertRequestData.setDepositNo(depositNo); // deposit no 채번
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 상품선택창 검색 결과 return 함수
     * 상품이동지시 화면에서 storageId, purchaseVendorId, assortId, assortNm로 상품(Ititmc 기준)을 가져와 목록을 return
     */
    @GetMapping(path="/items/goods")
    public ResponseEntity getGoodsList(@RequestParam @Nullable String storageId,
                                           @RequestParam @Nullable String vendorId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm){
        GoodsModalListResponseData goodsMoveListDataListResponse = jpaMoveService.getGoodsList(storageId, vendorId, assortId, assortNm);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), goodsMoveListDataListResponse);
        return ResponseEntity.ok(res);
    }

    /**
     * 상품선택창 검색 결과에서 상품들을 선택한 후 확인을 누르면 상품이동지시 화면에 선택된 물건 정보 리스트를 반환하는 함수
     * 상품이동지시 화면에서 선택한 물건들의 json을 받아 리스트를 return
     */
    @GetMapping(path="/items/indicate/goods")
    public ResponseEntity getGoodsMoveList(@RequestParam @Nullable String storageId,
                                           @RequestParam @Nullable String vendorId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm){
        GoodsModalListResponseData goodsMoveListDataListResponse = jpaMoveService.getGoodsList(storageId, vendorId, assortId, assortNm);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), goodsMoveListDataListResponse);
        return ResponseEntity.ok(res);
    }

//    /**
//     * 상품이동지시 화면에서 검색시 가져오는 상품 list를 return
//     */
//    @GetMapping(path="/items/indicate/goods")
//    public ResponseEntity getGoodsMoveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shipIndDt,
//                                           @RequestParam @Nullable String storeCd,
//                                           @RequestParam @Nullable String oStoreCd,
//                                           @RequestParam @Nullable String deliMethod){
//        List<GoodsMoveListResponseData> goodsMoveListDataListResponse = jpaMoveService.getGoodsMoveList(shipIndDt, storeCd, oStoreCd, deliMethod);
//        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), goodsMoveListDataListResponse);
//        return ResponseEntity.ok(res);
//    }

    /**
     * 상품이동지시 저장
     */
    @PostMapping(path="/indicate/goods")
    public ResponseEntity saveGoodsMove(@RequestBody GoodsMoveSaveData goodsMoveSaveData){
        System.out.println("========== : " + goodsMoveSaveData.getOStorageId());
        List<String> shipIdList = jpaMoveService.saveGoodsMove(goodsMoveSaveData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 이동지시리스트(상품, 주문)를 반환하는 api
     * @param startDt 이동지시일자 min
     * @param endDt 이동지시일자 max
     * @param storageId 이동창고
     * @param oStorageId 출고창고
     * @param assortId 품목코드
     * @param assortNm 품목이름
     * @return 상품이동지시, 주문이동지시 리스트 DTO
     */
    @GetMapping(path = "/items/indicate")
    public ResponseEntity getMoveIndicateList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                              @RequestParam @Nullable String storageId,
                                              @RequestParam @Nullable String oStorageId,
                                              @RequestParam @Nullable String assortId,
                                              @RequestParam @Nullable String assortNm
                                              ){



        MoveIndicateListResponseData moveIndicateListResponseData = jpaMoveService.getMoveIndicateList(startDt,endDt,storageId,oStorageId,assortId,assortNm);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),moveIndicateListResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 이동지시내역
     * @param shipId 출고번호
     * @return 이동지시내역 리스트를 가진 DTO
     */
    @GetMapping(path = "/item/{shipId}")
    public ResponseEntity getMoveIndicateDetail(@PathVariable String shipId){
        MoveIndicateDetailResponseData moveIndicateDetailResponseData = jpaMoveService.getMoveIndicateDetail(shipId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),moveIndicateDetailResponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 이동처리 조회
     */
    @GetMapping(path = "/move/items")
    public ResponseEntity getMoveList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                      @RequestParam @Nullable String shipId,
                                      @RequestParam @Nullable String assortId,
                                      @RequestParam @Nullable String assortNm,
                                      @RequestParam @Nullable String storageId,
                                      @RequestParam @Nullable String deliMethod){
        MoveListResponseData moveListResponseData = jpaMoveService.getMoveList(startDt, endDt, shipId, assortId, assortNm, storageId, deliMethod);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), moveListResponseData);
        return ResponseEntity.ok(res);
    }
    
    /**
     * 이동처리 저장
     */
    @PostMapping(path = "/move")
    public ResponseEntity changeShipStatus(@RequestBody MoveListSaveData moveListSaveData){
        List<String> shipIdList = jpaMoveService.changeShipStatus(moveListSaveData);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), shipIdList);
        return ResponseEntity.ok(res);
    }

    /**
     * 이동리스트 조회
     * @return 이동완료리스트 DTO 반환
     */
    @GetMapping(path = "/items")
    public ResponseEntity getMovedList(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDt,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDt,
                                           @RequestParam @Nullable String shipId,
                                           @RequestParam @Nullable String assortId,
                                           @RequestParam @Nullable String assortNm,
                                           @RequestParam @Nullable String storageId){
        MoveCompletedLIstReponseData moveCompletedLIstReponseData = jpaMoveService.getMovedList(startDt, endDt, shipId, assortId, assortNm, storageId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), moveCompletedLIstReponseData);
        return ResponseEntity.ok(res);
    }

    /**
     * 이동내역 조회
     * @return 이동내역 DTO 반환
     */
    @GetMapping(path = "/moved/item/{shipId}")
    public ResponseEntity getMovedDetail(@PathVariable String shipId){
        MovedDetailResponseData movedDetailResponseData = jpaMoveService.getMovedDetail(shipId);
        ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(), movedDetailResponseData);
        return ResponseEntity.ok(res);
    }
}
