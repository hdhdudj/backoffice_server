package io.spring.enums;

import io.spring.model.common.EnumCommonInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrdstOrderStatus implements EnumCommonInterface {
    A01("주문접수"),
    A02("주문확인"),
    B01("발주대기"),
    B02("발주완료"),
    C01("해외입고완료"),
    C02("이동지시"),
    C03("이동지시완료"),
    C04("국내(현지)입고완료"),
    D01("출고지시"),
    D02("출고"),
    D03("국제운송"),
    D04("통관"),
    D05("국내운송"),
    D06("배송완료"),
    E01("구매확정");
    private final String fieldName;
}
