package io.spring.model.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.model.common.entity.CommonProps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="tb_order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TbOrderMaster extends CommonProps {
    public TbOrderMaster(String orderId){
        this.orderId = orderId;
    }
    @Id
    private String orderId;
    private String firstOrderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date orderDate;
    private String orderStatus;
    private String channelGb;
    private Long custId;
    private Long deliId;
    private Float orderAmt;
    private Float receiptAmt;
    private String firstOrderGb;
    private String orderGb;
    private String channelOrderNo;
    private String custPcode;
    private String orderMemo;


    @JoinColumn(name = "custId", referencedColumnName = "custId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "none"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private TbMember tbMember; // tbMember 연관관계
}
