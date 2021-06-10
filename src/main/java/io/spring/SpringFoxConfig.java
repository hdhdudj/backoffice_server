package io.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@EnableSwagger2
@ConditionalOnExpression(value = "${useSwagger:false}")
@Configuration
public class SpringFoxConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("trdst backend api with Swagger").description("trdst api")
				.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
				.license("Apache License Version 2.0")
				.contact(new Contact("trdst Swagger", "https://trdst.com/", "dev@trdst.com"))
				.licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE").version("2.0")
				.build();
	}

	/*
	 * @Bean public LinkDiscoverers discoverers() { List<LinkDiscoverer> plugins =
	 * new ArrayList<>(); plugins.add(new CollectionJsonLinkDiscoverer()); return
	 * new LinkDiscoverers(SimplePluginRegistry.create(plugins)); }
	 */

}