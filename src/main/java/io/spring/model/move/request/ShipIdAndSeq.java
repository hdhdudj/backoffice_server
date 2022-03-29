package io.spring.model.move.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipIdAndSeq {
    private String shipId;
    private String shipSeq;
}
