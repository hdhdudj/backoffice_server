package io.spring.infrastructure.mapstruct;

import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseSelectDetailResponseDataMapper {
    @Mapping(target = "purchaseId", expression = "java(p.getPurchaseId() == null? \"\" : p.getPurchaseId())")
    @Mapping(target = "purchaseDt", expression = "java(p.getPurchaseDt() == null? \"\" : p.getPurchaseDt())")
    @Mapping(target = "ownerId", expression = "java(p.getOwnerId() == null? \"\" : p.getOwnerId())")
    @Mapping(target = "purchaseRemark", expression = "java(p.getPurchaseRemark() == null? \"\" : p.getPurchaseRemark())")
    @Mapping(target = "storageId", expression = "java(p.getStorageId() == null? \"\" : p.getStorageId())")
    @Mapping(target = "terms", expression = "java(p.getTerms() == null? \"\" : p.getTerms())")
    @Mapping(target = "dealtypeCd", expression = "java(p.getDealtypeCd() == null? \"\" : p.getDealtypeCd())")
    @Mapping(target = "delivery", expression = "java(p.getDelivery() == null? \"\" : p.getDelivery())")
    @Mapping(target = "payment", expression = "java(p.getPayment() == null? \"\" : p.getPayment())")
    @Mapping(target = "carrier", expression = "java(p.getCarrier() == null? \"\" : p.getCarrier())")
    @Mapping(target = "siteOrderNo", expression = "java(p.getSiteOrderNo() == null? \"\" : p.getSiteOrderNo())")
    @Mapping(target = "purchaseStatus", expression = "java(p.getPurchaseStatus() == null? \"\" : p.getPurchaseStatus())")
    PurchaseSelectDetailResponseData nullToEmpty(PurchaseSelectDetailResponseData p);
}
