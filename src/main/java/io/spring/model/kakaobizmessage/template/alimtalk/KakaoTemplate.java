package io.spring.model.kakaobizmessage.template.alimtalk;

import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;

public interface KakaoTemplate {
    public void setTemplate(TbOrderDetail tod, TbOrderMaster tom, TbMember tm);
}
