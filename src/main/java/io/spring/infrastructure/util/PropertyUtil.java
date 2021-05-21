package io.spring.infrastructure.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import io.spring.service.file.FileService;
import lombok.extern.slf4j.Slf4j;


public class PropertyUtil {

	//private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static String getProperty(String propertyName) {
		return getProperty(propertyName, null);
		}
	
		public static String getProperty(String propertyName, String defaultValue) {
		String value = defaultValue;
		ApplicationContext applicationContext = ApplicationContextServe.getApplicationContext();
		if(applicationContext.getEnvironment().getProperty(propertyName) == null) {
			System.out.println(propertyName + " properties was not loaded.");
			//logger.debug(propertyName + " properties was not loaded.");
			
		} else {
		value = applicationContext.getEnvironment().getProperty(propertyName).toString();
		}
		return value;
		}

}
