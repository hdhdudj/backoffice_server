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
import io.spring.model.goods.request.ProductsPostRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products_image")
@Getter
@Setter
@NoArgsConstructor
public class ProductsImage extends CommonProps implements Serializable {

	// GoodsInsertRequestData.UploadMainImage

	public ProductsImage(Long productId, ProductsPostRequestData.MainImage o) {

		this.imageGb = o.getImageGb();
		this.imageUrl = o.getUrl();

		if (o.getFileYn().toUpperCase().equals("Y")) {
			this.imageSeq = Long.valueOf(o.getUid());
		}

		this.productId = productId;
		this.fileYn = o.getFileYn();
	}


	public ProductsImage(Long productId, ProductsPostRequestData.AddImage o) {

		this.imageGb = o.getImageGb();
		this.imageUrl = o.getUrl();

		if (o.getFileYn().toUpperCase().equals("Y")) {
			this.imageSeq = Long.valueOf(o.getUid());
		}

		this.productId = productId;
		this.fileYn = o.getFileYn();

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sno;

	private String imageGb;
	private String imageUrl;


	private Long imageSeq;
	private Long productId;
	private String fileYn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "imageSeq", referencedColumnName = "imageSeq", insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none")) })
	private Itaimg itaimg;

}
