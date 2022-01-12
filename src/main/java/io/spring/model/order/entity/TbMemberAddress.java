package io.spring.model.order.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "tb_member_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbMemberAddress extends CommonProps {
    public TbMemberAddress(IfOrderMaster ifOrderMaster, TbMember tbMember){
        custId = tbMember.getCustId();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliId;
    private Long custId;
    private String deliNm;
    private String deliGb = StringFactory.getThreeStartCd();// 001 하드코딩
    private String deliTel;
    private String deliHp;
    private String deliZipcode;
    private String deliZonecode;
    private String deliAddr1;
    private String deliAddr2;
}
