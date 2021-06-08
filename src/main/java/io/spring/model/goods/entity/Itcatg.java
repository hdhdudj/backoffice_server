package io.spring.model.goods.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name="itcatg")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itcatg extends CommonProps {
    @Id
    private String categoryId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    private String categoryGid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
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
    private String rootCategoryId;
    private String linkUrl;
}
