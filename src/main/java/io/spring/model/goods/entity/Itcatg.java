package io.spring.model.goods.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="itcatg")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itcatg {
    @Id
    private String categoryId;
    private Date effEndDt;
    private String categoryGid;
    @CreationTimestamp
    private Date effStaDt;
    private String categoryNm;
    private String categoryEnm;
    private String dispNm;
    private String dispOrder;
    private String upCategoryId;
    private Long lvl;
    private String isBottomYn;
    private String couponUseYn;
    private String tstopYn;
    private String categoryGb;
    private String innerCtgId;
    private String orderType;
    private String dispType;
    private String siteCateType;
    private String globalCtgId;
    private String brandId;
    private String joinId;
    private String dispGb;
    private String templateId;
    private Long userId;
    private Long regId;
    @CreationTimestamp
    private Date regDt;
    private Long updId;
    @UpdateTimestamp
    private Date updDt;
    private String rootCategoryId;
    private String linkUrl;
}
