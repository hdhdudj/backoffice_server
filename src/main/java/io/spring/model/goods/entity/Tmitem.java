package io.spring.model.goods.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.Utilities;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.TmitemId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "tmitem")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TmitemId.class)
public class Tmitem extends CommonProps {
    public Tmitem(Ititmm ititmm){
        this.assortId = ititmm.getAssortId();
        this.itemId = ititmm.getItemId();
        this.effStaDt = new Date(); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
    }

	public Tmitem(String channelGb,String assortId, String itemId, String channelGoodsNo, String channelOptionsNo) {

		this.channelGb=channelGb;
		this.assortId = assortId;
		this.itemId = itemId;
		this.channelGoodsNo = channelGoodsNo;
		this.channelOptionsNo = channelOptionsNo;
        this.effStaDt = new Date(); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        this.effEndDt = Utilities.getStringToDate(StringFactory.getDoomDay()); // 마지막 날짜(없을 경우 9999-12-31 23:59:59?)
        this.shortYn="01";
        this.variationGb1="01";
        this.variationSeq1="0001";
		this.optionName1 = "단품";
		

	}

    @Id
    private String channelGb = StringFactory.getGbOne(); // 01 하드코딩
    @Id
    private String assortId;
    @Id
    private String itemId;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effStaDt;
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date effEndDt;
    private String shortYn;
    private String variationGb1;
    private String variationSeq1;
    private String variationGb2;
    private String variationSeq2;
    private String channelGoodsNo;
    private String channelOptionsNo;
    private Float optionPrice;
	private String optionName1;
	private String optionName2;
}
