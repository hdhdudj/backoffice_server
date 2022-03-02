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
	private static final String PROPERTIES = "spring.config.location=" + "classpath:application.properties"
			+ ",classpath:kakaobizmessage.yml";
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(TrdstApplication.class)
//				.properties(PROPERTIES)
				.run(args);
//		SpringApplication.run(TrdstApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowCredentials(false).allowedMethods("HEAD", "GET",
						"PUT", "POST", "DELETE", "PATCH");
			}
		};
	}

}
