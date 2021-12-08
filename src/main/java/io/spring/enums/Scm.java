package io.spring.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Scm {
    Trdst("1"),
    세보코리아("63"),
    다른공급사("64");
    private final String fieldName;
}
