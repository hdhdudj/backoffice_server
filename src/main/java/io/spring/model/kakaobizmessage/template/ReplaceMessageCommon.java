package io.spring.model.kakaobizmessage.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.infrastructure.util.StringFactory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 메시지 치환 전송시 공통 폼
 */
@Getter
public class ReplaceMessageCommon {
    private String senderKey;
    private String templateCode;
    @JsonProperty
    private List<KakaoTemplate> recipientList;
    public ReplaceMessageCommon(String senderKey, String templateCode, KakaoTemplate kakaoTemplate){
        this.senderKey = senderKey;
        this.templateCode = templateCode;
        this.recipientList = new ArrayList<>();
        this.recipientList.add(kakaoTemplate);
    }
}
