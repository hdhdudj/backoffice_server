package io.spring.infrastructure.mapstruct;

import io.spring.model.ship.response.ShipItemListData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipItemListDataMapper {
    @Mapping(target = "shipId", expression = "java(i.getShipId() == null? \"\" : i.getShipId())")
    @Mapping(target = "storageId", expression = "java(i.getStorageId() == null? \"\" : i.getStorageId())")
    @Mapping(target = "channelId", expression = "java(i.getChannelId() == null? \"\" : i.getChannelId())")
    @Mapping(target = "orderDt", expression = "java(i.getOrderDt() == null? \"\" : i.getOrderDt())")
    @Mapping(target = "shipIndicateDt", expression = "java(i.getShipIndicateDt() == null? \"\" : i.getShipIndicateDt())")
    @Mapping(target = "shipDt", expression = "java(i.getShipDt() == null? \"\" : i.getShipDt())")
    ShipItemListData nullToEmpty(ShipItemListData i);

    @Mapping(target = "shipId", expression = "java(i.getShipId() == null? \"\" : i.getShipId())")
    @Mapping(target = "shipSeq", expression = "java(i.getShipSeq() == null? \"\" : i.getShipSeq())")
    @Mapping(target = "shipKey", expression = "java(i.getShipKey() == null? \"\" : i.getShipKey())")
    @Mapping(target = "orderId", expression = "java(i.getOrderId() == null? \"\" : i.getOrderId())")
    @Mapping(target = "orderSeq", expression = "java(i.getOrderSeq() == null? \"\" : i.getOrderSeq())")
    @Mapping(target = "orderKey", expression = "java(i.getOrderKey() == null? \"\" : i.getOrderKey())")
    @Mapping(target = "assortGb", expression = "java(i.getAssortGb() == null? \"\" : i.getAssortGb())")
    @Mapping(target = "deliMethod", expression = "java(i.getDeliMethod() == null? \"\" : i.getDeliMethod())")
    @Mapping(target = "assortId", expression = "java(i.getAssortId() == null? \"\" : i.getAssortId())")
    @Mapping(target = "itemId", expression = "java(i.getItemId() == null? \"\" : i.getItemId())")
    @Mapping(target = "goodsKey", expression = "java(i.getGoodsKey() == null? \"\" : i.getGoodsKey())")
    @Mapping(target = "assortNm", expression = "java(i.getAssortNm() == null? \"\" : i.getAssortNm())")
    @Mapping(target = "optionNm1", expression = "java(i.getOptionNm1() == null? \"\" : i.getOptionNm1())")
    @Mapping(target = "optionNm2", expression = "java(i.getOptionNm2() == null? \"\" : i.getOptionNm2())")
    @Mapping(target = "optionNm3", expression = "java(i.getOptionNm3() == null? \"\" : i.getOptionNm3())")
    @Mapping(target = "qty", expression = "java(i.getQty() == null? \"\" : i.getQty())")
    @Mapping(target = "cost", expression = "java(i.getCost() == null? \"\" : i.getCost())")
    ShipItemListData.Ship nullToEmpty(ShipItemListData.Ship i);
}
