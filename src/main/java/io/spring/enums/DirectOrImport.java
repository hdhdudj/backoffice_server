package io.spring.enums;

import io.spring.model.common.EnumCommonInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DirectOrImport implements EnumCommonInterface {
    direct("01"), // 직구
    imports("02"), // 수입
    move("02"), // 이동요청 (수입)
    purchase("01"); // 일반발주 (수입)
    private final String fieldName;
}
