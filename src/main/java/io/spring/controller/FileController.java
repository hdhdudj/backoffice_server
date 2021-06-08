package io.spring.controller;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.model.file.FileVo;
import io.spring.model.file.response.FileUploadFileResponseData;
import io.spring.model.goods.entity.Itaimg;
import io.spring.service.file.FileService;
import io.spring.service.goods.JpaGoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(value="/file")
@RequiredArgsConstructor
public class FileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final FileService fileService ;
	private final JpaGoodsService jpaGoodsService ;
	
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
    
	
	
	@PostMapping("/deleteFile/{uid}")
	 public ResponseEntity deleteFile(@PathVariable("uid") String uid) {
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
			
	//	jpaGoodsService.
		
		Itaimg r =	jpaGoodsService.getItaimg(Long.parseLong(uid));
		
		if(r==null) {
			  throw new ResourceNotFoundException();
		}
		
		FileVo f = new FileVo();
		f.setFilePath(r.getImagePath());
		f.setFileName(r.getImageName());
		
		String ret = fileService.deleteFile(f);
		
		if(ret.equals("success")) {
			jpaGoodsService.deleteItaimg(r);
		}
		
		
			ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),null);
		       return ResponseEntity.ok(res);
		}
	    	
}
