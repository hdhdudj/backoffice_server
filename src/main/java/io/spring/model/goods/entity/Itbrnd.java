package io.spring.model.goods.entity;

import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
//@ToString
@Table(name="itbrnd")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itbrnd extends CommonProps {
    @Id
    private String brandId;
    private String brandNm;
    private String brandEnm;

    @Override
    public String toString() {
        return "Itbrnd [brandId=" + brandId + ", brandNm=" + brandNm + ", brandEnm=" + brandEnm + ", regId=" + super.getRegId()
                + ", updId=" + super.getUpdId() + ", regDt=" + super.getRegDt() + ", updDt=" + super.getUpdDt() + "]";
    }
}
