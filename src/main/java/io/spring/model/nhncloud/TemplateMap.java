package io.spring.model.nhncloud;

import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.nhncloud.template.alimtalk.KakaoTemplate;
import io.spring.model.nhncloud.template.alimtalk.Order125;
import io.spring.model.nhncloud.template.alimtalk.Order126;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * statusCd와 template 매칭을 위한 클래스
 */
@Component
public class TemplateMap {
    private final Map<TrdstOrderStatus, String> templateNameMap = new HashMap<TrdstOrderStatus, String>(){{
        put(TrdstOrderStatus.B01, StringFactory.getStrOrder125());
        put(TrdstOrderStatus.B02, StringFactory.getStrOrder126());
    }};

    public KakaoTemplate getTemplateObject(TrdstOrderStatus status){
        switch (status) {
            case B01 : return new Order125();
            case B02 : return new Order126();
        }
        return null;
    }
    @Bean
    public Map<TrdstOrderStatus, String> getTemplateNameMap(){
        return this.templateNameMap;
    }
}
