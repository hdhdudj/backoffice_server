package io.spring;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
@SpringBootApplication
@EnableJpaRepositories
@EntityScan("io.spring.model.*")
public class TrdstApplication {
//	 private static final String P"ROPERTIES = "spring.config.location=" +
//	 "classpath:application.properties"
//	 + ",classpath:kakaobizmessage".yml";

	// ;

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(TrdstApplication.class)
//				 .properties(PROPERTIES)
				.run(args);
//		SpringApplication.run(TrdstApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			// api요청주소가 추가되면 추가하면됨.

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				// .allowedOrigins("http://localhost:3000", "https://erp.trdst.com",
				// "https://terp.trdst.com",
//								"http://www.trdst.com", "https://www.trdst.com", "http://m.trdst.com",
				// "https://m.trdst.com", "https://trdst.com", "http://trdst.com",
				// "http://erp.trdst.com",
				// "http://terp.trdst.com")
//						.allowCredentials(true)
						.allowedOrigins("*").allowCredentials(false)
						.allowedMethods("HEAD", "GET",
						"PUT", "POST", "DELETE", "PATCH");
			}
		};
	}

}
