package io.spring.service.kakaobizmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.enums.TrdstOrderStatus;
import io.spring.model.kakaobizmessage.TemplateMap;
import io.spring.model.kakaobizmessage.template.KakaoTemplate;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.service.HttpApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Configuration
@PropertySource("classpath:kakaobizmessage.yml")
public class KakaoBizMessageService {
    private final TemplateMap templateMap;
    private final ObjectMapper objectMapper;
    private final HttpApiService httpApiService;
    // api 주소 : nhnCloudUrl + alimtalkUrl + appKey + message
//    @Value("${url.nhnCloud}")
    private String nhnCloudUrl = "https://api-alimtalk.cloud.toast.com";
//    @Value("${url.alimtalk}")
    private String alimtalkUrl = "/alimtalk/v2.2/appkeys/";
//    @Value("${appKey}")
    private String appKey = "6Tt6xBHx7coMPcBX";
//    @Value("${url.message}")
    private String message = "/messages";

//    @Value("${secretKey}")
    private String secretKey = "wUiXwVR5";
//    @Value("${senderKey}")
    private String senderKey = "16facfeb928b85c9817e5d44fd5bd81cff5a9342";

    public void sendKakaoBizMessage(String statusCd, TbOrderDetail tod){
        String reqUrl = nhnCloudUrl + alimtalkUrl + appKey + message;
        TbOrderMaster tom = tod.getTbOrderMaster();
        TbMember tm = tom.getTbMember();
        KakaoTemplate template = this.templateMap.getTemplateMap().get(this.strCdToEnumCd(statusCd));
        template.setTemplate(senderKey, tod, tom, tm);
        try{
            String jsonBody = objectMapper.writeValueAsString(template);
            Map<String, String> headerMap = new HashMap<String, String>(){{
                put("X-Secret-Key", secretKey);
                put("Content-Type", "application/json;charset=UTF-8");
            }};
            httpApiService.post(reqUrl, headerMap, jsonBody);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    private TrdstOrderStatus strCdToEnumCd(String statusCd){
        for(TrdstOrderStatus val : TrdstOrderStatus.values()){
            if(val.toString().equals(statusCd)){
                return val;
            }
        }
        return null;
    }
}



