package io.spring.infrastructure.mapstruct;

import io.spring.model.ship.response.ShipListDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipListDataResponseMapper {
        @Mapping(target = "shipId", expression = "java(p.getShipId() == null? \"\" : p.getShipId())")
        @Mapping(target = "assortId", expression = "java(p.getAssortId() == null? \"\" : p.getAssortId())")
        @Mapping(target = "assortNm", expression = "java(p.getAssortNm() == null? \"\" : p.getAssortNm())")
        @Mapping(target = "vendorId", expression = "java(p.getVendorId() == null? \"\" : p.getVendorId())")
        ShipListDataResponse nullToEmpty(ShipListDataResponse p);

        @Mapping(target = "orderId", expression = "java(p.getOrderId() == null? \"\" : p.getOrderId())")
        @Mapping(target = "orderSeq", expression = "java(p.getOrderSeq() == null? \"\" : p.getOrderSeq())")
        @Mapping(target = "orderKey", expression = "java(p.getOrderKey() == null? \"\" : p.getOrderKey())")
        @Mapping(target = "orderNm", expression = "java(p.getOrderNm() == null? \"\" : p.getOrderNm())")
        @Mapping(target = "receiverNm", expression = "java(p.getReceiverNm() == null? \"\" : p.getReceiverNm())")
        @Mapping(target = "receiverTel", expression = "java(p.getReceiverTel() == null? \"\" : p.getReceiverTel())")
        @Mapping(target = "receiverHp", expression = "java(p.getReceiverHp() == null? \"\" : p.getReceiverHp())")
        @Mapping(target = "receiverZonecode", expression = "java(p.getReceiverZonecode() == null? \"\" : p.getReceiverZonecode())")
        @Mapping(target = "receiverAddr1", expression = "java(p.getReceiverAddr1() == null? \"\" : p.getReceiverAddr1())")
        @Mapping(target = "receiverAddr2", expression = "java(p.getReceiverAddr2() == null? \"\" : p.getReceiverAddr2())")
        @Mapping(target = "orderMemo", expression = "java(p.getOrderMemo() == null? \"\" : p.getOrderMemo())")
        @Mapping(target = "assortNm", expression = "java(p.getAssortNm() == null? \"\" : p.getAssortNm())")
        @Mapping(target = "optionNm1", expression = "java(p.getOptionNm1() == null? \"\" : p.getOptionNm1())")
        @Mapping(target = "optionNm2", expression = "java(p.getOptionNm2() == null? \"\" : p.getOptionNm2())")
        @Mapping(target = "optionNm3", expression = "java(p.getOptionNm3() == null? \"\" : p.getOptionNm3())")
        @Mapping(target = "imagePath", expression = "java(p.getImagePath() == null? \"\" : p.getImagePath())")
        @Mapping(target = "assortId", expression = "java(p.getAssortId() == null? \"\" : p.getAssortId())")
        @Mapping(target = "itemId", expression = "java(p.getItemId() == null? \"\" : p.getItemId())")
        @Mapping(target = "itemKey", expression = "java(p.getItemKey() == null? \"\" : p.getItemKey())")
        ShipListDataResponse.Ship nullToEmpty(ShipListDataResponse.Ship p);
}
