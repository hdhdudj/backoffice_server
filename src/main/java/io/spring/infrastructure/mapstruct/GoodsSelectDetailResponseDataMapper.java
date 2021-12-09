package io.spring.infrastructure.mapstruct;

import io.spring.model.goods.response.GoodsSelectDetailResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsSelectDetailResponseDataMapper {
    @Mapping(target = "assortId", expression = "java(g.getAssortId() == null? \"\" : g.getAssortId())")
    @Mapping(target = "origin", expression = "java(g.getOrigin() == null? \"\" : g.getOrigin())")
    @Mapping(target = "shortageYn", expression = "java(g.getShortageYn() == null? \"\" : g.getShortageYn())")
    @Mapping(target = "brandId", expression = "java(g.getBrandId() == null? \"\" : g.getBrandId())")
    @Mapping(target = "dispCategoryId", expression = "java(g.getDispCategoryId() == null? \"\" : g.getDispCategoryId())")
    @Mapping(target = "siteGb", expression = "java(g.getSiteGb() == null? \"\" : g.getSiteGb())")
    @Mapping(target = "asVendorId", expression = "java(g.getAsVendorId() == null? \"\" : g.getAsVendorId())")
    @Mapping(target = "manufactureNm", expression = "java(g.getManufactureNm() == null? \"\" : g.getManufactureNm())")
    @Mapping(target = "deliPrice", expression = "java(g.getDeliPrice() == null? 0f : g.getDeliPrice())")
    @Mapping(target = "localPrice", expression = "java(g.getLocalPrice() == null? 0f : g.getLocalPrice())")
    @Mapping(target = "localDeliFee", expression = "java(g.getLocalDeliFee() == null? 0f : g.getLocalDeliFee())")
    @Mapping(target = "localSale", expression = "java(g.getLocalSale() == null? 0f : g.getLocalSale())")
    @Mapping(target = "assortColor", expression = "java(g.getAssortColor() == null? \"\" : g.getAssortColor())")
    @Mapping(target = "sellStaDt", expression = "java(g.getSellStaDt() == null? \"\" : g.getSellStaDt())")
    @Mapping(target = "sellEndDt", expression = "java(g.getSellEndDt() == null? \"\" : g.getSellEndDt())")
    @Mapping(target = "mdRrp", expression = "java(g.getMdRrp() == null? 0f : g.getMdRrp())")
    @Mapping(target = "mdTax", expression = "java(g.getMdTax() == null? \"\" : g.getMdTax())")
    @Mapping(target = "mdYear", expression = "java(g.getMdYear() == null? \"\" : g.getMdYear())")
    @Mapping(target = "mdMargin", expression = "java(g.getMdMargin() == null? 0f : g.getMdMargin())")
    @Mapping(target = "mdVatrate", expression = "java(g.getMdVatrate() == null? 0f : g.getMdVatrate())")
    @Mapping(target = "mdOfflinePrice", expression = "java(g.getMdOfflinePrice() == null? 0f : g.getMdOfflinePrice())")
    @Mapping(target = "mdOnlinePrice", expression = "java(g.getMdOnlinePrice() == null? 0f : g.getMdOnlinePrice())")
    @Mapping(target = "mdGoodsVatrate", expression = "java(g.getMdGoodsVatrate() == null? 0f : g.getMdGoodsVatrate())")
    @Mapping(target = "buyWhere", expression = "java(g.getBuyWhere() == null? \"\" : g.getBuyWhere())")
    @Mapping(target = "buyTax", expression = "java(g.getBuyTax() == null? \"\" : g.getBuyTax())")
    @Mapping(target = "buySupplyDiscount", expression = "java(g.getBuySupplyDiscount() == null? 0f : g.getBuySupplyDiscount())")
    @Mapping(target = "buyRrpIncrement", expression = "java(g.getBuyRrpIncrement() == null? 0f : g.getBuyRrpIncrement())")
    @Mapping(target = "buyExchangeRate", expression = "java(g.getBuyExchangeRate() == null? 0f : g.getBuyExchangeRate())")
    @Mapping(target = "sizeType", expression = "java(g.getSizeType() == null? \"\" : g.getSizeType())")
    @Mapping(target = "mdDiscountRate", expression = "java(g.getMdDiscountRate() == null? 0f : g.getMdDiscountRate())")
    @Mapping(target = "vendorId", expression = "java(g.getVendorId() == null? \"\" : g.getVendorId())")
    @Mapping(target = "optionUseYn", expression = "java(g.getOptionUseYn() == null? \"\" : g.getOptionUseYn())")
    @Mapping(target = "brandNm", expression = "java(g.getBrandNm() == null? \"\" : g.getBrandNm())")
    @Mapping(target = "vendorNm", expression = "java(g.getVendorNm() == null? \"\" : g.getVendorNm())")
    GoodsSelectDetailResponseData nullToEmpty(GoodsSelectDetailResponseData g);
}
