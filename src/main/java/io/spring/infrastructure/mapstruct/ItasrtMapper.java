package io.spring.infrastructure.mapstruct;

import io.spring.model.goods.entity.Itasrt;
import io.spring.model.goods.request.GoodsInsertRequestData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItasrtMapper {
    @Mapping(target = "assortId", expression = "java(g.getAssortId() == null? \"\" : g.getAssortId())")
    Itasrt to(GoodsInsertRequestData g);
}
