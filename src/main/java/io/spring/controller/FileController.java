package io.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

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
	
	@GetMapping(path = "/godoImage2", produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody byte[] getGodoImage2() throws IOException {
		System.out.println("godoImage");
		// https://trdst.hgodo.com/product_data/goods/editor/201222/melampo_terra_image4752536-960x960_102001.jpg

		String url = "https://trdst.hgodo.com/data/editor/goods/200615/mathieu-challieres-demi-grande-voliere_165826.jpg";

		InputStream input = new URL(url).openStream();


		return IOUtils.toByteArray(input);

	}

	@GetMapping(path = "/godoImage")
	public @ResponseBody String getGodoImage() throws IOException {
		System.out.println("godoImage");
		// https://trdst.hgodo.com/product_data/goods/editor/201222/melampo_terra_image4752536-960x960_102001.jpg

		String url ="https://trdst.hgodo.com/data/editor/goods/200615/mathieu-challieres-demi-grande-voliere_165826.jpg";
		
		InputStream input = new URL(url).openStream();

		/*
		 * ByteArrayOutputStream byteOutStream = null; byteOutStream = new
		 * ByteArrayOutputStream();
		 * 
		 * int len = 0; byte[] buf = new byte[1024]; while ((len = input.read(buf)) !=
		 * -1) { byteOutStream.write(buf, 0, len); }
		 * 
		 * String fileExtName = url.substring(url.lastIndexOf(".") + 1);
		 * 
		 * byte[] fileArray = byteOutStream.toByteArray(); String imageString = new
		 * String(Base64.encodeBase64(fileArray)); String changeString = "data:image/" +
		 * fileExtName + ";base64, " + imageString;
		 */

		/*
		 * 
		 * ByteArrayOutputStream byteOutStream = null; byteOutStream = new
		 * ByteArrayOutputStream();
		 * 
		 * int len = 0; byte[] buf = new byte[1024]; while( (len = input.read( buf )) !=
		 * -1 ) { byteOutStream.write(buf, 0, len); }
		 * 
		 * byte[] fileArray = byteOutStream.toByteArray(); imageString[i] = new String(
		 * Base64.encodeBase64( fileArray ) );
		 * 
		 * String changeString = “data:image/”+ fileExtName +”;base64, “+
		 * imageString[i]; content = content.replace(imageUrl[i], changeString);
		 * 
		 */

		byte[] fileContent = IOUtils.toByteArray(input);// FileUtils.readFileToByteArray(new File(filePath));

//		String imageString = new String(Base64.encodeBase64String(fileContent));
//
		// System.out.println(fileContent.length);

		// String encodedString = Base64.getEncoder().encodeToString(fileContent);
		// Base64
		// System.out.println(fileContent);
		// String encoded = Base64.getEncoder().encodeToString(fileContent);

		// String encodedString = Base64.encodeToString(fileContent, Base64.DEFAULT); //
		// Base64.encodeToString(fileContent,Base64.default);

		// System.out.println(imageString);

		input.close();

		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		String fileExtName = url.substring(url.lastIndexOf(".") + 1);

		String changeString = "data:image/" + fileExtName + ";base64, " + encodedString;

		System.out.println(encodedString.length());
		System.out.println(changeString.length());
		return changeString;

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
