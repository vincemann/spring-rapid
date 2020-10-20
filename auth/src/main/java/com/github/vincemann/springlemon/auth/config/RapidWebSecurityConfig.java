package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.handler.RapidAuthenticationSuccessHandler;
import com.github.vincemann.springlemon.auth.security.JwtAuthenticationFilter;
import com.github.vincemann.springlemon.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
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
 */
@Slf4j
public class RapidWebSecurityConfig extends WebSecurityConfigurerAdapter {


	private LemonProperties properties;
	private HttpTokenService httpTokenService;
	private AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService;
	private RapidSecurityContext<RapidAuthAuthenticatedPrincipal> securityContext;
	private RapidAuthenticationSuccessHandler authenticationSuccessHandler;

	public RapidWebSecurityConfig() {

	}

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
		cors(http); // CORS configuration
		authorizeRequests(http); // authorize requests
		otherConfigurations(http); // override this to add more configurations
		login(http); // authentication
		exceptionHandling(http); // exception handling
	}


	/**
	 * Configuring authentication.
	 */
	protected void login(HttpSecurity http) throws Exception {

		http
				.formLogin() // form login
				.loginPage(loginPage())

				/******************************************
				 * Setting a successUrl would redirect the user there. Instead,
				 * let's send 200 and the userDto along with an Authorization token.
				 *****************************************/
				.successHandler(authenticationSuccessHandler)

				/*******************************************
				 * Setting the failureUrl will redirect the user to
				 * that url if login fails. Instead, we need to send
				 * 401. So, let's set failureHandler instead.
				 *******************************************/
				.failureHandler(new SimpleUrlAuthenticationFailureHandler());
	}


	/**
	 * Override this to change login URL
	 *
	 * @return
	 */
	protected String loginPage() {
		return properties.getController().getLoginUrl();
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
		http.addFilterBefore(new JwtAuthenticationFilter(httpTokenService,authorizationTokenService,securityContext),
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
	 * Configures CORS
	 */
	protected void cors(HttpSecurity http) throws Exception {
		
		http
			.cors();
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
	public void injectHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}

	@Autowired
	public void injectAuthorizationTokenService(AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService) {
		this.authorizationTokenService = authorizationTokenService;
	}

	@Autowired
	public void injectSecurityContext(RapidSecurityContext<RapidAuthAuthenticatedPrincipal> securityContext) {
		this.securityContext = securityContext;
	}

	@Autowired
	public void injectProperties(LemonProperties properties) {
		this.properties = properties;
	}

	@Autowired
	public void injectAuthenticationSuccessHandler(RapidAuthenticationSuccessHandler authenticationSuccessHandler) {
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}
}
