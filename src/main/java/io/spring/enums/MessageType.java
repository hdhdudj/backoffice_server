package io.spring.enums;

import io.spring.model.common.EnumCommonInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType implements EnumCommonInterface {
    sms("01"), // 문자
    alimtalk("02"); // 카카오 알림톡
    private final String fieldName;
}
