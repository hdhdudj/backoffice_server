package io.spring.infrastructure.mapstruct;

import io.spring.model.move.response.MoveCompletedLIstReponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoveCompletedListResponseDataMapper {
    @Mapping(target = "startDt", expression = "java(m.getStartDt() == null? \"\" : m.getStartDt())")
    @Mapping(target = "endDt", expression = "java(m.getEndDt() == null? \"\" : m.getEndDt())")
    @Mapping(target = "shipId", expression = "java(m.getShipId() == null? \"\" : m.getShipId())")
    @Mapping(target = "assortId", expression = "java(m.getAssortId() == null? \"\" : m.getAssortId())")
    @Mapping(target = "assortNm", expression = "java(m.getAssortNm() == null? \"\" : m.getAssortNm())")
    @Mapping(target = "storageId", expression = "java(m.getStorageId() == null? \"\" : m.getStorageId())")
    MoveCompletedLIstReponseData nullToEmpty(MoveCompletedLIstReponseData m);

    @Mapping(target = "shipDt", expression = "java(m.getShipDt() == null? \"\" : m.getShipDt())")
    @Mapping(target = "shipIndDt", expression = "java(m.getShipIndDt() == null? \"\" : m.getShipIndDt())")
    @Mapping(target = "shipId", expression = "java(m.getShipId() == null? \"\" : m.getShipId())")
    @Mapping(target = "shipSeq", expression = "java(m.getShipSeq() == null? \"\" : m.getShipSeq())")
    @Mapping(target = "shipKey", expression = "java(m.getShipKey() == null? \"\" : m.getShipKey())")
    @Mapping(target = "trackNo", expression = "java(m.getTrackNo() == null? \"\" : m.getTrackNo())")
    @Mapping(target = "storageId", expression = "java(m.getStorageId() == null? \"\" : m.getStorageId())")
    @Mapping(target = "OStorageId", expression = "java(m.getOStorageId() == null? \"\" : m.getOStorageId())")
    @Mapping(target = "shipGb", expression = "java(m.getShipGb() == null? \"\" : m.getShipGb())")
    @Mapping(target = "deliMethod", expression = "java(m.getDeliMethod() == null? \"\" : m.getDeliMethod())")
    @Mapping(target = "assortId", expression = "java(m.getAssortId() == null? \"\" : m.getAssortId())")
    @Mapping(target = "itemId", expression = "java(m.getItemId() == null? \"\" : m.getItemId())")
    @Mapping(target = "goodsKey", expression = "java(m.getGoodsKey() == null? \"\" : m.getGoodsKey())")
    @Mapping(target = "assortNm", expression = "java(m.getAssortNm() == null? \"\" : m.getAssortNm())")
    @Mapping(target = "optionNm1", expression = "java(m.getOptionNm1() == null? \"\" : m.getOptionNm1())")
    @Mapping(target = "optionNm2", expression = "java(m.getOptionNm2() == null? \"\" : m.getOptionNm2())")
    @Mapping(target = "optionNm3", expression = "java(m.getOptionNm3() == null? \"\" : m.getOptionNm3())")
    @Mapping(target = "qty", expression = "java(m.getQty() == null? \"\" : m.getQty())")
    @Mapping(target = "orderId", expression = "java(m.getOrderId() == null? \"\" : m.getOrderId())")
    @Mapping(target = "orderSeq", expression = "java(m.getOrderSeq() == null? \"\" : m.getOrderSeq())")
    @Mapping(target = "orderKey", expression = "java(m.getOrderKey() == null? \"\" : m.getOrderKey())")
    @Mapping(target = "shipmentDt", expression = "java(m.getShipmentDt() == null? \"\" : m.getShipmentDt())")
    @Mapping(target = "blNo", expression = "java(m.getBlNo() == null? \"\" : m.getBlNo())")
    @Mapping(target = "movementKd", expression = "java(m.getMovementKd() == null? \"\" : m.getMovementKd())")
    @Mapping(target = "estiArrvTm", expression = "java(m.getEstiArrvTm() == null? \"\" : m.getEstiArrvTm())")
    @Mapping(target = "containerKd", expression = "java(m.getContainerKd() == null? \"\" : m.getContainerKd())")
    @Mapping(target = "containerQty", expression = "java(m.getContainerQty() == null? \"\" : m.getContainerQty())")
    MoveCompletedLIstReponseData.Move nullToEmpty(MoveCompletedLIstReponseData.Move m);
}
