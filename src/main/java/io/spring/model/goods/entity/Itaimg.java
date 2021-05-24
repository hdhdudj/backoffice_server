package io.spring.model.goods.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.spring.model.file.FileVo;
import io.spring.model.purchase.entity.Lspchs;
import io.spring.model.purchase.request.PurchaseInsertRequestData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="itaimg")
@NoArgsConstructor
public class Itaimg {
	 private final static Logger logger = LoggerFactory.getLogger(Lspchs.class);
	 
	 
	  public Itaimg(String imageGb,FileVo f){
		  this.imageGb = imageGb;
		  this.imageName = f.getFileName();
		  this.imageOriginalName = f.getOriginalFileName();
		  this.imagePath = f.getFilePath();
		  this.imageSize=f.getFileSize();
		  this.imageType= f.getFileType();
		  this.imageStatus="01";
		  this.regId=1L;
		  this.updId=1L;
	  }
	 
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long imageSeq;
	  
	    
	    
	  private String imageGb;
	  private String   imageName;
	  private String   imageOriginalName;
	  private String   imagePath;
	  private String   imageStatus;
	  private Long  imageSize;
	  private String  imageType;
	  private String  assortId;
	  private Long   regId;
	  
	  @CreationTimestamp
	  private Date   regDt;
	  
	  private Long  updId;
	  @UpdateTimestamp
	  private Date   updDt; 
	    

}
