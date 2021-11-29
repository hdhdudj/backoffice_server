package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.IfBrandId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "if_brand")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(value = IfBrandId.class)
public class IfBrand extends CommonProps implements Serializable {
    @Id
    private String channelGb;
    @Id
    private String channelBrandId;
    private String channelBrandNm;
    @Column(unique = true)
    private String brandId;
    private String brandNm;

    @JoinColumn(name="brandId", referencedColumnName = "brandId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
//    @NotFound(action = NotFoundAction.IGNORE)
    private Itbrnd itbrnd; // itbrnd 연관관계
}
