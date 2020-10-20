package com.github.vincemann.springlemon.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springrapid.core.config.RapidJsonAutoConfiguration;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfigurationSource;

@WebConfig
@EnableSpringDataWebSupport
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@AutoConfigureAfter(RapidJsonAutoConfiguration.class)
@AutoConfigureBefore({
	WebMvcAutoConfiguration.class,
	ErrorMvcAutoConfiguration.class,
	SecurityAutoConfiguration.class,
	SecurityFilterAutoConfiguration.class,
	RapidTokenServiceAutoConfiguration.class})
@Slf4j
public class RapidCommonsWebAutoConfiguration {

	/**
	 * For handling JSON vulnerability,
	 * JSON response bodies would be prefixed with
	 * this string.
	 */
	public final static String JSON_PREFIX = ")]}',\n";


	public RapidCommonsWebAutoConfiguration() {

	}
	
    /**
	 * Prefixes JSON responses for JSON vulnerability. Disabled by default.
	 * To enable, add this to your application properties:
	 *     lemon.enabled.json-prefix: true
	 */
	@Bean
	@ConditionalOnProperty(name="lemon.enabled.json-prefix")
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
			ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter =
        		new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setJsonPrefix(JSON_PREFIX);
        
        return converter;
	}

//	@Bean
//	public MapperUtils lmapUtils(ObjectMapper mapper){
//		return new MapperUtils(mapper);
//	}

	/**
	 * Configures LemonCorsConfig if missing and lemon.cors.allowed-origins is provided
	 */
	@Bean
	@ConditionalOnProperty(name="lemon.cors.allowed-origins")
	@ConditionalOnMissingBean(CorsConfigurationSource.class)
	public RapidCorsConfigurationSource corsConfigurationSource(LemonProperties properties) {
		return new RapidCorsConfigurationSource(properties);
	}
	

	



}
