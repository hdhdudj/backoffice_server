package io.spring.service.nhncloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.spring.enums.MessageType;
import io.spring.infrastructure.util.StringFactory;
import io.spring.jparepos.kakaobizmessage.JpaSendMessageLogRepository;
import io.spring.model.nhncloud.entity.SendMessageLog;
import io.spring.model.nhncloud.template.sms.Recipient;
import io.spring.model.nhncloud.template.sms.SmsTemplate;
import io.spring.model.order.entity.TbMember;
import io.spring.model.order.entity.TbOrderDetail;
import io.spring.model.order.entity.TbOrderMaster;
import io.spring.service.HttpApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
//@PropertySource("classpath:kakaobizmessage.yml")
//@PropertySource("file:${app.home}/kakaobizmessage.yml")
public class SmsService {
    private final ObjectMapper objectMapper;
    private final HttpApiService httpApiService;

    private final JpaSendMessageLogRepository jpaSendMessageLogRepository;
    // api 주소 : nhnCloudUrl + appKey + sms
    @Value("${sms.url}")
    private String nhnCloudUrl;
    @Value("${appKey.sms}")
    private String appKey;
    @Value("${sms.sms}")
    private String sms;
    @Value("${sms.sendNo}")
    private String sendNo;

    @Value("${secretKey.sms}")
    private String secretKey;

	public void sendSmsMessage(String body, TbOrderDetail tod, String userId) {
        String reqUrl = nhnCloudUrl + appKey + sms;
        TbOrderMaster tom = tod.getTbOrderMaster();
        TbMember tm = tom.getTbMember();
        SmsTemplate smsTemplate = new SmsTemplate(body, sendNo);
        Recipient r = new Recipient(tm);
        List<Recipient> rList = new ArrayList<>();
        rList.add(r);
        smsTemplate.setRecipient(rList);
        try{
            String jsonBody = objectMapper.writeValueAsString(smsTemplate);
            Map<String, String> headerMap = new HashMap<String, String>(){{
                put(StringFactory.getStrXSecretKey(), secretKey);
                put(StringFactory.getStrContentType(), StringFactory.getStrContentTypeValue());
            }};
            int res = httpApiService.post(reqUrl, headerMap, jsonBody);

            if(res == 200){
                SendMessageLog sl = new SendMessageLog(tod, tm, MessageType.sms);
				sl.setRegId(userId);
				sl.setUpdId(userId);
                jpaSendMessageLogRepository.save(sl);
            }
            else {
                log.debug("sms가 정상적으로 보내지지 않았습니다.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
