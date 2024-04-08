package com.github.vincemann.springlemon.exceptions.config;

import com.github.vincemann.springlemon.exceptions.ExceptionIdMaker;
import com.github.vincemann.springlemon.exceptions.util.LemonExceptionUtils;
import com.github.vincemann.springlemon.exceptions.util.Message;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@AutoConfigureAfter({ValidationAutoConfiguration.class})
public class LemonExceptionUtilsAutoConfiguration {

    public LemonExceptionUtilsAutoConfiguration() {

    }

    @Bean
    public Message messageUtils(MessageSource messageSource){
        return new Message(messageSource);
    }

    /**
     * Configures ExceptionCodeMaker if missing
     *
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionIdMaker.class)
    public ExceptionIdMaker exceptionIdMaker() {

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
    public LemonExceptionUtils lemonExceptionUtils(
            LocalValidatorFactoryBean validator,
            ExceptionIdMaker exceptionIdMaker) {
        return new LemonExceptionUtils(validator, exceptionIdMaker);
    }
}
