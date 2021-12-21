package io.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

@Slf4j
@RestController
@RequestMapping(value="/file")
@RequiredArgsConstructor
public class FileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final FileService fileService ;
	private final JpaGoodsService jpaGoodsService ;
	
	@GetMapping(path = "/godoImage", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getGodoImage() throws IOException {

		// https://trdst.hgodo.com/product_data/goods/editor/201222/melampo_terra_image4752536-960x960_102001.jpg

		InputStream input = new URL(
				"https://trdst.hgodo.com/data/editor/goods/200615/mathieu-challieres-demi-grande-voliere_165826.jpg")
						.openStream();

		return IOUtils.toByteArray(input);

	}

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
