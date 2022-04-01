package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.ProductsMasterPostRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products_master")
@Getter
@Setter
@NoArgsConstructor
public class ProductsMaster extends CommonProps implements Serializable {

//	ProductsMasterPostRequestData

	public ProductsMaster(ProductsMasterPostRequestData v) {

		if (v.getMasterId() != null) {
			this.masterId = v.getMasterId();
			this.setRegId(v.getUserId());

		}

		this.masterNm = v.getMasterNm();
		this.masterDescription = v.getMasterDescription();
		this.optionKey1 = v.getOptionKey1();
		this.optionKey2 = v.getOptionKey2();
		this.optionKey3 = v.getOptionKey3();
		this.optionKey4 = v.getOptionKey4();
		this.optionKey5 = v.getOptionKey5();


	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long masterId;

	private String masterNm;
	private String masterDescription;

	private String optionKey1;
	private String optionKey2;
	private String optionKey3;
	private String optionKey4;
	private String optionKey5;

}
