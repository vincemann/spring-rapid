package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * CORS Configuration
 */
@Slf4j
public class RapidCorsConfigurationSource implements CorsConfigurationSource {


	private LemonProperties.Cors cors;

	public RapidCorsConfigurationSource(LemonProperties properties) {
		this.cors = properties.getCors();

	}

	@Override
	public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
		
		CorsConfiguration config = new CorsConfiguration();
		
		config.setAllowCredentials(true);
		config.setAllowedHeaders(Arrays.asList(cors.getAllowedHeaders()));
		config.setAllowedMethods(Arrays.asList(cors.getAllowedMethods()));
		config.setAllowedOrigins(Arrays.asList(cors.getAllowedOrigins()));
		config.setExposedHeaders(Arrays.asList(cors.getExposedHeaders()));
		config.setMaxAge(cors.getMaxAge());
		
		return config;
	}

}
