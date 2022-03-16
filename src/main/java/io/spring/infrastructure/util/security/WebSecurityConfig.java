package io.spring.infrastructure.util.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;


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
	public void configure(WebSecurity web) throws Exception {
		web.httpFirewall(defaultHttpFirewall());
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {


		if (h2ConsoleEnabled)
			http.authorizeRequests().antMatchers("/h2-console", "/h2-console/**").permitAll().and().headers()
					.frameOptions().sameOrigin();

		http.csrf().disable().cors().and().exceptionHandling()
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll().antMatchers(HttpMethod.GET, "/articles/feed")
				.authenticated().antMatchers(HttpMethod.POST, "/users", "/users/login", "/users/refreshtoken", "/test").permitAll()
				.antMatchers(HttpMethod.GET, "/tests").permitAll().antMatchers(HttpMethod.GET, "/orderList").permitAll()
				.antMatchers(HttpMethod.GET, "/user").authenticated()
				.antMatchers(HttpMethod.POST, "/testa").permitAll()
				.antMatchers(HttpMethod.GET, "/order/orders").permitAll()
				//
				.antMatchers(HttpMethod.GET, "/goods/**").permitAll()
				.antMatchers(HttpMethod.POST, "/goods/**").permitAll()
				 .antMatchers(HttpMethod.GET, "/purchase/**").permitAll()
				.antMatchers(HttpMethod.POST, "/purchase/**").permitAll()
				.antMatchers(HttpMethod.GET, "/order/**").permitAll()
				.antMatchers(HttpMethod.GET, "/deposit/**").permitAll()
				.antMatchers(HttpMethod.POST, "/deposit/**").permitAll()
				.antMatchers(HttpMethod.GET, "/move/**").permitAll()
				.antMatchers(HttpMethod.POST, "/move/**").permitAll()
				.antMatchers(HttpMethod.GET, "/ship/**").permitAll()
				.antMatchers(HttpMethod.POST, "/ship/**").permitAll()
				.antMatchers(HttpMethod.POST, "/order/**").permitAll()
				.antMatchers(HttpMethod.GET, "/category/*").permitAll()
				.antMatchers(HttpMethod.GET, "/common/*").permitAll()
				.antMatchers(HttpMethod.POST, "/common/*").permitAll()
				.antMatchers(HttpMethod.GET, "/file/**").permitAll()
				.antMatchers(HttpMethod.POST, "/file/**").permitAll()
				.antMatchers(HttpMethod.GET, "/xml/**").permitAll()
				.antMatchers(HttpMethod.GET, "/napi/**").permitAll()

			
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
