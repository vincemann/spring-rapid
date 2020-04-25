package com.naturalprogrammer.spring.lemon.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.handler.BadCredentialsExceptionHandler;
import com.naturalprogrammer.spring.lemon.auth.mail.MailSender;
import com.naturalprogrammer.spring.lemon.auth.mail.MockMailSender;
import com.naturalprogrammer.spring.lemon.auth.mail.SmtpMailSender;
import com.naturalprogrammer.spring.lemon.auth.security.LemonPermissionEvaluator;
import com.naturalprogrammer.spring.lemon.auth.security.service.BlueTokenService;
import com.naturalprogrammer.spring.lemon.auth.security.service.GreenTokenService;
import com.naturalprogrammer.spring.lemon.auth.security.service.LemonJweService;
import com.naturalprogrammer.spring.lemon.auth.security.service.LemonJwsService;
import com.naturalprogrammer.spring.lemon.auth.util.LecUtils;
import com.naturalprogrammer.spring.lemon.auth.util.LecwUtils;
import com.naturalprogrammer.spring.lemon.auth.validation.CaptchaValidator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.naturalprogrammer.spring.lemon.exceptions.config.LemonWebExceptionsAutoConfiguration;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@ServiceConfig
//@ComponentScan(basePackageClasses= BadCredentialsExceptionHandler.class)
@EnableAsync
@AutoConfigureBefore({
	LemonWebExceptionsAutoConfiguration.class})
public class LemonCommonsAutoConfiguration {

	private static final Log log = LogFactory.getLog(LemonCommonsAutoConfiguration.class);
	
	public LemonCommonsAutoConfiguration() {
		log.info("Created");
	}

	//should override bean in AclConfig
	//todo solve this more elegantly
	@Primary
	@ConditionalOnMissingBean(name = "lemonPermissionEvaluator")
	@Bean
	public PermissionEvaluator lemonPermissionEvaluator(AclService aclService){
		return new LemonPermissionEvaluator(aclService);
	}

	/**
	 * Configures LemonUtils
	 */
	@Bean
	public LecwUtils lecwUtils() {
		log.info("Configuring LecwUtils");
		return new LecwUtils();
	}

	

	/**
	 * Configures AuthTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(BlueTokenService.class)
	public BlueTokenService blueTokenService(LemonProperties properties) throws JOSEException {
		
        log.info("Configuring AuthTokenService");       
		return new LemonJwsService(properties.getJwt().getSecret());
	}


	/**
	 * Configures ExternalTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(GreenTokenService.class)
	public GreenTokenService greenTokenService(LemonProperties properties) throws KeyLengthException {
		
        log.info("Configuring ExternalTokenService");       
		return new LemonJweService(properties.getJwt().getSecret());
	}


	/**
	 * Configures Password encoder if missing
	 */
	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
	
		log.info("Configuring PasswordEncoder");		
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


	/**
	 * Configures a MockMailSender when the property
	 * <code>spring.mail.host</code> isn't defined.
	 */
	@Bean
	@ConditionalOnMissingBean(MailSender.class)
	@ConditionalOnProperty(name="spring.mail.host", havingValue="foo", matchIfMissing=true)
	public MailSender<?> mockMailSender() {

        log.info("Configuring MockMailSender");       
        return new MockMailSender();
	}

	
	/**
	 * Configures an SmtpMailSender when the property
	 * <code>spring.mail.host</code> is defined.
	 */
	@Bean
	@ConditionalOnMissingBean(MailSender.class)
	@ConditionalOnProperty("spring.mail.host")
	public MailSender<?> smtpMailSender(JavaMailSender javaMailSender) {
		
        log.info("Configuring SmtpMailSender");       
		return new SmtpMailSender(javaMailSender);
	}

	@Bean
	public LecUtils lecUtils(ApplicationContext applicationContext, ObjectMapper objectMapper) {
		return new LecUtils(applicationContext, objectMapper);
	}
	
	/**
	 * Configures CaptchaValidator if missing
	 */
	@Bean
	@ConditionalOnMissingBean(CaptchaValidator.class)
	public CaptchaValidator captchaValidator(LemonProperties properties) {
		
        log.info("Configuring LemonUserDetailsService");       
		return new CaptchaValidator(properties);
	}
}
