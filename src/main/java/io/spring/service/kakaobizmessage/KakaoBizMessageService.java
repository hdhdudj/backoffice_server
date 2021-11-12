package io.spring.service.kakaobizmessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.enums.MessageType;
import io.spring.enums.TrdstOrderStatus;
import io.spring.infrastructure.util.StringFactory;
import io.spring.jparepos.kakaobizmessage.JpaSendMessageLogRepository;
import io.spring.model.kakaobizmessage.TemplateMap;
import io.spring.model.kakaobizmessage.entity.SendMessageLog;
import io.spring.model.kakaobizmessage.template.alimtalk.KakaoTemplate;
import io.spring.model.kakaobizmessage.template.alimtalk.ReplaceMessageCommon;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.service.HttpApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Component
@PropertySource("classpath:kakaobizmessage.yml")
public class KakaoBizMessageService {
    private final TemplateMap templateMap;
    private final ObjectMapper objectMapper;
    private final HttpApiService httpApiService;

    private final JpaSendMessageLogRepository jpaSendMessageLogRepository;
    // api 주소 : nhnCloudUrl + alimtalkUrl + appKey + message
    @Value("${url.nhnCloud}")
    private String nhnCloudUrl;
    @Value("${url.alimtalk}")
    private String alimtalkUrl;
    @Value("${appKey}")
    private String appKey;
    @Value("${url.messages}")
    private String message;

    @Value("${secretKey}")
    private String secretKey;
    @Value("${senderKey}")
    private String senderKey;

    public void sendKakaoBizMessage(String statusCd, TbOrderDetail tod){
        String reqUrl = nhnCloudUrl + alimtalkUrl + appKey + message;
        TbOrderMaster tom = tod.getTbOrderMaster();
        TbMember tm = tom.getTbMember();
        TrdstOrderStatus enumStatusCd = this.strCdToEnumCd(statusCd);
        KakaoTemplate template = this.templateMap.getTemplateObject(enumStatusCd);
        if(template == null){
            log.debug("존재하지 않는 템플릿입니다.");
            return;
        }
        template.setTemplate(tod, tom, tm);
        String tempNm = this.templateMap.getTemplateNameMap().get(enumStatusCd);
        if(tempNm == null){
            log.debug("존재하지 않는 템플릿 이름입니다.");
            return;
        }
        ReplaceMessageCommon replaceMessageCommon = new ReplaceMessageCommon(senderKey, tempNm, template);
        try{
            String jsonBody = objectMapper.writeValueAsString(replaceMessageCommon);
            Map<String, String> headerMap = new HashMap<String, String>(){{
                put(StringFactory.getStrXSecretKey(), secretKey);
                put(StringFactory.getStrContentType(), StringFactory.getStrContentTypeValue());
            }};
            int res = httpApiService.post(reqUrl, headerMap, jsonBody);

            if(res == 200){
                SendMessageLog sl = new SendMessageLog(tod, tm, MessageType.alimtalk);
                jpaSendMessageLogRepository.save(sl);
            }
            else {
                log.debug("알림톡이 정상적으로 보내지지 않았습니다.");
            }
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



