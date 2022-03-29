package io.spring.infrastructure.mapstruct;

import io.spring.model.move.response.MoveIndicateListResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MoveIndicateListResponseDataMapper {
    @Mapping(target = "shipId", expression = "java(m.getShipId() == null? \"\" : m.getShipId())")
    @Mapping(target = "shipSeq", expression = "java(m.getShipSeq() == null? \"\" : m.getShipSeq())")
    @Mapping(target = "shipKey", expression = "java(m.getShipKey() == null? \"\" : m.getShipKey())")
    @Mapping(target = "moveIndGb", expression = "java(m.getMoveIndGb() == null? \"\" : m.getMoveIndGb())")
    @Mapping(target = "deliMethod", expression = "java(m.getDeliMethod() == null? \"\" : m.getDeliMethod())")
    @Mapping(target = "storageId", expression = "java(m.getStorageId() == null? \"\" : m.getStorageId())")
    @Mapping(target = "OStorageId", expression = "java(m.getOStorageId() == null? \"\" : m.getOStorageId())")
    @Mapping(target = "moveIndDt", expression = "java(m.getMoveIndDt() == null? \"\" : m.getMoveIndDt())")
    @Mapping(target = "orderId", expression = "java(m.getOrderId() == null? \"\" : m.getOrderId())")
    @Mapping(target = "orderSeq", expression = "java(m.getOrderSeq() == null? \"\" : m.getOrderSeq())")
    @Mapping(target = "orderKey", expression = "java(m.getOrderKey() == null? \"\" : m.getOrderKey())")
    @Mapping(target = "assortId", expression = "java(m.getAssortId() == null? \"\" : m.getAssortId())")
    @Mapping(target = "itemId", expression = "java(m.getItemId() == null? \"\" : m.getItemId())")
    @Mapping(target = "goodsKey", expression = "java(m.getGoodsKey() == null? \"\" : m.getGoodsKey())")
    @Mapping(target = "assortNm", expression = "java(m.getAssortNm() == null? \"\" : m.getAssortNm())")
    @Mapping(target = "optionNm1", expression = "java(m.getOptionNm1() == null? \"\" : m.getOptionNm1())")
    @Mapping(target = "optionNm2", expression = "java(m.getOptionNm2() == null? \"\" : m.getOptionNm2())")
    @Mapping(target = "optionNm3", expression = "java(m.getOptionNm3() == null? \"\" : m.getOptionNm3())")
    @Mapping(target = "qty", expression = "java(m.getQty() == null? \"\" : m.getQty())")
    @Mapping(target = "cost", expression = "java(m.getCost() == null? \"\" : m.getCost())")
    MoveIndicateListResponseData.Move to(MoveIndicateListResponseData.Move m);

    @Mapping(target = "storageId", expression = "java(m.getStorageId() == null? \"\" : m.getStorageId())")
    @Mapping(target = "OStorageId", expression = "java(m.getOStorageId() == null? \"\" : m.getOStorageId())")
    @Mapping(target = "assortId", expression = "java(m.getAssortId() == null? \"\" : m.getAssortId())")
    @Mapping(target = "assortNm", expression = "java(m.getAssortNm() == null? \"\" : m.getAssortNm())")
    MoveIndicateListResponseData to(MoveIndicateListResponseData m);
}
