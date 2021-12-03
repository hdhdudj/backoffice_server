package io.spring.infrastructure.mapstruct;

import io.spring.model.purchase.response.PurchaseSelectDetailResponseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseSelectDetailResponseDataMapper {
//    @Mapping(target = "name", expression = "java(source.getName.isEmpty() ? null : source.getName)")
//    PurchaseSelectDetailResponseData nullToEmpty();
}
