package io.spring.model.goods.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.goods.idclass.IfGoodsOptionId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "if_goods_option")
@Getter
@Setter
@ToString
@IdClass(IfGoodsOptionId.class)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class IfGoodsOption {


    @Id
    private String channelGb = StringFactory.getGbOne(); // 01 하드코딩
    @Id
    private String sno;
    @Id
    private String goodsNo;

    private String optionNo;
    private String assortId;
    private String itemId;
    private String optionName; // ifGoodsMaster에 있는 optionName 저장용 (ex : 색상^|^이불색상)
    private String optionValue1;
    private String optionValue2;
    private String optionValue3;
    private String optionValue4;
    private String optionValue5;
    private Float optionPrice;
    private String optionViewFl;
    private String optionCode;
    private String soldOutFl;
    private Long minOrderCnt;
    private Long maxOrderCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date regDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date modDt;
    private Long regId;
    private Long updId;
    private String uploadStatus;
}
