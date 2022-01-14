package io.spring.enums;

import io.spring.model.common.EnumCommonInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeliveryMethod implements EnumCommonInterface {
    delivery("001"),
    air("002"),
    ship("003"),
    quick("004"),
    기타("005"),
    cargo("006");
    private final String fieldName;
}
