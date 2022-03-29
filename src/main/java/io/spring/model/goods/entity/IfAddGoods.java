package io.spring.model.goods.entity;

import io.spring.model.common.entity.CommonProps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "if_add_goods")
public class IfAddGoods extends CommonProps {
    @Id
    private String addGoodsNo;
    private String addGoodsId;
    private String scmNo;
    private String title;
    private String goodsNm;
    private String brandId;
    private String optionNm;
    private String makerNm;
    private Float goodsPrice;
    private Long stockCnt;
    private String viewFl;
    private String soldOutFl;
    private String uploadStatus;
}
