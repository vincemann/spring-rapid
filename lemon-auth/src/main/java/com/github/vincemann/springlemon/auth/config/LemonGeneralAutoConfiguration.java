package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.util.LemonUtils;
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
//@ComponentScan("com.naturalprogrammer.spring.lemon.auth.properties")
public class LemonGeneralAutoConfiguration {

    public LemonGeneralAutoConfiguration() {
        log.info("Created");
    }

    /**
     * Spring Lemon related properties
     */
    @ConfigurationProperties(prefix="lemon")
    @ConditionalOnMissingBean(LemonProperties.class)
    @Bean
    public LemonProperties lemonProperties() {

        log.info("Configuring LemonProperties");
        return new LemonProperties();
    }

}
