package io.spring.model.kakaobizmessage;

import io.spring.enums.TrdstOrderStatus;
import io.spring.model.kakaobizmessage.template.KakaoTemplate;
import io.spring.model.kakaobizmessage.template.Order125;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateMap {
    private final Map<TrdstOrderStatus, KakaoTemplate> templateMap = new HashMap<TrdstOrderStatus, KakaoTemplate>(){{
        put(TrdstOrderStatus.B01, new Order125());
        put(TrdstOrderStatus.B02, new Order125());
    }};
    @Bean
    public Map<TrdstOrderStatus, KakaoTemplate> getTemplateMap(){
        return templateMap;
    }
}
