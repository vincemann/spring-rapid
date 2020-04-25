package com.naturalprogrammer.spring.lemon.exceptions.config;

import com.naturalprogrammer.spring.lemon.exceptions.ExceptionIdMaker;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@Slf4j
@AutoConfigureAfter({ValidationAutoConfiguration.class})
public class LemonExceptionUtilsAutoConfiguration
{

    public LemonExceptionUtilsAutoConfiguration() {
            log.info("Created");
    }

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
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
