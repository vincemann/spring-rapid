package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.validation.CaptchaValidator;
import com.github.vincemann.springrapid.core.RapidCoreProperties;
import com.github.vincemann.springrapid.core.config.RapidControllerAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties
@AutoConfigureBefore(RapidControllerAutoConfiguration.class)
public class LemonGeneralAutoConfiguration {


    /**
     * Spring Lemon related properties
     */
    @ConfigurationProperties(prefix="lemon")
    @ConditionalOnMissingBean(LemonProperties.class)
    @Bean
    public LemonProperties lemonProperties(RapidCoreProperties coreProperties) {
        return new LemonProperties(coreProperties);
    }

    /**
     * Configures CaptchaValidator if missing
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaValidator.class)
    public CaptchaValidator captchaValidator(LemonProperties properties) {
        return new CaptchaValidator(properties);
    }

}
