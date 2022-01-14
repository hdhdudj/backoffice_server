package io.spring.enums;

import io.spring.model.common.EnumCommonInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Scm implements EnumCommonInterface {
    씨디에프브로스("1"),
    atempo("63"),
    mohd("67"),
    본사재고("74"),
    sevokorea("64");
    private final String fieldName;
}
