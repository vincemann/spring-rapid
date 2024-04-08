package com.github.vincemann.springrapid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.login.CustomLoginConfigurer;
import com.github.vincemann.springrapid.auth.handler.AuthenticationSuccessHandler;
import com.github.vincemann.springrapid.auth.jwt.JwtAuthenticationFilter;
import com.github.vincemann.springrapid.auth.jwt.AuthorizationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


	protected AuthProperties properties;
	protected AuthorizationTokenService authorizationTokenService;
	protected AuthenticationSuccessHandler authenticationSuccessHandler;

	protected ObjectMapper objectMapper;

	public WebSecurityConfig() {

	}


//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		disableFilterForLogin(web);
//	}

	/**
	 * Security configuration, calling protected methods
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		sessionCreationPolicy(http); // set session creation policy
		logout(http); // logout related configuration
		exceptionHandling(http); // exception handling
		tokenAuthentication(http); // configure token authentication filter
		csrf(http); // CSRF configuration
		authorizeRequests(http); // authorize requests
		otherConfigurations(http); // override this to add more configurations
		login(http); // authentication
		exceptionHandling(http); // exception handling
	}



	// make sure to enable Spring Security when testing with mvc
				/*

				@Override
				protected DefaultMockMvcBuilder createMvcBuilder() {
					DefaultMockMvcBuilder mvcBuilder = super.createMvcBuilder();
					mvcBuilder.apply(SecurityMockMvcConfigurers.springSecurity());
					return mvcBuilder;
				}
				 */
	/**
	 * Configuring authentication.
	 */
	protected void login(HttpSecurity http) throws Exception {
		// using custom login configurer here to allow safer authentication by sending credentials in body
		http.apply(new CustomLoginConfigurer<>(objectMapper))
//				.loginPage(loginPage())
				.loginProcessingUrl(loginUrl())
				.successHandler(authenticationSuccessHandler)
				.failureHandler(new SimpleUrlAuthenticationFailureHandler());
	}

	/**
	 * Override this to change login URL
	 *
	 * @return
	 */
	protected String loginPage() {
		return null;
	}

	protected String loginUrl(){
		return properties.getLoginUrl();
	}
	
	/**
	 * Configuring session creation policy
	 */
	protected void sessionCreationPolicy(HttpSecurity http) throws Exception {
		
		// No session
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

		
	/**
	 * Logout related configuration
	 */
	protected void logout(HttpSecurity http) throws Exception {
		
		http
			.logout().disable(); // we are stateless; so /logout endpoint not needed			
	}

	
	/**
	 * Configures exception-handling
	 */
	protected void exceptionHandling(HttpSecurity http) throws Exception {
		
		http
		.exceptionHandling()
		
			/***********************************************
			 * To prevent redirection to the login page
			 * when someone tries to access a restricted page
			 **********************************************/
			.authenticationEntryPoint(new Http403ForbiddenEntryPoint());
	}


	/**
	 * Configuring token authentication filter
	 */
	protected void tokenAuthentication(HttpSecurity http) throws Exception {
		//needs to be created with new, cant be autowired for some spring internal reasons
		http.addFilterBefore(new JwtAuthenticationFilter(authorizationTokenService,properties),
				UsernamePasswordAuthenticationFilter.class);
	}



	/**
	 * Disables CSRF. We are stateless.
	 */
	protected void csrf(HttpSecurity http) throws Exception {
		
		http
			.csrf().disable();
	}


	
	/**
	 * URL based authorization configuration. Override this if needed.
	 */
	protected void authorizeRequests(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/**").permitAll();
	}
	

	/**
	 * Override this to add more http configurations,
	 * such as more authentication methods.
	 * 
	 * @param http
	 * @throws Exception
	 */
	protected void otherConfigurations(HttpSecurity http)  throws Exception {

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
