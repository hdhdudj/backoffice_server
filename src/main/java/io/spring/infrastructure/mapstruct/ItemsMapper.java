package io.spring.infrastructure.mapstruct;

import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemsMapper {

    @Mapping(target = "purchaseId", expression = "java(items.getPurchaseId() == null? \"\" : items.getPurchaseId())")
    @Mapping(target = "orderId", expression = "java(items.getOrderId() == null? \"\" : items.getOrderId())")
    @Mapping(target = "orderSeq", expression = "java(items.getOrderSeq() == null? \"\" : items.getOrderSeq())")
    @Mapping(target = "assortNm", expression = "java(items.getAssortNm() == null? \"\" : items.getAssortNm())")
    @Mapping(target = "assortId", expression = "java(items.getAssortId() == null? \"\" : items.getAssortId())")
    @Mapping(target = "itemId", expression = "java(items.getItemId() == null? \"\" : items.getItemId())")
    @Mapping(target = "itemKey", expression = "java(items.getItemKey() == null? \"\" : items.getItemKey())")
    @Mapping(target = "optionNm1", expression = "java(items.getOptionNm1() == null? \"\" : items.getOptionNm1())")
    @Mapping(target = "optionNm2", expression = "java(items.getOptionNm2() == null? \"\" : items.getOptionNm2())")
    @Mapping(target = "optionNm3", expression = "java(items.getOptionNm3() == null? \"\" : items.getOptionNm3())")
    @Mapping(target = "deliMethod", expression = "java(items.getDeliMethod() == null? \"\" : items.getDeliMethod())")
    @Mapping(target = "purchaseSeq", expression = "java(items.getPurchaseSeq() == null? \"\" : items.getPurchaseSeq())")
    @Mapping(target = "purchaseStatus", expression = "java(items.getPurchaseStatus() == null? \"\" : items.getPurchaseStatus())")
    @Mapping(target = "imagePath", expression = "java(items.getImagePath() == null? \"\" : items.getImagePath())")
    @Mapping(target = "modelNo", expression = "java(items.getModelNo() == null? \"\" : items.getModelNo())")
    @Mapping(target = "origin", expression = "java(items.getOrigin() == null? \"\" : items.getOrigin())")
    @Mapping(target = "custCategory", expression = "java(items.getCustCategory() == null? \"\" : items.getCustCategory())")
    @Mapping(target = "material", expression = "java(items.getMaterial() == null? \"\" : items.getMaterial())")
    @Mapping(target = "imgServerUrl", expression = "java(items.getImgServerUrl() == null? \"\" : items.getImgServerUrl())")
    @Mapping(target = "custNm", expression = "java(items.getCustNm() == null? \"\" : items.getCustNm())")
    @Mapping(target = "channelOrderNo", expression = "java(items.getChannelOrderNo() == null? \"\" : items.getChannelOrderNo())")
    @Mapping(target = "mdRrp", expression = "java(items.getMdRrp() == null? 0f : items.getMdRrp())")
    @Mapping(target = "buySupplyDiscount", expression = "java(items.getBuySupplyDiscount() == null? 0f : items.getBuySupplyDiscount())")
    @Mapping(target = "purchaseQty", expression = "java(items.getPurchaseQty() == null? 0l : items.getPurchaseQty())")
    @Mapping(target = "purchaseUnitAmt", expression = "java(items.getPurchaseUnitAmt() == null? 0f : items.getPurchaseUnitAmt())")
    PurchaseSelectDetailResponseData.Items nullToEmpty(PurchaseSelectDetailResponseData.Items items);
}
