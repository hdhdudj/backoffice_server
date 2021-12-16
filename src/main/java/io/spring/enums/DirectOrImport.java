package io.spring.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DirectOrImport {
    direct("01"), // 직구
    imports("02"), // 수입
    move("02"), // 이동요청 (수입)
    purchase("01"); // 일반발주 (수입)
    private final String fieldName;
}
