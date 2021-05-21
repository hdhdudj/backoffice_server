package io.spring.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.spring.infrastructure.util.PropertyUtil;
import io.spring.model.file.FileVo;
import io.spring.service.deposit.JpaDepositService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class FileService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private  static final String REMOTE_URL = "https://trdst.hgodo.com";
	
	
	private String FTP_HOST;
	
	private String FTP_ID;
	
	private String FTP_PASSWORD;
	
	private String FTP_PATH;	
	
	private int FTP_PORT=21;
	
	 @Autowired
	    public FileService(@Value("${ftp.host}") String FTP_HOST,@Value("${ftp.id}") String FTP_ID,@Value("${ftp.password}") String FTP_PASSWORD,@Value("${ftp.path}") String FTP_PATH) {

			this.FTP_HOST = FTP_HOST;
			
			this.FTP_ID=FTP_ID;
			
			this.FTP_PASSWORD=FTP_PASSWORD;
			
			this.FTP_PATH=FTP_PATH;	
	    }  

	
	public FileVo storeFile(MultipartFile file)  {
		
	
		
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
        
        FTP_PATH="/editor_test";	
		
        String ServerPath = FTP_PATH + "/" + year + "/" + month + "/" + day + "/";
        		
       // + "/";
        
        String ret = ftpConnect(ServerPath,newFileName,newfile);
        
        
        if(ret.equals("success")) {
        	f.setFilePath(ServerPath + newFileName );
        	newfile.delete();
        	
        }
        return f;
	
	        }catch(Exception e) {
	        	e.printStackTrace();
	        	return null;
	           // throw new FileUploadException("["+fileName+"] 파일 업로드에 실패하였습니다. 다시 시도하십시오.",e);
	        } 
		  
		 
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
