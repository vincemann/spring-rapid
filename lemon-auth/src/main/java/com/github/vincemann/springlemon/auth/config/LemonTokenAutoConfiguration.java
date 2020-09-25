package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.service.token.*;
import com.github.vincemann.springlemon.auth.validation.CaptchaValidator;
import com.github.vincemann.springlemon.exceptions.config.LemonWebExceptionsAutoConfiguration;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@AutoConfigureBefore({LemonWebExceptionsAutoConfiguration.class})
@Slf4j
public class LemonTokenAutoConfiguration {


	public LemonTokenAutoConfiguration() {
		log.info("Created");
	}


	/**
	 * Configures JwsTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(JwsTokenService.class)
	public JwsTokenService jwsTokenService(LemonProperties properties) throws JOSEException {
		
        log.info("Configuring JwsTokenService");
		return new LemonJwsService(properties.getJwt().getSecret());
	}


	/**
	 * Configures JweTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(JweTokenService.class)
	public JweTokenService jweTokenService(LemonProperties properties) throws KeyLengthException {
		
        log.info("Configuring JweTokenService");
		return new LemonJweService(properties.getJwt().getSecret());
	}

	@Bean
	@ConditionalOnMissingBean(EmailJwtService.class)
	public EmailJwtService emailJwtService(){
		return new LemonEmailJwtService();
	}


}
