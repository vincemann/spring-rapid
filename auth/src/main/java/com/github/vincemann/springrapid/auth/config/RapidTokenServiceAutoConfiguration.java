package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springlemon.exceptions.config.LemonWebExceptionsAutoConfiguration;
import com.github.vincemann.springrapid.auth.service.token.*;
import org.springframework.context.annotation.Configuration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
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




}
