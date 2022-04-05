package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsPostRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_goods_option_supplier")
@Getter
@Setter
@NoArgsConstructor
public class TbGoodsOptionSupplier extends CommonProps implements Serializable {

	// GoodsInsertRequestData.itemSupplier

	public TbGoodsOptionSupplier(String assortId, GoodsPostRequestData.itemSupplier o) {

		this.assortId = assortId;
		this.itemId = o.getItemId();
		this.supplierId = o.getSupplierId();
		this.salePrice = o.getSalePrice();
		this.stockCnt = o.getStockCnt();
		this.saleYn = o.getSaleYn();
		this.offlineSalePrice = o.getOfflineSalePrice();
		this.overseasSalePrice = o.getOverseasSalePrice();

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sno;

	private String assortId;
	private String itemId;
	private String supplierId;
	private float salePrice;
	private float offlineSalePrice;
	private float overseasSalePrice;

	private Long stockCnt;
	private String saleYn;

}
