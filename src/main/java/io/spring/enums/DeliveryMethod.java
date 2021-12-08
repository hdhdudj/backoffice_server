package io.spring.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeliveryMethod {
    delivery("001"),
    air("002"),
    ship("003"),
    quick("004"),
    기타("005"),
    cargo("006");
    private final String fieldName;
}
