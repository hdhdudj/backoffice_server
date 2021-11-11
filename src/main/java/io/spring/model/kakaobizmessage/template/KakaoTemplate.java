package io.spring.model.kakaobizmessage.template;

import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;

public interface KakaoTemplate {
    public void setTemplate(String senderKey, TbOrderDetail tod, TbOrderMaster tom, TbMember tm);
}
