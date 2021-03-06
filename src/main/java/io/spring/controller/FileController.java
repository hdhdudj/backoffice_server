package io.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.spring.infrastructure.util.ApiResponseMessage;
import io.spring.infrastructure.util.StringFactory;
import io.spring.infrastructure.util.exception.ResourceNotFoundException;
import io.spring.model.file.FileVo;
import io.spring.model.file.request.GodoImageRequestData;
import io.spring.model.file.request.GodoImagesRequestData;
import io.spring.model.file.response.FileUploadFileResponseData;
import io.spring.model.file.response.GodoImagesResponseData;
import io.spring.model.goods.entity.Itaimg;
import io.spring.service.file.FileService;
import io.spring.service.goods.JpaGoodsNewService;
import io.spring.service.goods.JpaGoodsService;
import io.spring.service.goods.JpaProductsService;
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
	private final JpaGoodsNewService jpaGoodsNewService ;

	private final JpaProductsService jpaProductsService;
	


	@PostMapping(path = "/godoImages")
	public @ResponseBody GodoImagesResponseData getGodoImages(@RequestBody GodoImagesRequestData data)
			throws IOException {

		System.out.println("getGodoImages");

		String[] urls = data.getUrls();

		LinkedList<String> ret = new LinkedList<>();



		for (String url : urls) {

			System.out.println(url);

			if (url.equals("") || url == null) {

			}

			// System.out.println("godoImage");
			String changeString = "";
			try {
				InputStream input = new URL(url).openStream();

				byte[] fileContent = IOUtils.toByteArray(input);// FileUtils.readFileToByteArray(new File(filePath));

				input.close();

				String encodedString = Base64.getEncoder().encodeToString(fileContent);
				String fileExtName = url.substring(url.lastIndexOf(".") + 1);


				if (encodedString.length() > 0 && encodedString != null) {
					changeString = "data:image/" + fileExtName + ";base64, " + encodedString;
				}
			} catch (Exception e) {
				e.printStackTrace();
				changeString = "";
			}


			ret.add(changeString);

		}



		GodoImagesResponseData r = new GodoImagesResponseData();
		r.setImages(ret);

		return r;

	}

	@PostMapping(path = "/godoImage")
	public @ResponseBody String getGodoImage(@RequestBody GodoImageRequestData data) throws IOException {

		System.out.println("getGodoImage");

		String url = data.getUrl();

		System.out.println(url);

		if (url.equals("") || url == null) {
			return "";
		}

		System.out.println("godoImage");
		// https://trdst.hgodo.com/product_data/goods/editor/201222/melampo_terra_image4752536-960x960_102001.jpg

		// String url
		// ="https://trdst.hgodo.com/data/editor/goods/200615/mathieu-challieres-demi-grande-voliere_165826.jpg";
		
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
		 * String changeString = ???data:image/???+ fileExtName +???;base64, ???+
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

	// 20220307 rjb80 requestbody ??????
	@PostMapping("/uploadFile")
	public ResponseEntity uploadFile(@RequestParam("imageGb") String imageGb, @RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file) {
//	public ResponseEntity uploadFile(@RequestBody UploadFileRequestData req, @RequestParam("file") MultipartFile file) {
       // String fileName = service.storeFile(file);
       // 
        //String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
         //                       .path("/downloadFile/")
           //                     .path(fileName)
             //                   .toUriString();
        
       // return new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  
		//?????????????????? 
		//???????????????
		//????????????
		//????????????
		//??????seq
		//????????????
		//???????????????
		
		// String imageGb = req.getImageGb();
//		String userId = req.getUserId();
		
		FileVo f = fileService.storeFile(imageGb,file);
		
		Itaimg ii = jpaGoodsService.saveItaimg(imageGb, f, userId);
		
		FileUploadFileResponseData r = new FileUploadFileResponseData(ii);
		
		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(),StringFactory.getStrSuccess(),r);
	       return ResponseEntity.ok(res);
	}
    
	@PostMapping("/v2/delete-image/{sno}")
	public ResponseEntity deleteFile2(@PathVariable("sno") String sno) {

		System.out.println("deleteFile2");
		jpaGoodsNewService.deleteGoodsImage(Long.parseLong(sno));

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), null);
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("/v3/delete-image/{sno}")
	public ResponseEntity deleteFile3(@PathVariable("sno") String sno) {

		System.out.println("deleteFile2");
		jpaProductsService.deleteImage(Long.parseLong(sno));

		ApiResponseMessage res = new ApiResponseMessage(StringFactory.getStrOk(), StringFactory.getStrSuccess(), null);
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
	  
			//?????????????????? 
			//???????????????
			//????????????
			//????????????
			//??????seq
			//????????????
			//???????????????
			
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
