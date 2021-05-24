package io.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.model.file.FileVo;
import io.spring.model.file.response.FileUploadFileResponseData;
import io.spring.model.goods.entity.Itaimg;
import io.spring.service.file.FileService;
import io.spring.service.goods.JpaGoodsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="/file")
public class FileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FileService fileService ;
	

	@Autowired
	private JpaGoodsService jpaGoodsService ;
	
	@PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("imageGb") String imageGb,@RequestParam("file") MultipartFile file) {
       // String fileName = service.storeFile(file);
       // 
        //String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
         //                       .path("/downloadFile/")
           //                     .path(fileName)
             //                   .toUriString();
        
       // return new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  
		//파일업로드후 
		//파일원본명
		//파일이름
		//파일경로
		//파일seq
		//파일타입
		//파일사이즈
		
		
		FileVo f = fileService.storeFile(imageGb,file);
		
		Itaimg ii = jpaGoodsService.saveItaimg(imageGb, f);
		
		FileUploadFileResponseData r = new FileUploadFileResponseData(ii);
		
		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),r);
	       return ResponseEntity.ok(res);
	}
    
}
