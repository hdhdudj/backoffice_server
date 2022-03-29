package io.spring.model.goods.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.spring.model.common.entity.CommonProps;
import io.spring.model.goods.request.GoodsPostRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_goods_image")
@Getter
@Setter
@NoArgsConstructor
public class TbGoodsImage extends CommonProps implements Serializable {

	// GoodsInsertRequestData.UploadMainImage

	public TbGoodsImage(String assortId, GoodsPostRequestData.UploadMainImage o) {

		this.imageGb = o.getImageGb();
		this.imageUrl = o.getUrl();

		this.imageSeq = o.getUid();
		this.assortId = assortId;
	}


	public TbGoodsImage(String assortId, GoodsPostRequestData.UploadAddImage o) {

		this.imageGb = o.getImageGb();
		this.imageUrl = o.getUrl();

		this.imageSeq = o.getUid();
		this.assortId = assortId;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sno;

	private String imageGb;
	private String imageUrl;


	private Long imageSeq;
	private String assortId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "imageSeq", referencedColumnName = "imageSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")) })
	private Itaimg itaimg;

}
