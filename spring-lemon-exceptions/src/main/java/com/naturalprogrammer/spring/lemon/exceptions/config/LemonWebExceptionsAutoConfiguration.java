package com.naturalprogrammer.spring.lemon.exceptions.config;

import java.util.List;

import com.naturalprogrammer.spring.lemon.exceptions.ErrorResponseComposer;
import com.naturalprogrammer.spring.lemon.exceptions.ExceptionIdMaker;
import com.naturalprogrammer.spring.lemon.exceptions.handlers.AbstractExceptionHandler;
import com.naturalprogrammer.spring.lemon.exceptions.web.DefaultExceptionHandlerControllerAdvice;
import com.naturalprogrammer.spring.lemon.exceptions.web.LemonErrorAttributes;
import com.naturalprogrammer.spring.lemon.exceptions.web.LemonErrorController;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;

@WebConfig
//@AutoConfigureBefore({ValidationAutoConfiguration.class})
@Slf4j
//@ComponentScan(basePackageClasses=AbstractExceptionHandler.class) cant override anymore in this config
public class LemonWebExceptionsAutoConfiguration {


	public LemonWebExceptionsAutoConfiguration() {
		log.info("Created");
	}



	/**
	 * Configures ErrorResponseComposer if missing
	 */	
	@Bean
	@ConditionalOnMissingBean(ErrorResponseComposer.class)
	public <T extends Throwable>
	ErrorResponseComposer<T> errorResponseComposer(List<AbstractExceptionHandler<T>> handlers) {
		
        log.info("Configuring ErrorResponseComposer");       
		return new ErrorResponseComposer<T>(handlers);
	}


	/**
	 * Configures DefaultExceptionHandlerControllerAdvice if missing
	 */
	@Bean
	@ConditionalOnMissingBean(DefaultExceptionHandlerControllerAdvice.class)
	public <T extends Throwable>
	DefaultExceptionHandlerControllerAdvice<T> defaultExceptionHandlerControllerAdvice(
			ErrorResponseComposer<T> errorResponseComposer) {

		log.info("Configuring DefaultExceptionHandlerControllerAdvice");
		return new DefaultExceptionHandlerControllerAdvice<T>(errorResponseComposer);
	}

	/**
	 * Configures an Error Attributes if missing
	 */
	@Bean
	@ConditionalOnMissingBean(ErrorAttributes.class)
	public <T extends Throwable>
	ErrorAttributes errorAttributes(ErrorResponseComposer<T> errorResponseComposer) {

		log.info("Configuring LemonErrorAttributes");
		return new LemonErrorAttributes<T>(errorResponseComposer);
	}

	/**
	 * Configures an Error Controller if missing
	 */
	@Bean
	@ConditionalOnMissingBean(ErrorController.class)
	public ErrorController errorController(ErrorAttributes errorAttributes,
										   ServerProperties serverProperties,
										   List<ErrorViewResolver> errorViewResolvers) {

		log.info("Configuring LemonErrorController");
		return new LemonErrorController(errorAttributes, serverProperties, errorViewResolvers);
	}

}
