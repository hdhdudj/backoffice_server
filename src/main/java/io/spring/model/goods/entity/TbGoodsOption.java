package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.idclass.TbGoodsOptionId;
import io.spring.model.goods.request.GoodsPostRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="tb_goods_option")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(TbGoodsOptionId.class)
public class TbGoodsOption extends CommonProps implements Serializable {
	
	public TbGoodsOption(String assortId, GoodsPostRequestData.Items items) {
		this.assortId = assortId;
		this.shortYn = items.getShortYn();
		this.addPrice = items.getAddPrice() == null || items.getAddPrice().trim().equals("") ? null
				: Float.parseFloat(items.getAddPrice());
	}

	public TbGoodsOption(GoodsPostRequestData goodsPostRequestData) {
		this.assortId = goodsPostRequestData.getAssortId();
		this.itemNm = goodsPostRequestData.getAssortNm();
		this.shortYn = goodsPostRequestData.getShortageYn();
	}

	@Override
	public String toString() {
		return "TbGoodsOption : assortId=" + this.assortId + ", itemId=" + this.itemId;
	}

	@Id
	private String assortId;
	@Id
	private String itemId;
	private String itemNm;
	private String shortYn;
	private Long minCnt = 0l; // 하드코딩
	private Long maxCnt = 0l; // 하드코딩
	private Long dayDeliCnt;
	private Long totDeliCnt;
	private String variationGb1;
	private String variationSeq1;
	private String variationGb2;
	private String variationSeq2;
	// 21-11-25 추가
	private String variationGb3;
	private String variationSeq3;
	// 추가 끝
	private String setYn = StringFactory.getGbTwo(); // 02 하드코딩
	private String orderLmtYn;
	private Long orderLmtCnt;
	private Float addPrice = 0f; // 하드코딩

	// 21-11-22 추가
	private String delYn = StringFactory.getGbTwo(); // 02 하드코딩
	// 21-12-02 추가
	private String modelNo;
	private String material;
	private Float purchasePrice;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
	private TbGoods tbGoods;


//    @NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
			@JoinColumn(name = "variationSeq1", referencedColumnName = "seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")), })
	private TbGoodsOptionValue tbGoodsOptionValue1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
			@JoinColumn(name = "variationSeq2", referencedColumnName = "seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")), })
	private TbGoodsOptionValue tbGoodsOptionValue2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "assortId", referencedColumnName = "assortId", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")),
			@JoinColumn(name = "variationSeq3", referencedColumnName = "seq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")), })
	private TbGoodsOptionValue tbGoodsOptionValue3;

}
