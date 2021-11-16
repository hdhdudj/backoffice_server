package io.spring.model.nhncloud.template.sms;

import io.spring.model.order.entity.TbMember;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipient {
    private String recipientNo;

    public Recipient(TbMember tm){
        this.recipientNo = tm.getCustHp();
    }
}
