package io.spring.model.file.response;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import io.spring.infrastructure.util.PropertyUtil;
import io.spring.model.goods.entity.Itaimg;
import io.spring.model.goods.response.GoodsSelectListResponseData.Goods;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@PropertySource("classpath:application.properties")
public class FileUploadFileResponseData {

		private Long uid;
		private String fileName;
		private String filePath;
		private String url;
		private String status;
		
		
		public FileUploadFileResponseData(Itaimg ii) {
			
			String prefixUrl = PropertyUtil.getProperty("ftp.prefix_url");
			this.uid = ii.getImageSeq();
			this.fileName = ii.getImageName();
			this.filePath = ii.getImagePath();
			this.url = prefixUrl + ii.getImagePath() +ii.getImageName(); 
			this.status="done";
		}
	
}
