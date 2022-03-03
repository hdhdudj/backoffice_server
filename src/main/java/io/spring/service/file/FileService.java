package io.spring.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.spring.model.file.FileVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
//@PropertySource("classpath:application.properties")
public class FileService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
//	private  static final String FTP_PREPIX_URL = "https://trdst.hgodo.com";

	@Value("${ftp.host}")
	private String FTP_HOST;
	@Value("${ftp.id}")
	private String FTP_ID;
	@Value("${ftp.password}")
	private String FTP_PASSWORD;
	@Value("${ftp.editor_path}")
	private String FTP_EDITOR_PATH;
	@Value("${ftp.image_path}")
	private String FTP_IMAGE_PATH;	
	
	private int FTP_PORT=21;
	@Value("${ftp.prefix_url}")
	private String FTP_PREPIX_URL;
	
//	 @Autowired
//	    public FileService(@Value("${ftp.host}") String FTP_HOST,@Value("${ftp.id}") String FTP_ID,@Value("${ftp.password}") String FTP_PASSWORD,@Value("${ftp.editor_path}") String FTP_EDITOR_PATH,@Value("${ftp.image_path}") String FTP_IMAGE_PATH,
//	    		@Value("${ftp.prefix_url}") String FTP_PREPIX_URL) {
//
//			this.FTP_HOST = FTP_HOST;
//
//			this.FTP_ID=FTP_ID;
//
//			this.FTP_PASSWORD=FTP_PASSWORD;
//
//			this.FTP_EDITOR_PATH=FTP_EDITOR_PATH;
//			this.FTP_IMAGE_PATH=FTP_IMAGE_PATH;
//			this.FTP_PREPIX_URL=FTP_PREPIX_URL;
//	    }

	 public String deleteFile(FileVo f) {
		 
		 String fileName = f.getFileName();
		 String filePath = f.getFilePath();
		 
		 String ret = ftpDeleteFile( filePath , fileName);
		 return ret;
	 }
	
	public FileVo storeFile(String imageGb,MultipartFile file)  {
		
	
		
		  String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		  
		  try {
			  
	

 
		  FileVo f = new FileVo();
		  
		  String uuid = UUID.randomUUID().toString().replace("-", "");
		  
		 // f.setFileName(fileName);
		  String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		  String newFileName =uuid + "." + ext;
		  
		  f.setFileName(newFileName);
		  f.setOriginalFileName(fileName);
		  f.setFileSize(file.getSize());
	
			String directoryName = System.getProperty("user.dir");
		  
		  File newfile = new File(directoryName + File.separator + fileName);
		  file.transferTo(newfile);
		  String mimeType = URLConnection.guessContentTypeFromName(newfile.getName());
		  f.setFileType(mimeType);

		  
		  Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		  String currentDate = format.format(today);

        String year = currentDate.substring(0, 4);

        String month = currentDate.substring(4, 6);

        String day = currentDate.substring(6, 8);
        
     //   FTP_PATH="/editor_test";	
		
        String ftpPath="";
        
        if(imageGb.equals("03")) {
        	ftpPath = FTP_EDITOR_PATH;
        }else {
        	ftpPath = FTP_IMAGE_PATH;
        }
        
        String ServerPath = ftpPath + "/" + year + "/" + month + "/" + day + "/";
        String fileUrl =FTP_PREPIX_URL + ftpPath + "/" + year + "/" + month + "/" + day + "/";
        		
       // + "/";
        
        String ret = ftpConnect(ServerPath,newFileName,newfile);
        
        
        if(ret.equals("success")) {
        	f.setFilePath(ServerPath );
        	
        	newfile.delete();
        	
        }
        return f;
	
	        }catch(Exception e) {
	        	e.printStackTrace();
	        	return null;
	           // throw new FileUploadException("["+fileName+"] 파일 업로드에 실패하였습니다. 다시 시도하십시오.",e);
	        } 
		  
		 
	}
	
	
	private String ftpDeleteFile(String filePath ,String fileName) {
		
		FTPClient ftp = null;

		FileInputStream fis = null;

		boolean isSuccess = false;

		int reply = 0;

		ftp = new FTPClient();

		

		try {
			

			ftp.connect(FTP_HOST,FTP_PORT);

			

			//success

			reply = ftp.getReplyCode();

			 

			if (!FTPReply.isPositiveCompletion(reply)) {

				ftp.disconnect();

				logger.debug("FTP server refused connection.");

				System.exit(1);

			}

			 

			if(!ftp.login(FTP_ID, FTP_PASSWORD)) {

                ftp.logout();

                logger.debug("ftp 서버에 로그인하지 못했습니다.");

            }

            

            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            ftp.enterLocalPassiveMode();
            
            String fileToDelete = filePath + fileName;
            
            isSuccess = ftp.deleteFile(fileToDelete);
            if (isSuccess) {
                System.out.println("The file was deleted successfully.");
            } else {
                System.out.println("Could not delete the  file, it may not exist.");
            }     
            
            
		}catch (SocketException e) {

			e.printStackTrace();

			

			return "SocketException";

		} catch (IOException e) {

			e.printStackTrace();

			return "IOException";

		} finally {

		

				try {

				

					ftp.disconnect();

					logger.debug("FTP DISCONNECT   ");

					

					if (!isSuccess) {

						return "fail";

					}

				} catch (IOException e) {

					e.printStackTrace();

				}

				

	

		}

		
		return "success";
	}
	
	private String ftpConnect(String ftpFolder, String filename, File files) {

	
		   
		         
		
		FTPClient ftp = null;

		FileInputStream fis = null;

		boolean isSuccess = false;

		int reply = 0;

		ftp = new FTPClient();

		

		try {
			

			ftp.connect(FTP_HOST,FTP_PORT);

			

			//success

			reply = ftp.getReplyCode();

			 

			if (!FTPReply.isPositiveCompletion(reply)) {

				ftp.disconnect();

				logger.debug("FTP server refused connection.");

				System.exit(1);

			}

			 

			if(!ftp.login(FTP_ID, FTP_PASSWORD)) {

                ftp.logout();

                logger.debug("ftp 서버에 로그인하지 못했습니다.");

            }

            

            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            ftp.enterLocalPassiveMode();
            
            boolean dirExists = true;
            
            String[] directories = ftpFolder.split("/");
            for (String dir : directories ) {
             if (!dir.isEmpty() ) {
              if (dirExists) {
               dirExists = ftp.changeWorkingDirectory(dir);
              }
              if (!dirExists) {
               if (!ftp.makeDirectory(dir)) {
                throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + ftp.getReplyString()+"'");
               }
               if (!ftp.changeWorkingDirectory(dir)) {
                throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + ftp.getReplyString()+"'");
               }
              }
             }
            }               

           // boolean ismakeDirectory =ftp.makeDirectory(ftpFolder);
            
           // logger.debug("ismakeDirectory : "+ismakeDirectory);

            boolean ischangeWorkingDirectory = ftp.changeWorkingDirectory(ftpFolder);

            logger.debug("ischangeWorkingDirectory : "+ischangeWorkingDirectory); 

            logger.debug("directory : "+ftp.printWorkingDirectory());

            

            //파일 업로드

            String tempFileName = new String((ftpFolder+filename).getBytes("utf-8"),"iso_8859_1");

            logger.debug(files.getPath());

            fis = new FileInputStream(files);

            logger.debug("start send tempFileName  "+tempFileName);

            

            isSuccess = ftp.storeFile(tempFileName, fis);  

            logger.debug("send file RESULT :   "+isSuccess);

            

            

		} catch (SocketException e) {

			e.printStackTrace();

			

			return "SocketException";

		} catch (IOException e) {

			e.printStackTrace();

			return "IOException";

		} finally {

			if(fis != null) {

				try {

					fis.close();

					ftp.disconnect();

					logger.debug("FTP DISCONNECT   ");

					

					if (!isSuccess) {

						return "fail";

					}

				} catch (IOException e) {

					e.printStackTrace();

				}

				

			} 

		}

		return "success";

	}


	
}
