package io.spring.model.nhncloud.template.sms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsTemplate {
    private String body;
    private String sendNo;
    private List<Recipient> recipientList;

    public SmsTemplate(String body, String sendNo){
        this.body = body;
        this.sendNo = sendNo;
    }

    public void setRecipient(List<Recipient> recipientList){
        this.recipientList = recipientList;
    }
}
