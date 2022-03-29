package io.spring.model.goods.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import io.spring.infrastructure.util.PropertyUtil;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.common.entity.CommonProps;
import io.spring.model.file.FileVo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="itaimg")
@BatchSize(size = 10)
@NoArgsConstructor(access = AccessLevel.PROTECTED)

/*
 * String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url"); this.uid =
 * ii.getImageSeq(); this.fileName = ii.getImageName(); this.filePath =
 * ii.getImagePath(); this.url = prefixUrl + ii.getImagePath()
 * +ii.getImageName();
 */
public class Itaimg extends CommonProps {
	  public Itaimg(String imageGb, FileVo f){
		  
			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");
			this.imageGb = imageGb;
			this.imageName = f.getFileName();
			this.imageOriginalName = f.getOriginalFileName();
			this.imagePath = f.getFilePath();
			this.imageSize = f.getFileSize();
			this.imageType = f.getFileType();
			this.imageStatus = StringFactory.getGbOne(); // 01 하드코딩
			this.imageUrl = prefixUrl + f.getFilePath() + f.getFileName();
			this.imageHost = prefixUrl;
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
		private String imageUrl;
		private String imageHost;

}
