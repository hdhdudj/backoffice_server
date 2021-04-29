package io.spring.infrastructure.util.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${spring.h2.console.enabled:false}")
	private boolean h2ConsoleEnabled;

	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	private static final String[] AUTH_WHITELIST = {

			// -- swagger ui
			"/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {


		if (h2ConsoleEnabled)
			http.authorizeRequests().antMatchers("/h2-console", "/h2-console/**").permitAll().and().headers()
					.frameOptions().sameOrigin();

		http.csrf().disable().cors().and().exceptionHandling()
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll().antMatchers(HttpMethod.GET, "/articles/feed")
				.authenticated().antMatchers(HttpMethod.POST, "/users", "/users/login", "/test").permitAll()
				.antMatchers(HttpMethod.GET, "/tests").permitAll().antMatchers(HttpMethod.GET, "/orderList").permitAll()
				.antMatchers(HttpMethod.POST, "/testa").permitAll()
				.antMatchers(HttpMethod.GET, "/order/orders").permitAll()
				//
				.antMatchers(HttpMethod.GET, "/goods/select").permitAll()
				.antMatchers(HttpMethod.POST, "/goods/insert").permitAll()
				.antMatchers(HttpMethod.POST, "/goods/inserttest").permitAll()
				.antMatchers(HttpMethod.POST, "/goods/inittables").permitAll()
				.antMatchers(HttpMethod.GET, "/goods/getgoodslist").permitAll()
				//
				.antMatchers(AUTH_WHITELIST).permitAll()
				.antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags").permitAll().anyRequest()
				.authenticated();

		http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/*
	 * @Bean public CorsConfigurationSource corsConfigurationSource() { final
	 * CorsConfiguration configuration = new CorsConfiguration();
	 * configuration.setAllowedOrigins(asList("*"));
	 * configuration.setAllowedMethods(asList("HEAD", "GET", "POST", "PUT",
	 * "DELETE", "PATCH")); // setAllowCredentials(true) is important, otherwise: //
	 * The value of the 'Access-Control-Allow-Origin' header in the response must //
	 * not be the wildcard '*' when the request's credentials mode is 'include'.
	 * configuration.setAllowCredentials(true); // setAllowedHeaders is important!
	 * Without it, OPTIONS preflight request // will fail with 403 Invalid CORS
	 * request configuration.setAllowedHeaders(asList("Authorization",
	 * "Cache-Control", "Content-Type")); final UrlBasedCorsConfigurationSource
	 * source = new UrlBasedCorsConfigurationSource();
	 * source.registerCorsConfiguration("/**", configuration); return source; }
	 */
}
