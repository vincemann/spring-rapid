package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.AuthProperties;
import com.github.vincemann.springlemon.auth.domain.RapidAuthAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.token.*;
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
public class RapidTokenServiceAutoConfiguration {


	public RapidTokenServiceAutoConfiguration() {

	}


	/**
	 * Configures JwsTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(JwsTokenService.class)
	public JwsTokenService jwsTokenService(AuthProperties properties) throws JOSEException {
		return new RapidJwsService(properties.getJwt().getSecret());
	}


	/**
	 * Configures JweTokenService if missing
	 */
	@Bean
	@ConditionalOnMissingBean(JweTokenService.class)
	public JweTokenService jweTokenService(AuthProperties properties) throws KeyLengthException {
		return new RapidJweService(properties.getJwt().getSecret());
	}

	@Bean
	@ConditionalOnMissingBean(EmailJwtService.class)
	public EmailJwtService emailJwtService(){
		return new EncryptedEmailJwtService();
	}


	@Bean
	@ConditionalOnMissingBean(AuthorizationTokenService.class)
	public AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService(){
		return new RapidJwtAuthorizationTokenService();
	}


}
