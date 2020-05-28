package io.github.vincemann.springlemon.exceptions.config;

import java.util.List;

import io.github.vincemann.springlemon.exceptions.ErrorResponseComposer;
import io.github.vincemann.springlemon.exceptions.handlers.AbstractExceptionHandler;
import io.github.vincemann.springlemon.exceptions.web.DefaultExceptionHandlerControllerAdvice;
import io.github.vincemann.springlemon.exceptions.web.LemonErrorAttributes;
import io.github.vincemann.springlemon.exceptions.web.LemonErrorController;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;

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
