package io.spring.model.goods.entity;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.TmitemId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "tmitem")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TmitemId.class)
public class Tmitem extends CommonProps {
    @Id
    private String channelGb;
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    private String effStaDt;
    @Id
    private String effEndDt;
    private String shortYn;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String channelGoodsNo;
    private String channelOptionsNo;
}
