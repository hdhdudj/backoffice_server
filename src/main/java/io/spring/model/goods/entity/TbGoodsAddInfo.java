package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_goods_add_info")
@Getter
@Setter
@NoArgsConstructor
public class TbGoodsAddInfo extends CommonProps implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sno;

	private String assortId;
	private String infoTitle;
	private String infoValue;

}
