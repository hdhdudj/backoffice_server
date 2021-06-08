package io.spring.model.goods.entity;

import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.file.FileVo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="itaimg")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Itaimg extends CommonProps {
	  public Itaimg(String imageGb,FileVo f){
		  this.imageGb = imageGb;
		  this.imageName = f.getFileName();
		  this.imageOriginalName = f.getOriginalFileName();
		  this.imagePath = f.getFilePath();
		  this.imageSize=f.getFileSize();
		  this.imageType= f.getFileType();
		  this.imageStatus= StringFactory.getGbOne(); // 01 하드코딩
	  }
	 
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long imageSeq;
	  private String imageGb;
	  private String imageName;
	  private String imageOriginalName;
	  private String imagePath;
	  private String imageStatus;
	  private Long imageSize;
	  private String imageType;
	  private String assortId;
}
