package io.spring.infrastructure.mapstruct;

import io.spring.model.purchase.entity.Lspchm;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LspchmMapper {

    @Mapping(target = "updId", expression = "java(p.getUserId() == null? \"\" : p.getUserId())")
    @Mapping(target = "storeCd", expression = "java(p.getStorageId() == null? \"\" : p.getStorageId())")
    Lspchm to(PurchaseInsertRequestData p);
}
