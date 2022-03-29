package io.spring.infrastructure.mapstruct;

import io.spring.model.purchase.response.PurchaseMasterListResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseMasterListResponseDataMapper {
    @Mapping(target = "siteOrderNo", expression = "java(p.getSiteOrderNo() == null? \"\" : p.getSiteOrderNo())")
    @Mapping(target = "channelOrderNo", expression = "java(p.getChannelOrderNo() == null? \"\" : p.getChannelOrderNo())")
    @Mapping(target = "brandId", expression = "java(p.getBrandId() == null? \"\" : p.getBrandId())")
    @Mapping(target = "vendorId", expression = "java(p.getVendorId() == null? \"\" : p.getVendorId())")
    @Mapping(target = "purchaseGb", expression = "java(p.getPurchaseGb() == null? \"\" : p.getPurchaseGb())")
    PurchaseMasterListResponseData nullToEmpty(PurchaseMasterListResponseData p);

    @Mapping(target = "purchaseNo", expression = "java(p.getPurchaseNo() == null? \"\" : p.getPurchaseNo())")
    @Mapping(target = "siteOrderNo", expression = "java(p.getSiteOrderNo() == null? \"\" : p.getSiteOrderNo())")
    @Mapping(target = "piNo", expression = "java(p.getPiNo() == null? \"\" : p.getPiNo())")
    @Mapping(target = "purchaseDt", expression = "java(p.getPurchaseDt() == null? \"\" : p.getPurchaseDt())")
    @Mapping(target = "purchaseGb", expression = "java(p.getPurchaseGb() == null? \"\" : p.getPurchaseGb())")
    @Mapping(target = "purchaseStatus", expression = "java(p.getPurchaseStatus() == null? \"\" : p.getPurchaseStatus())")
    PurchaseMasterListResponseData.Purchase nullToEmpty(PurchaseMasterListResponseData.Purchase p);
}
