package io.spring.infrastructure.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.spring.model.goods.response.GoodsResponseData;

@Mapper(componentModel = "spring")
public interface GoodsResponseDataMapper {
	@Mapping(target = "assortId", expression = "java(g.getAssortId() == null? \"\" : g.getAssortId())")
	@Mapping(target = "origin", expression = "java(g.getOrigin() == null? \"\" : g.getOrigin())")
	@Mapping(target = "shortageYn", expression = "java(g.getShortageYn() == null? \"\" : g.getShortageYn())")
	@Mapping(target = "brandId", expression = "java(g.getBrandId() == null? \"\" : g.getBrandId())")
	@Mapping(target = "dispCategoryId", expression = "java(g.getDispCategoryId() == null? \"\" : g.getDispCategoryId())")
	@Mapping(target = "siteGb", expression = "java(g.getSiteGb() == null? \"\" : g.getSiteGb())")
	@Mapping(target = "asVendorId", expression = "java(g.getAsVendorId() == null? \"\" : g.getAsVendorId())")
	@Mapping(target = "manufactureNm", expression = "java(g.getManufactureNm() == null? \"\" : g.getManufactureNm())")
	@Mapping(target = "deliPrice", expression = "java(g.getDeliPrice() == null? \"\" : g.getDeliPrice())")
	@Mapping(target = "localPrice", expression = "java(g.getLocalPrice() == null? \"\" : g.getLocalPrice())")
	@Mapping(target = "localDeliFee", expression = "java(g.getLocalDeliFee() == null? \"\" : g.getLocalDeliFee())")
	@Mapping(target = "localSale", expression = "java(g.getLocalSale() == null? \"\" : g.getLocalSale())")
	@Mapping(target = "assortColor", expression = "java(g.getAssortColor() == null? \"\" : g.getAssortColor())")
	@Mapping(target = "sellStaDt", expression = "java(g.getSellStaDt() == null? \"\" : g.getSellStaDt())")
	@Mapping(target = "sellEndDt", expression = "java(g.getSellEndDt() == null? \"\" : g.getSellEndDt())")
	@Mapping(target = "mdRrp", expression = "java(g.getMdRrp() == null? \"\" : g.getMdRrp())")
	@Mapping(target = "mdTax", expression = "java(g.getMdTax() == null? \"\" : g.getMdTax())")
	@Mapping(target = "mdYear", expression = "java(g.getMdYear() == null? \"\" : g.getMdYear())")
	@Mapping(target = "mdMargin", expression = "java(g.getMdMargin() == null? \"\" : g.getMdMargin())")
	@Mapping(target = "mdVatrate", expression = "java(g.getMdVatrate() == null? \"\" : g.getMdVatrate())")
	@Mapping(target = "mdOfflinePrice", expression = "java(g.getMdOfflinePrice() == null? \"\" : g.getMdOfflinePrice())")
	@Mapping(target = "mdOnlinePrice", expression = "java(g.getMdOnlinePrice() == null? \"\" : g.getMdOnlinePrice())")
	@Mapping(target = "mdGoodsVatrate", expression = "java(g.getMdGoodsVatrate() == null? \"\" : g.getMdGoodsVatrate())")
	@Mapping(target = "buyWhere", expression = "java(g.getBuyWhere() == null? \"\" : g.getBuyWhere())")
	@Mapping(target = "buyTax", expression = "java(g.getBuyTax() == null? \"\" : g.getBuyTax())")
	@Mapping(target = "buySupplyDiscount", expression = "java(g.getBuySupplyDiscount() == null? \"\" : g.getBuySupplyDiscount())")
	@Mapping(target = "buyRrpIncrement", expression = "java(g.getBuyRrpIncrement() == null? \"\" : g.getBuyRrpIncrement())")
	@Mapping(target = "buyExchangeRate", expression = "java(g.getBuyExchangeRate() == null? \"\" : g.getBuyExchangeRate())")
	@Mapping(target = "sizeType", expression = "java(g.getSizeType() == null? \"\" : g.getSizeType())")
	@Mapping(target = "mdDiscountRate", expression = "java(g.getMdDiscountRate() == null? \"\" : g.getMdDiscountRate())")
	@Mapping(target = "vendorId", expression = "java(g.getVendorId() == null? \"\" : g.getVendorId())")
	@Mapping(target = "optionUseYn", expression = "java(g.getOptionUseYn() == null? \"\" : g.getOptionUseYn())")
	@Mapping(target = "brandNm", expression = "java(g.getBrandNm() == null? \"\" : g.getBrandNm())")
	@Mapping(target = "vendorNm", expression = "java(g.getVendorNm() == null? \"\" : g.getVendorNm())")
	@Mapping(target = "asWidth", expression = "java(g.getAsWidth() == null? \"\" : g.getAsWidth())")
	@Mapping(target = "asLength", expression = "java(g.getAsLength() == null? \"\" : g.getAsLength())")
	@Mapping(target = "asHeight", expression = "java(g.getAsHeight() == null? \"\" : g.getAsHeight())")
	@Mapping(target = "weight", expression = "java(g.getWeight() == null? \"\" : g.getWeight())")
	GoodsResponseData nullToEmpty(GoodsResponseData g);

	@Mapping(target = "itemId", expression = "java(i.getItemId() == null? \"\" : i.getItemId())")
	@Mapping(target = "seq1", expression = "java(i.getSeq1() == null? \"\" : i.getSeq1())")
	@Mapping(target = "seq2", expression = "java(i.getSeq2() == null? \"\" : i.getSeq2())")
	@Mapping(target = "seq3", expression = "java(i.getSeq3() == null? \"\" : i.getSeq3())")
	@Mapping(target = "value1", expression = "java(i.getValue1() == null? \"\" : i.getValue1())")
	@Mapping(target = "value2", expression = "java(i.getValue2() == null? \"\" : i.getValue2())")
	@Mapping(target = "value3", expression = "java(i.getValue3() == null? \"\" : i.getValue3())")
	@Mapping(target = "addPrice", expression = "java(i.getAddPrice() == null? \"\" : i.getAddPrice())")
	@Mapping(target = "shortageYn", expression = "java(i.getShortageYn() == null? \"\" : i.getShortageYn())")
	@Mapping(target = "status1", expression = "java(i.getStatus1() == null? \"\" : i.getStatus1())")
	@Mapping(target = "status2", expression = "java(i.getStatus2() == null? \"\" : i.getStatus2())")
	@Mapping(target = "status3", expression = "java(i.getStatus3() == null? \"\" : i.getStatus3())")
	GoodsResponseData.Items nullToEmpty(GoodsResponseData.Items i);
}
