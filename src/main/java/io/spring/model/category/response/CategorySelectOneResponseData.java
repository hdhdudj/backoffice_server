package io.spring.model.category.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.entity.Itcatg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategorySelectOneResponseData extends CommonProps {
    public CategorySelectOneResponseData(Itcatg itcatg){
        if(itcatg == null){
            return;
        }
        this.categoryId = itcatg.getCategoryId();
        this.effEndDt = itcatg.getEffEndDt();
        this.categoryGid = itcatg.getCategoryGid();
        this.effStaDt = itcatg.getEffStaDt();
        this.categoryNm = itcatg.getCategoryNm();
        this.categoryEnm = itcatg.getCategoryEnm();
        this.dispNm = itcatg.getDispNm();
        this.dispOrder = itcatg.getDispOrder();
        this.upCategoryId = itcatg.getUpCategoryId();
        this.lvl = itcatg.getLvl();
        this.isBottomYn = itcatg.getIsBottomYn();
        this.couponUseYn = itcatg.getCouponUseYn();
        this.tstopYn = itcatg.getTstopYn();
        this.categoryGb = itcatg.getCategoryGb();
        this.innerCtgId = itcatg.getInnerCtgId();
        this.orderType = itcatg.getOrderType();
        this.dispType = itcatg.getDispType();
        this.siteCateType = itcatg.getSiteCateType();
        this.globalCtgId = itcatg.getGlobalCtgId();
        this.brandId = itcatg.getBrandId();
        this.joinId = itcatg.getJoinId();
        this.dispGb = itcatg.getDispGb();
        this.templateId = itcatg.getTemplateId();
        this.userId = itcatg.getUserId();
        super.setRegId(itcatg.getRegId());
        super.setRegDt(itcatg.getRegDt());
        super.setUpdDt(itcatg.getUpdDt());
        super.setUpdId(itcatg.getUpdId());
        this.rootCategoryId = itcatg.getRootCategoryId();
        this.linkUrl = itcatg.getLinkUrl();
    }
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
