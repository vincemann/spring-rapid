package lemon.exceptions.config;

import java.util.List;

import lemon.exceptions.ErrorResponseComposer;
import lemon.exceptions.ExceptionIdMaker;
import lemon.exceptions.web.DefaultExceptionHandlerControllerAdvice;
import lemon.exceptions.web.LemonErrorAttributes;
import lemon.exceptions.web.LemonErrorController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import lemon.exceptions.handlers.AbstractExceptionHandler;
import lemon.exceptions.util.LexUtils;

@Configuration
@AutoConfigureBefore({ValidationAutoConfiguration.class})
@ComponentScan(basePackageClasses=AbstractExceptionHandler.class)
public class LemonExceptionsAutoConfiguration {

	private static final Log log = LogFactory.getLog(LemonExceptionsAutoConfiguration.class);

	public LemonExceptionsAutoConfiguration() {
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
	 * Configures ExceptionCodeMaker if missing
	 */	
	@Bean
	@ConditionalOnMissingBean(ExceptionIdMaker.class)
	public ExceptionIdMaker exceptionIdMaker() {
		
        log.info("Configuring ExceptionIdMaker");
        return ex -> {
        	
        	if (ex == null)
        		return null;
        	
        	return ex.getClass().getSimpleName();
        };
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

	
	/**
	 * Configures LexUtils
	 */
	@Bean
	public LexUtils lexUtils(MessageSource messageSource,
			LocalValidatorFactoryBean validator,
			ExceptionIdMaker exceptionIdMaker) {

        log.info("Configuring LexUtils");       		
		return new LexUtils(messageSource, validator, exceptionIdMaker);
	}
}
