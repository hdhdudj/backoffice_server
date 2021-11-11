package io.spring.model.kakaobizmessage.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.kakaobizmessage.TemplateMap;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * template code : order_125, B01일 때 보내게 되는 메시지
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Order125 implements KakaoTemplate{
    private String recipientNo;
    @JsonProperty
    private TemplateParams templateParameter;
    public void setTemplate(TbOrderDetail tod, TbOrderMaster tom, TbMember tm){
        this.recipientNo = tm.getCustHp();
        this.templateParameter = new TemplateParams(tod, tm);
    }

    @Getter
    @Setter
    public class TemplateParams{
        public TemplateParams(TbOrderDetail tod, TbMember tm){
            this.orderNo = tod.getChannelOrderNo();
            this.orderName = tm.getCustNm();
            this.goodsNm = tod.getGoodsNm();
        }
        private TemplateParams templateParameter;
        private String rc_mallNm = StringFactory.getStrTrdst(); // Trdst 하드코딩
        private String orderName;
        private String orderNo;
        private String goodsNm;
        private String shopUrl = StringFactory.getStrUrlTrdst(); // trdst.com 하드코딩
    }
}
