package io.spring.model.goods.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="itbrnd")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itbrnd {
    @Id
    @Column(name = "brand_id")
    private String brandId;
    private String brandNm;
    private String brandEnm;
    private Long regId;
    private Long updId;
    @CreationTimestamp
    private Date regDt;
    @UpdateTimestamp
    private Date updDt;

    @Override
    public String toString() {
        return "Itbrnd [brandId=" + brandId + ", brandNm=" + brandNm + ", brandEnm=" + brandEnm + ", regId=" + regId
                + ", updId=" + updId + ", regDt=" + regDt + ", updDt=" + updDt + "]";
    }
}
