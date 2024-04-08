package com.github.vincemann.springrapid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.login.AuthenticationSuccessHandler;
import com.github.vincemann.springrapid.auth.login.CustomLoginConfigurer;
import com.github.vincemann.springrapid.auth.jwt.JwtAuthenticationFilter;
import com.github.vincemann.springrapid.auth.jwt.AuthorizationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class. Extend it in the
 * application, and make a configuration class. Override
 * protected methods, if you need any customization.
 * 
 * @author Sanjay Patel
 * @modifiedBy vincemann
 */
public class WebSecurityConfig {


	protected AuthProperties properties;
	protected AuthorizationTokenService authorizationTokenService;
	protected AuthenticationSuccessHandler authenticationSuccessHandler;
	protected ObjectMapper objectMapper;

	public WebSecurityConfig() {
		// Default constructor
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		sessionCreationPolicy(http); // Sets the session creation policy
		logout(http); // Configures logout behavior
		exceptionHandling(http); // Handles exceptions like forbidden access
		tokenAuthentication(http); // Adds JWT token authentication filter
		csrf(http); // Disables CSRF protection as stateless
		authorizeRequests(http); // Configures URL-based authorization
		login(http); // Configures custom login behavior
		otherConfigurations(http); // Additional HTTP configurations can be added here
		return http.build();
	}

	/**
	 * Configures custom login behavior using credentials in the request body.
	 */
	protected void login(HttpSecurity http) throws Exception {
		http.apply(new CustomLoginConfigurer<>(objectMapper))
				.loginProcessingUrl(properties.getLoginUrl())
				.successHandler(authenticationSuccessHandler)
				.failureHandler(new SimpleUrlAuthenticationFailureHandler());
	}

	/**
	 * Configures the session creation policy to STATELESS as required by stateless applications.
	 */
	protected void sessionCreationPolicy(HttpSecurity http) throws Exception {
		http.sessionManagement(customizer -> customizer
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}

	/**
	 * Disables logout functionality since stateless applications do not use sessions.
	 */
	protected void logout(HttpSecurity http) throws Exception {
		http.logout(AbstractHttpConfigurer::disable);
	}

	/**
	 * Configures exception handling to prevent redirection to login page when unauthorized access occurs.
	 */
	protected void exceptionHandling(HttpSecurity http) throws Exception {
		http.exceptionHandling(customizer -> customizer
				.authenticationEntryPoint(new Http403ForbiddenEntryPoint()));
	}

	/**
	 * Adds a JWT authentication filter before the username and password authentication filter.
	 */
	protected void tokenAuthentication(HttpSecurity http) throws Exception {
		http.addFilterBefore(new JwtAuthenticationFilter(authorizationTokenService, properties),
				UsernamePasswordAuthenticationFilter.class);
	}

	/**
	 * Disables CSRF protection to support a stateless application architecture.
	 */
	protected void csrf(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
	}

	/**
	 * Configures URL-based authorization rules. Override this method to customize the authorization strategy.
	 */
	protected void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(customizer -> customizer
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated());
	}

	/**
	 * Placeholder method for adding additional HTTP security configurations.
	 */
	protected void otherConfigurations(HttpSecurity http) throws Exception {
		// Additional configuration can be implemented here
	}

	@Autowired
	public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
		this.authorizationTokenService = authorizationTokenService;
	}

	@Autowired
	public void setProperties(AuthProperties properties) {
		this.properties = properties;
	}

	@Autowired
	public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
